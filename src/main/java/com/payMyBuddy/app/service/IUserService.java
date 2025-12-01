package com.payMyBuddy.app.service;

import com.payMyBuddy.app.dto.UserChangePasswordDto;
import com.payMyBuddy.app.dto.UserRelationDto;
import com.payMyBuddy.app.dto.UserSignInDto;
import com.payMyBuddy.app.dto.UserSignUpDto;
import com.payMyBuddy.app.model.User;

import java.util.Optional;

public interface IUserService {
    void signUp(UserSignUpDto userSignUpDto);

    Optional<User> signIn(UserSignInDto userSignInDto);

    void addRelation(UserRelationDto userRelationDto);

    void changePassword(UserChangePasswordDto userChangePasswordDto);

    boolean emailExists(String email);
}
