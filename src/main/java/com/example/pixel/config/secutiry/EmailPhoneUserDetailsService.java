package com.example.pixel.config.secutiry;

import com.example.pixel.model.User;
import com.example.pixel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailPhoneUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<User> byEmail = userRepository.findByEmailIgnoreCase(login);
        if (byEmail.isPresent()) {
            return byEmail.get();
        }

        Optional<User> byPhone = userRepository.findByPhone(login);
        if (byPhone.isPresent()) {
            return byPhone.get();
        }

        throw new UsernameNotFoundException("User not found with login: " + login);
    }
}
