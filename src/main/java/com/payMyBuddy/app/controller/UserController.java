package com.payMyBuddy.app.controller;

import com.payMyBuddy.app.dto.UserProfileDto;
import com.payMyBuddy.app.dto.UserRelationDto;
import com.payMyBuddy.app.dto.UserSignInDto;
import com.payMyBuddy.app.dto.UserSignUpDto;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.service.IUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Optional;

@Slf4j
@Controller
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/signup")
    public String signUp(
            @Valid @ModelAttribute("userSignUpDto") UserSignUpDto userSignUpDto,
            BindingResult result,
            Model model
    ) {
        log.debug("POST /signup for email:{}", userSignUpDto.email());
        if (userService.emailExists(userSignUpDto.email())) {
            log.warn("email already exists :{}", userSignUpDto.email());
            result.rejectValue("email", "email.exists", "Cet email est déjà utilisé");
        }

        if (result.hasErrors()) {
            log.debug("Validation errors {} for email {}", result.getAllErrors(), userSignUpDto.email());
            return "signup";
        }

        userService.signUp(userSignUpDto);
        model.addAttribute("successMessage", "Inscription réussie");
        log.info("Signup successful for email {}", userSignUpDto.email());
        return "redirect:/signin";
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        log.debug("GET /signup ");
        model.addAttribute("userSignUpDto", new UserSignUpDto("", "", ""));
        return "signup";
    }

    @PostMapping("/signin")
    public String signIn(
            @Valid UserSignInDto userSignInDto,
            BindingResult result,
            Model model,
            HttpSession session
    ) {
        log.debug("POST /signin for email:{}", userSignInDto.email());

        if (result.hasErrors()) {
            log.debug("Validation errors {} for email {}", result.getAllErrors(), userSignInDto.email());
            return "signin";
        }

        Optional<User> user = userService.signIn(userSignInDto);

        if (user.isEmpty()) {
            log.warn("user not find for email {}", userSignInDto.email());
            model.addAttribute("error", "Email ou mot de passe incorrect");
            return "signin";
        }

        session.setAttribute("currentUser", user.get());
        log.info("Signin successful for email {}", userSignInDto.email());
        return "redirect:/transfert";
    }

    @GetMapping("/signin")
    public String signIn(Model model) {
        log.debug("GET /signin");
        model.addAttribute("userSignInDto", new UserSignInDto("", ""));
        return "signin";
    }

    @PostMapping("/addrelation")
    public String addRelation(
            @Valid @ModelAttribute("userRelationDto") UserRelationDto userRelationDto,
            BindingResult result,
            @SessionAttribute("currentUser") User user,
            Model model
    ) {
        log.debug("POST /addrelation userId:{}, dto:{}", user.getId(), userRelationDto);

        if (result.hasErrors()) {
            log.debug("Validation errors {} for user {}", result.getAllErrors(), user.getId());
            return "addrelation";
        }

        try {
            userService.addRelation(userRelationDto, user);
            model.addAttribute("successMessage", "Relation ajoutée");
            log.info("Add relation {} successful for user {}", userRelationDto.email(), user.getId());
        } catch (RuntimeException ex) {
            log.error("addRelation errors:{} for user:{}", ex.getMessage(), user.getId(), ex);
            result.rejectValue("email", "errorEmail", ex.getMessage());
        }

        return "addrelation";
    }

    @GetMapping("/addrelation")
    public String addRelation(Model model) {
        log.debug("GET /addrelation");
        model.addAttribute("userRelationDto", new UserRelationDto(""));
        return "addrelation";
    }

    @PostMapping("/changePassword")
    public String changePassword(
            @Valid @ModelAttribute("userProfileDto") UserProfileDto userProfileDto,
            BindingResult result,
            @SessionAttribute("currentUser") User user,
            Model model
    ) {
        log.debug("POST /changePassword for user:{}", user.getId());

        if (result.hasErrors()) {
            log.debug("Validation errors {} for user {}", result.getAllErrors(), user.getId());
            return "profile";
        }

        userService.changePassword(userProfileDto, user);
        model.addAttribute("successMessage", "Mot de passe modifié");
        log.info("changePassword successful for user {}", user.getId());
        return "profile";
    }

    @GetMapping("/profile")
    public String profile(@SessionAttribute("currentUser") User user, Model model) {
        log.debug("GET /profile for user:{}", user.getId());
        model.addAttribute("userProfileDto", new UserProfileDto(user.getUsername(), user.getEmail(), user.getPassword()));
        return "profile";
    }

    @GetMapping("/signout")
    public String signout(@SessionAttribute("currentUser") User user, HttpSession session) {
        log.debug("GET /signout for user:{}", user.getId());
        session.invalidate();
        return "redirect:/signin";
    }

}
