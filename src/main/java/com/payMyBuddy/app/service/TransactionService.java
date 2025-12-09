package com.payMyBuddy.app.service;

import com.payMyBuddy.app.dto.TransactionTransfertDto;
import com.payMyBuddy.app.exception.MyException;
import com.payMyBuddy.app.model.Transaction;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.repository.ITransactionRepository;
import com.payMyBuddy.app.repository.IUserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private HttpSession session;

    @Override
    public void createPayment(TransactionTransfertDto transactionCreatePaymentDto) {
        var currentUser = (User) session.getAttribute("currentUser");

        var sender = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new MyException("L'utilisateur n'a pas été trouvé"));
        var receiver = userRepository.findById(transactionCreatePaymentDto.receiverId())
                .orElseThrow(() -> new MyException("Le destinataire n'a pas été trouvé "));

        var newTransaction = new Transaction();
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setDescription(transactionCreatePaymentDto.description());
        var amount = Integer.parseInt(transactionCreatePaymentDto.amount().replaceAll("\\D", ""));
        newTransaction.setAmount(amount);
        transactionRepository.save(newTransaction);
    }

    @Override
    public List<Transaction> getAllBySender(Long senderId) {
        return transactionRepository.findBySenderIdOrderByIdDesc(senderId);
    }
}
