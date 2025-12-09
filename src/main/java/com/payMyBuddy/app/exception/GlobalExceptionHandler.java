package com.payMyBuddy.app.exception;

import com.payMyBuddy.app.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(@SessionAttribute("currentUser") User user, Exception ex, Model model) {
        log.error("Unhandled exception caught for user:{} {}", user.getId(), ex.getMessage(), ex);
        model.addAttribute("errorMessage", "Une erreur interne est survenue.");
        return "error";
    }
}
