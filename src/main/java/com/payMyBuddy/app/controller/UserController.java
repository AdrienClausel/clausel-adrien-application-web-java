package com.payMyBuddy.app.controller;

import com.payMyBuddy.app.dto.UserChangePasswordDto;
import com.payMyBuddy.app.dto.UserRelationDto;
import com.payMyBuddy.app.dto.UserSignInDto;
import com.payMyBuddy.app.dto.UserSignUpDto;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.service.IUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

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

        if (userService.emailExists(userSignUpDto.email())) {
            result.rejectValue("email", "email.exists", "Cet email est déjà utilisé");
        }

        if (result.hasErrors()) {
            return "signup";
        }

        userService.signUp(userSignUpDto);
        model.addAttribute("successMessage", "Inscription réussie");
        return "redirect:/signin";
    }

    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("userSignUpDto", new UserSignUpDto("", "", ""));
        return "signup";
    }

    @PostMapping("/signin")
    public String signIn(
            @Valid @ModelAttribute("userSignInDto") UserSignInDto userSignInDto,
            BindingResult result,
            Model model,
            HttpSession session
    ) {
        if (result.hasErrors()) {
            return "signin";
        }

        Optional<User> user = userService.signIn(userSignInDto);

        if (user.isEmpty()) {
            model.addAttribute("error", "Email ou mot de passe incorrect");
            return "signin";
        }

        session.setAttribute("user", user.get());
        return "redirect:/transfert";
    }

    @GetMapping("/signin")
    public String signIn(Model model) {
        model.addAttribute("userSignInDto", new UserSignInDto("", ""));
        return "signin";
    }

    @PostMapping("/addrelation")
    public String addRelation(
            @Valid UserRelationDto userRelationDto,
            @ModelAttribute("currentUser") User user,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            return "addRelation";
        }

        try {
            userService.addRelation(userRelationDto, user);
            model.addAttribute("successMessage", "Relation ajoutée");
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        return "addRelation";
    }

    @GetMapping("/addrelation")
    public String addRelation(@ModelAttribute("currentUser") User user, Model model) {
        model.addAttribute("userRelationDto", new UserRelationDto(""));
        return "addrelation";
    }

    @PostMapping("/changePassword")
    public String changePassword(
            @Valid UserChangePasswordDto userChangePasswordDto,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            return "profile";
        }

        userService.changePassword(userChangePasswordDto);
        model.addAttribute("", "");
        return "profile";
    }


}
