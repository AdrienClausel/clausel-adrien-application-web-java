package com.payMyBuddy.app.controller;

import com.payMyBuddy.app.dto.TransactionTransfertDto;
import com.payMyBuddy.app.exception.MyException;
import com.payMyBuddy.app.model.User;
import com.payMyBuddy.app.service.ITransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ITransactionService transactionService;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setConnections(new ArrayList<>());
    }

    @Test
    void testGetTransfertShouldReturnView() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/transfert")
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("transactionTransfertDto"))
                .andExpect(model().attributeExists("relationUsers"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(view().name("transfert"));
    }

    @Test
    void testPostTransfertInvalidAmount() throws Exception {
        TransactionTransfertDto dto = new TransactionTransfertDto(1L, "Restaurant", "0€");

        mockMvc.perform(MockMvcRequestBuilders.post("/transfert")
                        .param("receiverId", dto.receiverId().toString())
                        .param("description", dto.description())
                        .param("amount", dto.amount())
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("transactionTransfertDto", "amount"))
                .andExpect(view().name("transfert"));

        verify(transactionService, never()).createPayment(any(), any());
    }

    @Test
    void testPostTransfertSuccess() throws Exception {
        TransactionTransfertDto dto = new TransactionTransfertDto(1L, "Restaurant", "10€");

        mockMvc.perform(MockMvcRequestBuilders.post("/transfert")
                        .param("receiverId", dto.receiverId().toString())
                        .param("description", dto.description())
                        .param("amount", dto.amount())
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("transactions"))
                .andExpect(view().name("transfert"));

        verify(transactionService, times(1)).createPayment(any(), any());
    }

    @Test
    void testPostTransfertMyException() throws Exception {

        doThrow(new MyException("Erreur de transfert"))
                .when(transactionService)
                .createPayment(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.post("/transfert")
                        .param("receiverId", "1")
                        .param("description", "Restaurant")
                        .param("amount", "10€")
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", "Erreur de transfert"))
                .andExpect(view().name("transfert"));
    }


    @Test
    void testPostTransfertRuntimeException() throws Exception {

        doThrow(new RuntimeException("unhandled errors"))
                .when(transactionService)
                .createPayment(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.post("/transfert")
                        .param("receiverId", "1")
                        .param("description", "Restaurent")
                        .param("amount", "10€")
                        .sessionAttr("currentUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(model().attribute("errorMessage", "Le transfert n'a pas pu aboutir"))
                .andExpect(view().name("transfert"));
    }

}
