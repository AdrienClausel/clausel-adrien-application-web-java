package com.payMyBuddy.app.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name="username")
    private String username;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @ManyToMany
    @JoinTable(
        name = "user_connection",
        joinColumns = @JoinColumn(name="user_id1"),
        inverseJoinColumns = @JoinColumn(name="user_id2")
    )
    private List<User> connections = new ArrayList<>();
}
