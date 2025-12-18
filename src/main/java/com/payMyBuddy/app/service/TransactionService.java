package com.payMyBuddy.app.service;

import com.payMyBuddy.app.dto.TransactionTransfertDto;
import com.payMyBuddy.app.exception.MyException;
import com.payMyBuddy.app.model.Transaction;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.repository.ITransactionRepository;
import com.payMyBuddy.app.repository.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private IUserRepository userRepository;

    /**
     * Création du paiement d'un utilisateur à une de ses relations
     *
     * @param transactionCreatePaymentDto données du paiement
     * @param currentUser                 utilisateur connecté (payeur)
     */
    @Transactional
    @Override
    public void createPayment(TransactionTransfertDto transactionCreatePaymentDto, User currentUser) {
        var sender = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new MyException("L'utilisateur n'a pas été trouvé"));
        var receiver = userRepository.findById(transactionCreatePaymentDto.receiverId())
                .orElseThrow(() -> new MyException("Le destinataire n'a pas été trouvé "));

        var amount = BigDecimal.valueOf(Integer.parseInt(transactionCreatePaymentDto.amount().replaceAll("\\D", "")));

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new MyException("Le solde est insuffisant");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        userRepository.save(sender);

        receiver.setBalance(receiver.getBalance().add(amount));
        userRepository.save(receiver);

        var newTransaction = new Transaction();
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setDescription(transactionCreatePaymentDto.description());
        newTransaction.setAmount(amount.intValue());
        transactionRepository.save(newTransaction);
    }

    /**
     * Récupère la liste des transactions d'un utilisateur
     *
     * @param senderId identifiant de l'émetteur
     * @return liste de transactions
     */
    @Override
    public List<Transaction> getAllBySender(Long senderId) {
        return transactionRepository.findBySenderIdOrderByIdDesc(senderId);
    }
}
