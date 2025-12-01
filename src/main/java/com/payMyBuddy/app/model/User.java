package com.payMyBuddy.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    @NotBlank(message = "L'identifiant est obligatoire")
    private String username;

    @Column(name = "email")
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    private String email;

    @Column(name = "password")
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    @ManyToMany
    @JoinTable(
            name = "user_connection",
            joinColumns = @JoinColumn(name = "user_id1"),
            inverseJoinColumns = @JoinColumn(name = "user_id2")
    )
    private List<User> connections = new ArrayList<>();

    private BigDecimal balance;
}
