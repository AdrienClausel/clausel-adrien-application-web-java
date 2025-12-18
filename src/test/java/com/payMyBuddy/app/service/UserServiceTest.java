package com.payMyBuddy.app.service;

import com.payMyBuddy.app.dto.UserProfileDto;
import com.payMyBuddy.app.dto.UserRelationDto;
import com.payMyBuddy.app.dto.UserSignInDto;
import com.payMyBuddy.app.dto.UserSignUpDto;
import com.payMyBuddy.app.exception.MyException;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IPasswordService passwordService;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("adrien@test.fr");
        mockUser.setUsername("adrien");
        mockUser.setPassword("12345678");
        mockUser.setConnections(new ArrayList<>());
    }

    @Test
    void signUp_shouldSaveUserWithEncodedPassword() {
        UserSignUpDto dto = new UserSignUpDto("adrien", "adrien@test.fr", "12345678");
        when(passwordService.Encode("12345678")).thenReturn("encodedPassword");

        userService.signUp(dto);

        verify(passwordService).Encode("12345678");
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("adrien@test.fr")
                        && user.getUsername().equals("adrien")
                        && user.getPassword().equals("encodedPassword")
                        && user.getBalance().intValue() == 1000
        ));
    }

    @Test
    void signIn_shouldReturnEmptyOptional_whenEmailNotFound() {
        UserSignInDto dto = new UserSignInDto("", "1234");

        when(userRepository.findByEmailIgnoreCase("")).thenReturn(Optional.empty());

        Optional<User> result = userService.signIn(dto);

        assertTrue(result.isEmpty());
        verify(userRepository).findByEmailIgnoreCase("");
    }

    @Test
    void signIn_shouldReturnUser_whenPasswordMatches() {
        UserSignInDto dto = new UserSignInDto("adrien@test.fr", "12345678");

        when(userRepository.findByEmailIgnoreCase("adrien@test.fr"))
                .thenReturn(Optional.of(mockUser));
        when(passwordService.checkPassword("12345678", "12345678")).thenReturn(true);

        Optional<User> result = userService.signIn(dto);

        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
        verify(passwordService).checkPassword("12345678", "12345678");
    }

    @Test
    void signIn_shouldReturnEmpty_whenPasswordDoesNotMatch() {
        UserSignInDto dto = new UserSignInDto("adrien@test.fr", "12345678");

        when(userRepository.findByEmailIgnoreCase("adrien@test.fr"))
                .thenReturn(Optional.of(mockUser));
        when(passwordService.checkPassword("12345678", "12345678")).thenReturn(false);

        Optional<User> result = userService.signIn(dto);

        assertTrue(result.isEmpty());
    }

    @Test
    void addRelation_shouldThrowException_whenEmailNotFound() {
        UserRelationDto dto = new UserRelationDto("relation@test.fr");

        when(userRepository.findByEmailIgnoreCase("relation@test.fr"))
                .thenReturn(Optional.empty());

        MyException ex = assertThrows(MyException.class, () ->
                userService.addRelation(dto, mockUser)
        );

        assertEquals("Email non trouvé", ex.getMessage());
    }

    @Test
    void addRelation_shouldThrowException_whenUserAddsHimself() {
        UserRelationDto dto = new UserRelationDto("adrien@test.fr");

        when(userRepository.findByEmailIgnoreCase("adrien@test.fr"))
                .thenReturn(Optional.of(mockUser));

        MyException ex = assertThrows(MyException.class, () ->
                userService.addRelation(dto, mockUser)
        );

        assertEquals("Impossible de se connecter à soi-même", ex.getMessage());
    }

    @Test
    void addRelation_shouldAddUserToConnections_andSave() {
        User newRelationUser = new User();
        newRelationUser.setId(2L);
        newRelationUser.setEmail("relation@test.fr");

        UserRelationDto dto = new UserRelationDto("relation@test.fr");

        when(userRepository.findByEmailIgnoreCase("relation@test.fr"))
                .thenReturn(Optional.of(newRelationUser));

        userService.addRelation(dto, mockUser);

        assertTrue(mockUser.getConnections().contains(newRelationUser));
        verify(userRepository).save(mockUser);
    }

    @Test
    void changePassword_shouldEncodeAndSave() {
        UserProfileDto dto = new UserProfileDto("adrien", "adrien@test.fr", "newPassword");

        when(userRepository.findById(any())).thenReturn(Optional.of(mockUser));
        when(passwordService.Encode("newPassword")).thenReturn("encodedNewPassword");

        userService.changePassword(dto, mockUser);

        assertEquals("encodedNewPassword", mockUser.getPassword());
        verify(userRepository).save(mockUser);
    }

    @Test
    void emailExists_shouldReturnTrue_whenExists() {
        when(userRepository.existsByEmail("adrien@test.fr")).thenReturn(true);

        assertTrue(userService.emailExists("adrien@test.fr"));
    }

    @Test
    void emailExists_shouldReturnFalse_whenDoesNotExist() {
        when(userRepository.existsByEmail("adrien@test.fr")).thenReturn(false);

        assertFalse(userService.emailExists("adrien@test.fr"));
    }
}
