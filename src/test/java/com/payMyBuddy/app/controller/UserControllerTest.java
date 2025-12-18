package com.payMyBuddy.app.controller;

import com.payMyBuddy.app.dto.UserProfileDto;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("adrien");
        mockUser.setEmail("adrien@test.fr");
        mockUser.setPassword("12345678");
        mockUser.setConnections(new ArrayList<>());
    }

    @Test
    void testGetSignup() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/signup"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userSignUpDto"))
                .andExpect(view().name("signup"));
    }

    @Test
    void testPostSignup_EmailExists() throws Exception {
        when(userService.emailExists("adrien@test.fr")).thenReturn(true);

        mockMvc.perform(post("/signup")
                        .param("email", "adrien@test.fr")
                        .param("username", "adrien")
                        .param("password", "123456789"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("userSignUpDto", "email"))
                .andExpect(view().name("signup"));

        verify(userService, never()).signUp(any());
    }

    @Test
    void testPostSignup_InvalidDTO() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "")
                        .param("username", "")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("userSignUpDto"))
                .andExpect(view().name("signup"));

        verify(userService, never()).signUp(any());
    }

    @Test
    void testPostSignup_Success() throws Exception {
        when(userService.emailExists("adrien@test.fr")).thenReturn(false);

        mockMvc.perform(post("/signup")
                        .param("email", "adrien@test.fr")
                        .param("username", "adrien")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));

        verify(userService, times(1)).signUp(any());
    }

    @Test
    void testGetSignin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/signin"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userSignInDto"))
                .andExpect(view().name("signin"));
    }

    @Test
    void testPostSignin_InvalidDTO() throws Exception {
        mockMvc.perform(post("/signin")
                        .param("email", "")     // vide → @NotBlank
                        .param("password", ""))  // vide
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("userSignInDto"))
                .andExpect(view().name("signin"));

        verify(userService, never()).signIn(any());
    }

    @Test
    void testPostSignin_InvalidCredentials() throws Exception {

        when(userService.signIn(any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/signin")
                        .param("email", "adrien@test.fr")
                        .param("password", "12345678"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Email ou mot de passe incorrect"))
                .andExpect(view().name("signin"));

        verify(userService).signIn(any());
    }

    @Test
    void testPostSignin_Success() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("adrien@test.com");
        user.setUsername("adrien");

        when(userService.signIn(any())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/signin")
                        .param("email", "adrien@test.fr")
                        .param("password", "12345678"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert"))
                .andExpect(request().sessionAttribute("currentUser", user));

        verify(userService).signIn(any());
    }

    @Test
    void testGetAddRelation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/addrelation")
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userRelationDto"))
                .andExpect(view().name("addrelation"));
    }

    @Test
    void testPostAddRelation_InvalidDTO() throws Exception {
        mockMvc.perform(post("/addrelation")
                        .param("email", "")  // DTO invalide
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("userRelationDto"))
                .andExpect(view().name("addrelation"));

        verify(userService, never()).addRelation(any(), any());
    }

    @Test
    void testPostAddRelation_ServiceError() throws Exception {
        doThrow(new RuntimeException("Email non trouvé"))
                .when(userService)
                .addRelation(any(), any());

        mockMvc.perform(post("/addrelation")
                        .param("email", "relation@test.fr")
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("userRelationDto", "email", "errorEmail"))
                .andExpect(view().name("addrelation"));

        verify(userService).addRelation(any(), any());
    }

    @Test
    void testPostAddRelation_Success() throws Exception {
        mockMvc.perform(post("/addrelation")
                        .param("email", "relation@test.fr")
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("successMessage"))
                .andExpect(model().attribute("successMessage", "Relation ajoutée"))
                .andExpect(view().name("addrelation"));

        verify(userService, times(1)).addRelation(any(), any());
    }

    @Test
    void testGetProfile() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/profile")
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userProfileDto"))
                .andExpect(view().name("profile"));
    }

    @Test
    void testPostChangePasswordValidationError() throws Exception {


        mockMvc.perform(post("/changePassword")
                        .param("username", "")
                        .param("email", "invalid-email")
                        .param("password", "")
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("userProfileDto"))
                .andExpect(view().name("profile"));

        verify(userService, never()).changePassword(any(), any());
    }

    @Test
    void testPostChangePasswordSuccess() throws Exception {
        mockMvc.perform(post("/changePassword")
                        .param("username", "adrien")
                        .param("email", "adrien@test.fr")
                        .param("password", "newPassword")
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("successMessage"))
                .andExpect(view().name("profile"));

        verify(userService).changePassword(any(UserProfileDto.class), eq(mockUser));
    }

    @Test
    void testSignout_shouldInvalidateSession_andRedirectToSignin() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", mockUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/signout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));

        assertTrue(session.isInvalid());
    }
}
