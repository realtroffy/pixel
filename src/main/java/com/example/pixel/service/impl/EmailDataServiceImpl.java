package com.example.pixel.service.impl;

import com.example.pixel.dto.EmailDataRequestDto;
import com.example.pixel.dto.EmailDataResponseDto;
import com.example.pixel.mapper.EmailDataMapper;
import com.example.pixel.model.EmailData;
import com.example.pixel.model.User;
import com.example.pixel.repository.EmailDataRepository;
import com.example.pixel.service.EmailDataService;
import com.example.pixel.util.Authenticator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailDataServiceImpl implements EmailDataService {

    private final EmailDataRepository emailDataRepository;
    private final Authenticator authenticator;

    @Override
    @Transactional
    public EmailDataResponseDto create(EmailDataRequestDto emailDataRequestDto) {
        User currentUser = authenticator.findByUserIdFromJwtToken();
        String newEmail = emailDataRequestDto.email();

        Optional<EmailData> existingEmail = emailDataRepository.findByEmail(newEmail);
        if (existingEmail.isPresent()) {
            User owner = existingEmail.get().getUser();
            if (!owner.getId().equals(currentUser.getId())) {
                throw new IllegalArgumentException("Email is already in use by another user.");
            } else {
                throw new IllegalArgumentException("You already have this email added.");
            }
        }

        EmailData newEmailData = new EmailData();
        newEmailData.setEmail(newEmail);
        newEmailData.setUser(currentUser);

        currentUser.getEmails().add(newEmailData);
        emailDataRepository.save(newEmailData);

        return EmailDataMapper.toEmailDataResponseDto(newEmailData);
    }

    @Override
    @Transactional
    public EmailDataResponseDto update(Long emailId, EmailDataRequestDto emailDataRequestDto) {
        User currentUser = authenticator.findByUserIdFromJwtToken();
        String newEmail = emailDataRequestDto.email();

        EmailData existing = emailDataRepository.findById(emailId)
                .orElseThrow(() -> new IllegalArgumentException("Email not found with id: " + emailId));

        if (!existing.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to update this email.");
        }

        if (existing.getEmail().equals(newEmail)) {
            return EmailDataMapper.toEmailDataResponseDto(existing);
        }

        Optional<EmailData> emailConflict = emailDataRepository.findByEmail(newEmail);
        if (emailConflict.isPresent() && !emailConflict.get().getId().equals(existing.getId())) {
            throw new IllegalArgumentException("Email is already in use by another user.");
        }

        existing.setEmail(newEmail);
        emailDataRepository.save(existing);

        return EmailDataMapper.toEmailDataResponseDto(existing);
    }

    @Override
    @Transactional
    public void delete(Long emailId) {
        User currentUser = authenticator.findByUserIdFromJwtToken();

        EmailData emailData = emailDataRepository.findById(emailId)
                .orElseThrow(() -> new IllegalArgumentException("Email not found with id: " + emailId));

        if (!emailData.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to delete this email.");
        }

        long remainingEmails = currentUser.getEmails().stream()
                .filter(email -> !email.getId().equals(emailId))
                .count();

        boolean hasAnotherEmail = remainingEmails > 0;
        boolean hasAnyPhone = currentUser.getPhones() != null && !currentUser.getPhones().isEmpty();

        if (!hasAnotherEmail && !hasAnyPhone) {
            throw new IllegalArgumentException("You cannot delete the only email without having at least one phone number.");
        }

        currentUser.getEmails().removeIf(e -> e.getId().equals(emailId));
        emailDataRepository.delete(emailData);
    }

}
