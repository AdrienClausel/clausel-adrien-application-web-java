package com.payMyBuddy.app.service;

import com.payMyBuddy.app.dto.TransactionTransfertDto;

public interface ITransactionService {

    void createPayment(TransactionTransfertDto transactionCreatePaymentDto);
}
