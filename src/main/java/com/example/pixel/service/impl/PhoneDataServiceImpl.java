package com.example.pixel.service.impl;

import com.example.pixel.dto.PhoneDataRequestDto;
import com.example.pixel.dto.PhoneDataResponseDto;
import com.example.pixel.mapper.PhoneDataMapper;
import com.example.pixel.model.PhoneData;
import com.example.pixel.model.User;
import com.example.pixel.repository.PhoneDataRepository;
import com.example.pixel.service.PhoneDataService;
import com.example.pixel.util.Authenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PhoneDataServiceImpl implements PhoneDataService {

    private final PhoneDataRepository phoneDataRepository;
    private final Authenticator authenticator;

    @Override
    @Transactional
    public PhoneDataResponseDto create(PhoneDataRequestDto phoneDataRequestDto) {
        User currentUser = authenticator.findByUserIdFromJwtToken();
        String newPhone = phoneDataRequestDto.phone();

        Optional<PhoneData> existingPhone = phoneDataRepository.findByPhone(newPhone);
        if (existingPhone.isPresent()) {
            User owner = existingPhone.get().getUser();
            if (!owner.getId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Phone is already in use by another user.");
            } else {
                throw new IllegalArgumentException("You already have this phone added.");
            }
        }

        PhoneData newPhoneData = new PhoneData();
        newPhoneData.setPhone(newPhone);
        newPhoneData.setUser(currentUser);

        currentUser.getPhones().add(newPhoneData);
        phoneDataRepository.save(newPhoneData);

        return PhoneDataMapper.toPhoneDataResponseDto(newPhoneData);
    }

    @Override
    @Transactional
    public PhoneDataResponseDto update(Long phoneId, PhoneDataRequestDto phoneDataRequestDto) {
        User currentUser = authenticator.findByUserIdFromJwtToken();
        String newPhone = phoneDataRequestDto.phone();

        PhoneData existing = phoneDataRepository.findById(phoneId)
                .orElseThrow(() -> new IllegalArgumentException("Phone not found with id: " + phoneId));

        if (!existing.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to update this phone.");
        }

        if (existing.getPhone().equals(newPhone)) {
            return PhoneDataMapper.toPhoneDataResponseDto(existing);
        }

        Optional<PhoneData> phoneConflict = phoneDataRepository.findByPhone(newPhone);
        if (phoneConflict.isPresent() && !phoneConflict.get().getId().equals(existing.getId())) {
            throw new IllegalArgumentException("Phone is already in use by another user.");
        }

        existing.setPhone(newPhone);
        phoneDataRepository.save(existing);

        return PhoneDataMapper.toPhoneDataResponseDto(existing);
    }

    @Override
    @Transactional
    public void delete(Long phoneId) {
        User currentUser = authenticator.findByUserIdFromJwtToken();

        PhoneData phoneData = phoneDataRepository.findById(phoneId)
                .orElseThrow(() -> new IllegalArgumentException("Phone not found with id: " + phoneId));

        if (!phoneData.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to delete this phone.");
        }

        long remainingPhones = currentUser.getPhones().stream()
                .filter(phone -> !phone.getId().equals(phoneId))
                .count();

        boolean hasAnotherPhone = remainingPhones > 0;
        boolean hasAnyEmail = currentUser.getEmails() != null && !currentUser.getEmails().isEmpty();

        if (!hasAnotherPhone && !hasAnyEmail) {
            throw new IllegalArgumentException("You cannot delete the only phone without having at least one email.");
        }

        currentUser.getPhones().removeIf(p -> p.getId().equals(phoneId));
        phoneDataRepository.delete(phoneData);
    }
}
