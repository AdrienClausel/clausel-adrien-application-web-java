package com.payMyBuddy.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignUpDto(
        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        String username,

        @Email(message = "Email invalide")
        @NotBlank(message = "L'email est obligatoire")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères")
        String password
) {
}
