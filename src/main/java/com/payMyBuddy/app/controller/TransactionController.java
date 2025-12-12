package com.payMyBuddy.app.controller;

import com.payMyBuddy.app.dto.TransactionTransfertDto;
import com.payMyBuddy.app.exception.MyException;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.service.ITransactionService;
import com.payMyBuddy.app.service.IUserService;
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

import java.util.Objects;

@Slf4j
@Controller
public class TransactionController {

    @Autowired
    private ITransactionService transactionService;

    @Autowired
    private IUserService userService;

    @PostMapping("/transfert")
    public String transfert(
            @Valid @ModelAttribute("transactionTransfertDto") TransactionTransfertDto transactionTransfertDto,
            BindingResult result,
            @SessionAttribute("currentUser") User user,
            Model model
    ) {
        log.debug("POST /transfert userId:{}, dto:{}", user.getId(), transactionTransfertDto);

        if (Objects.equals(transactionTransfertDto.amount(), "0€") || Objects.equals(transactionTransfertDto.amount(), "€")) {
            log.warn("Invalid amount {} for user {}", transactionTransfertDto.amount(), user.getId());
            result.rejectValue("amount", "amountInvalid", "Le montant doit être supérieur à 0");
        }

        if (result.hasErrors()) {
            log.debug("Validation errors {} for user {}", result.getAllErrors(), user.getId());
            loadTransfertModel(model, user);
            return "transfert";
        }

        try {
            transactionService.createPayment(transactionTransfertDto, user);
            log.info("Transfert successful for user {}", user.getId());
        } catch (MyException ex) {
            log.error("Transfert errors:{} for user:{}", ex.getMessage(), user.getId(), ex);
            model.addAttribute("errorMessage", ex.getMessage());
        } catch (RuntimeException ex) {
            log.error("Transfert errors:{} for user:{}", ex.getMessage(), user.getId(), ex);
            model.addAttribute("errorMessage", "Le transfert n'a pas pu aboutir");
        }

        loadTransfertModel(model, user);
        return "transfert";
    }

    @GetMapping("/transfert")
    public String transfert(@SessionAttribute("currentUser") User user, Model model) {
        log.debug("GET /transfert user:{}", user.getId());

        model.addAttribute("transactionTransfertDto", new TransactionTransfertDto(0L, "", "0€"));
        loadTransfertModel(model, user);
        return "transfert";
    }

    private void loadTransfertModel(Model model, User user) {
        model.addAttribute("relationUsers", user.getConnections());
        model.addAttribute("transactions", transactionService.getAllBySender(user.getId()));
    }
}
