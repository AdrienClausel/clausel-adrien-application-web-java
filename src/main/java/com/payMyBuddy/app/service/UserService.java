package com.payMyBuddy.app.service;

import com.payMyBuddy.app.dto.UserProfileDto;
import com.payMyBuddy.app.dto.UserRelationDto;
import com.payMyBuddy.app.dto.UserSignInDto;
import com.payMyBuddy.app.dto.UserSignUpDto;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IPasswordService passwordService;

    @Override
    public void signUp(UserSignUpDto userSignUpDto) {
        log.info("Creating new user for email:{}", userSignUpDto.email());

        var user = new User();
        user.setEmail(userSignUpDto.email());
        user.setPassword(passwordService.Encode(userSignUpDto.password()));
        user.setUsername(userSignUpDto.username());
        user.setBalance(BigDecimal.valueOf(1000));
        userRepository.save(user);

        log.info("User created successfully, id:{}, for email:{}", user.getId(), user.getEmail());
    }

    @Override
    public Optional<User> signIn(UserSignInDto userSignInDto) {
        log.info("Signing user for email:{}", userSignInDto.email());
        Optional<User> user = userRepository.findByEmailIgnoreCase(userSignInDto.email());
        if (user.isEmpty()) {
            log.info("user not found for email:{}", userSignInDto.email());
            return Optional.empty();
        }

        if (passwordService.checkPassword(userSignInDto.password(), user.get().getPassword())) {
            log.info("user signed successfully for email:{}", user.get().getEmail());
            return user;
        } else {
            log.info("user credentials invalid for email:{}", userSignInDto.email());
            return Optional.empty();
        }
    }

    @Override
    public void addRelation(UserRelationDto userRelationDto, User user) {
        log.info("adding relation {} to email:{}", userRelationDto.email(), user.getEmail());
        User newRelationUser = userRepository.findByEmailIgnoreCase(userRelationDto.email())
                .orElseThrow(() -> new RuntimeException("Email non trouvé"));

        if (user.getId().equals(newRelationUser.getId())) {
            throw new RuntimeException("Impossible de se connecter à soi-même");
        }

        user.getConnections().add(newRelationUser);
        userRepository.save(user);
    }

    @Override
    public void changePassword(UserProfileDto userProfileDto, User user) {
        user.setPassword(passwordService.Encode(userProfileDto.password()));
        userRepository.save(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
