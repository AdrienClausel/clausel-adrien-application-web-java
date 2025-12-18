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

/**
 * Contrôleur gérant les actions liés à l'utilisateur
 */
@Slf4j
@Controller
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * Enregistrement d'un utilisateur
     *
     * @param userSignUpDto données d'enregistrement d'un utilisateur
     * @param result        résultat de la validation
     * @param model         données transmises à la vue
     * @return vue signup ou signin
     */
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

    /**
     * Récupère la vue d'enregistrement d'un utilisateur
     *
     * @param model données d'un utilisateur
     * @return vue signup
     */
    @GetMapping("/signup")
    public String signUp(Model model) {
        log.debug("GET /signup ");
        model.addAttribute("userSignUpDto", new UserSignUpDto("", "", ""));
        return "signup";
    }

    /**
     * Connexion d'un utilisateur
     *
     * @param userSignInDto données de connexion
     * @param result        résultat de la validation
     * @param model         données transmises à la vue
     * @param session       données de session
     * @return vue signin ou transfert
     */
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

    /**
     * Récupère la vue de connexion
     *
     * @param model données transmises à la vue
     * @return vue signin
     */
    @GetMapping("/signin")
    public String signIn(Model model) {
        log.debug("GET /signin");
        model.addAttribute("userSignInDto", new UserSignInDto("", ""));
        return "signin";
    }

    /**
     * Ajout d'une relation
     *
     * @param userRelationDto données de la relation
     * @param result          résultat de la validation
     * @param user            utilisateur connecté
     * @param model           données transmises à la vue
     * @return vue addrelation
     */
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

    /**
     * Récupère la vue pour ajouter une relation
     *
     * @param model données transmises à la vue
     * @return vue addrelation
     */
    @GetMapping("/addrelation")
    public String addRelation(Model model) {
        log.debug("GET /addrelation");
        model.addAttribute("userRelationDto", new UserRelationDto(""));
        return "addrelation";
    }

    /**
     * Changement d'un mot de passe
     *
     * @param userProfileDto données du profil
     * @param result         résultat de la validation
     * @param user           utilisateur connecté
     * @param model          données transmises à la vue
     * @return vue profile
     */
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

    /**
     * Récupère la vue du profil
     *
     * @param user  utilisateur connecté
     * @param model données transmises à la vue
     * @return vue profile
     */
    @GetMapping("/profile")
    public String profile(@SessionAttribute("currentUser") User user, Model model) {
        log.debug("GET /profile for user:{}", user.getId());
        model.addAttribute("userProfileDto", new UserProfileDto(user.getUsername(), user.getEmail(), user.getPassword()));
        return "profile";
    }

    /**
     * Récupère la vue de déconnexion
     *
     * @param user    utilisateur connecté
     * @param session session en cours
     * @return vue signin
     */
    @GetMapping("/signout")
    public String signout(@SessionAttribute("currentUser") User user, HttpSession session) {
        log.debug("GET /signout for user:{}", user.getId());
        session.invalidate();
        return "redirect:/signin";
    }

}
