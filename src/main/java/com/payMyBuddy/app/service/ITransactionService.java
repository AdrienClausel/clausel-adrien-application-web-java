package com.payMyBuddy.app.service;

import com.payMyBuddy.app.dto.TransactionTransfertDto;
import com.payMyBuddy.app.model.Transaction;
import com.payMyBuddy.app.model.User;

import java.util.List;

public interface ITransactionService {

    void createPayment(TransactionTransfertDto transactionCreatePaymentDto, User currentUser);

    List<Transaction> getAllBySender(Long senderId);
}
