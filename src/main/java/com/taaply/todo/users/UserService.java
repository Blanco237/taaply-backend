package com.taaply.todo.users;

import com.taaply.todo.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public UserEntity findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Transactional
    public UserEntity createUser(String email, String name) {
        UserEntity user = UserEntity.builder()
                .email(email)
                .name(name)
                .build();

        UserEntity savedUser = userRepository.save(user);
        log.info("Created new user: {} ({})", savedUser.getEmail(), savedUser.getId());

        return savedUser;
    }

    @Transactional
    public UserEntity findOrCreate(String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> createUser(email, name));
    }
}
