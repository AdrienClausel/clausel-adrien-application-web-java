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

import java.math.BigDecimal;

@Controller
public class TransactionController {

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IUserService userService;

    @PostMapping("/transfert")
    public String transfert(
            @Valid TransactionTransfertDto transactionCreatePaymentDto,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            return "transfert";
        }

        transactionService.createPayment(transactionCreatePaymentDto);
        model.addAttribute("successMessage", "Transfert réussi");
        return "transfert";
    }

    @GetMapping("/transfert")
    public String transfert(@ModelAttribute("currentUser") User user, Model model) {
        model.addAttribute("transactionTransfertDto", new TransactionTransfertDto(user.getId(), 0L, "", BigDecimal.valueOf(0.00)));
        model.addAttribute("relationUsers", user.getConnections());
        return "transfert";
    }
}
