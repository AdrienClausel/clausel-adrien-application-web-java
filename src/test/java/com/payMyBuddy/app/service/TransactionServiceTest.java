package com.payMyBuddy.app.service;

import com.payMyBuddy.app.dto.TransactionTransfertDto;
import com.payMyBuddy.app.exception.MyException;
import com.payMyBuddy.app.model.Transaction;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.repository.ITransactionRepository;
import com.payMyBuddy.app.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private ITransactionRepository transactionRepository;

    @Mock
    private IUserRepository userRepository;


    @Test
    void createPayment_shouldSaveTransaction_whenUsersExist() {
        User sender = new User();
        sender.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        TransactionTransfertDto dto = new TransactionTransfertDto(2L, "Restaurant", "100€");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));

        transactionService.createPayment(dto, sender);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals(sender, savedTransaction.getSender());
        assertEquals(receiver, savedTransaction.getReceiver());
        assertEquals(100, savedTransaction.getAmount());
        assertEquals("Restaurant", savedTransaction.getDescription());
    }

    @Test
    void createPayment_shouldThrowException_whenSenderNotFound() {
        User sender = new User();
        sender.setId(1L);
        TransactionTransfertDto dto = new TransactionTransfertDto(2L, "Restaurant", "100€");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        MyException exception = assertThrows(MyException.class, () -> transactionService.createPayment(dto, sender));
        assertEquals("L'utilisateur n'a pas été trouvé", exception.getMessage());
    }

    @Test
    void createPayment_shouldThrowException_whenReceiverNotFound() {
        User sender = new User();
        sender.setId(1L);
        TransactionTransfertDto dto = new TransactionTransfertDto(2L, "Restaurant", "100€");

        when(userRepository.findById(1L)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        MyException exception = assertThrows(MyException.class, () -> transactionService.createPayment(dto, sender));
        assertEquals("Le destinataire n'a pas été trouvé ", exception.getMessage());
    }

    @Test
    void getAllBySender_shouldReturnTransactions() {
        Long senderId = 1L;
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());
        when(transactionRepository.findBySenderIdOrderByIdDesc(senderId)).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllBySender(senderId);

        assertEquals(transactions, result);
    }
}
