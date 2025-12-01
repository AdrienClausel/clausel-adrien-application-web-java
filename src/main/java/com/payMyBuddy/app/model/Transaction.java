package com.payMyBuddy.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private User sender;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    @NotBlank(message = "Le destinataire est obligatoire")
    private User receiver;

    @Column(name = "description")
    @NotBlank(message = "La description est obligatoire")
    private String description;

    @Column(name = "amount")
    @NotBlank(message = "Le montant est obligatoire")
    private BigDecimal amount;
}
