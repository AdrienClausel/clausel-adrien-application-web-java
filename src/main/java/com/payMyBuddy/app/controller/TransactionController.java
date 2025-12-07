package com.payMyBuddy.app.controller;

import com.payMyBuddy.app.dto.TransactionTransfertDto;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.service.ITransactionService;
import com.payMyBuddy.app.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Objects;

@Controller
public class TransactionController {

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IUserService userService;

    @PostMapping("/transfert")
    public String transfert(
            @Valid @ModelAttribute("transactionTransfertDto") TransactionTransfertDto transactionCreatePaymentDto,
            BindingResult result,
            @SessionAttribute("currentUser") User user,
            Model model
    ) {
        if (Objects.equals(transactionCreatePaymentDto.amount(), "0€") || Objects.equals(transactionCreatePaymentDto.amount(), "€")) {
            result.rejectValue("amount", "amountInvalid", "Le montant doit être supérieur à 0");
        }

        if (result.hasErrors()) {
            loadTransfertModel(model, user);
            return "transfert";
        }

        transactionService.createPayment(transactionCreatePaymentDto);
        return "redirect:/transfert";
    }

    @GetMapping("/transfert")
    public String transfert(@SessionAttribute("currentUser") User user, Model model) {
        model.addAttribute("transactionTransfertDto", new TransactionTransfertDto(0L, "", "0€"));
        loadTransfertModel(model, user);
        return "transfert";
    }

    private void loadTransfertModel(Model model, User user) {
        model.addAttribute("relationUsers", user.getConnections());
        model.addAttribute("transactions", transactionService.getAllBySender(user.getId()));
    }
}
