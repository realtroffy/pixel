package com.example.pixel.service.impl;

import com.example.pixel.dto.PageResponseDto;
import com.example.pixel.dto.UserResponseDto;
import com.example.pixel.dto.UserSearchFilterDto;
import com.example.pixel.mapper.UserMapper;
import com.example.pixel.model.EmailData;
import com.example.pixel.model.PhoneData;
import com.example.pixel.model.User;
import com.example.pixel.repository.EmailDataRepository;
import com.example.pixel.repository.PhoneDataRepository;
import com.example.pixel.repository.UserRepository;
import com.example.pixel.service.UserService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String FIELD_DATE_OF_BIRTH = "dateOfBirth";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_PHONES = "phones";
    private static final String FIELD_PHONE = "phone";
    private static final String FIELD_EMAILS = "emails";
    private static final String FIELD_EMAIL = "email";

    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;

    @Override
    @Transactional
    public PageResponseDto<UserResponseDto> searchUsers(UserSearchFilterDto filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<User> spec = buildUserSpecification(filter);

        Page<User> userPage = userRepository.findAll(spec, pageable);
        List<User> users = userPage.getContent();

        if (users.isEmpty()) {
            return new PageResponseDto<>(List.of(), page, size, 0, 0);
        }

        List<UserResponseDto> dtoList = convertToDtoList(users);

        return new PageResponseDto<>(
                dtoList,
                page,
                size,
                userPage.getTotalElements(),
                userPage.getTotalPages()
        );
    }

    private Specification<User> buildUserSpecification(UserSearchFilterDto filter) {
        Specification<User> spec = (root, query, cb) -> cb.conjunction();

        if (filter.getDateOfBirth() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThan(root.get(FIELD_DATE_OF_BIRTH), filter.getDateOfBirth()));
        }

        if (filter.getPhone() != null && !filter.getPhone().isBlank()) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                Join<User, PhoneData> phoneJoin = root.join(FIELD_PHONES, JoinType.LEFT);
                return cb.equal(phoneJoin.get(FIELD_PHONE), filter.getPhone());
            });
        }

        if (filter.getName() != null && !filter.getName().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get(FIELD_NAME), filter.getName() + "%"));
        }

        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                Join<User, EmailData> emailJoin = root.join(FIELD_EMAILS, JoinType.LEFT);
                return cb.equal(emailJoin.get(FIELD_EMAIL), filter.getEmail());
            });
        }

        return spec;
    }

    private List<UserResponseDto> convertToDtoList(List<User> users) {
        List<Long> userIds = users.stream()
                .map(User::getId)
                .toList();

        List<EmailData> allEmails = emailDataRepository.findByUserIdIn(userIds);
        List<PhoneData> allPhones = phoneDataRepository.findByUserIdIn(userIds);

        Map<Long, List<String>> emailsMap = allEmails.stream()
                .collect(Collectors.groupingBy(
                        ed -> ed.getUser().getId(),
                        Collectors.mapping(EmailData::getEmail, Collectors.toList())
                ));

        Map<Long, List<String>> phonesMap = allPhones.stream()
                .collect(Collectors.groupingBy(
                        pd -> pd.getUser().getId(),
                        Collectors.mapping(PhoneData::getPhone, Collectors.toList())
                ));

        return users.stream()
                .map(user -> UserMapper.toUserResponseDto(user, emailsMap, phonesMap))
                .toList();
    }
}
