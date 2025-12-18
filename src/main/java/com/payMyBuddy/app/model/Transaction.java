package com.payMyBuddy.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
    @NotNull(message = "L'émetteur  est obligatoire")
    private User sender;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    @NotNull(message = "Le destinataire est obligatoire")
    private User receiver;

    @Column(name = "description")
    @NotBlank(message = "La description est obligatoire")
    private String description;

    @Column(name = "amount")
    @NotNull(message = "Le montant est obligatoire")
    private int amount;

    public String getFormatedAmount() {
        return String.format("%d €", amount);
    }
}
