package com.payMyBuddy.app.service;

import com.payMyBuddy.app.dto.UserChangePasswordDto;
import com.payMyBuddy.app.dto.UserRelationDto;
import com.payMyBuddy.app.dto.UserSignInDto;
import com.payMyBuddy.app.dto.UserSignUpDto;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IPasswordService passwordService;

    @Override
    public void signUp(UserSignUpDto userSignUpDto) {
        var user = new User();
        user.setEmail(userSignUpDto.email());
        user.setPassword(passwordService.Encode(userSignUpDto.password()));
        user.setUsername(userSignUpDto.username());
        user.setBalance(BigDecimal.valueOf(1000));
        userRepository.save(user);
    }

    @Override
    public Optional<User> signIn(UserSignInDto userSignInDto) {
        Optional<User> user = userRepository.findByEmailIgnoreCase(userSignInDto.email());
        if (user.isEmpty()) {
            return Optional.empty();
        }

        if (passwordService.checkPassword(userSignInDto.password(), user.get().getPassword())) {
            return user;
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void addRelation(UserRelationDto userRelationDto) {

    }

    @Override
    public void changePassword(UserChangePasswordDto userChangePasswordDto) {

    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
