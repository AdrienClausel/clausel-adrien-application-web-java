package com.payMyBuddy.app.service;

import com.payMyBuddy.app.dto.TransactionTransfertDto;
import com.payMyBuddy.app.model.Transaction;

import java.util.List;

public interface ITransactionService {

    void createPayment(TransactionTransfertDto transactionCreatePaymentDto);

    List<Transaction> getAllBySender(Long senderId);
}
