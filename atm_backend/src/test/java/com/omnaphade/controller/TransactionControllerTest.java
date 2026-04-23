package com.omnaphade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnaphade.dtos.CreateTransactionRequest;
import com.omnaphade.dtos.TransactionDTO;
import com.omnaphade.entites.TransactionType;
import com.omnaphade.service.ITransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionDTO transactionDTO;
    private CreateTransactionRequest createRequest;

    @BeforeEach
    void setUp() {
        transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionId(1L);
        transactionDTO.setAccountId(1L);
        transactionDTO.setAmount(500.0);
        transactionDTO.setType(TransactionType.DEPOSIT);
        transactionDTO.setTimestamp(LocalDateTime.now());
        transactionDTO.setReferenceId("ref123");

        createRequest = new CreateTransactionRequest();
        createRequest.setAccountId(1L);
        createRequest.setAmount(BigDecimal.valueOf(500.0));
        createRequest.setType(TransactionType.DEPOSIT);
        createRequest.setReferenceId("ref123");
    }

    @Test
    void testGetAllTransactions_Success() throws Exception {
        List<TransactionDTO> transactions = Arrays.asList(transactionDTO);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].amount").value(500.0));

        verify(transactionService, times(1)).getAllTransactions();
    }

    @Test
    void testGetTransactionById_Success() throws Exception {
        when(transactionService.getTransactionById(1L)).thenReturn(transactionDTO);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(500.0));

        verify(transactionService, times(1)).getTransactionById(1L);
    }

    @Test
    void testGetTransactionById_NotFound() throws Exception {
        when(transactionService.getTransactionById(1L)).thenThrow(new RuntimeException("Transaction not found"));

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isBadRequest());

        verify(transactionService, times(1)).getTransactionById(1L);
    }

    @Test
    void testGetTransactionsByAccountId_Success() throws Exception {
        List<TransactionDTO> transactions = Arrays.asList(transactionDTO);
        when(transactionService.getTransactionsByAccountId(1L)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].accountId").value(1));

        verify(transactionService, times(1)).getTransactionsByAccountId(1L);
    }

    @Test
    void testCreateTransaction_Deposit_Success() throws Exception {
        when(transactionService.createTransaction(any(CreateTransactionRequest.class))).thenReturn(transactionDTO);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(500.0));

        verify(transactionService, times(1)).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void testCreateTransaction_Withdraw_Success() throws Exception {
        createRequest.setType(TransactionType.WITHDRAW);
        transactionDTO.setType(TransactionType.WITHDRAW);
        when(transactionService.createTransaction(any(CreateTransactionRequest.class))).thenReturn(transactionDTO);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        verify(transactionService, times(1)).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void testCreateTransaction_Transfer_Success() throws Exception {
        createRequest.setType(TransactionType.TRANSFER);
        createRequest.setToAccountId(2L);
        transactionDTO.setType(TransactionType.TRANSFER);
        transactionDTO.setToAccountId(2L);
        when(transactionService.createTransaction(any(CreateTransactionRequest.class))).thenReturn(transactionDTO);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        verify(transactionService, times(1)).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void testCreateTransaction_InvalidRequest() throws Exception {
        createRequest.setAmount(null); // Invalid

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(transactionService, never()).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void testDeleteTransaction_Success() throws Exception {
        doNothing().when(transactionService).deleteTransaction(1L);

        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());

        verify(transactionService, times(1)).deleteTransaction(1L);
    }

    @Test
    void testDeleteAllTransactions() throws Exception {
        when(transactionService.deleteAllTransactions()).thenReturn("All transactions deleted");

        mockMvc.perform(delete("/api/transactions/reset"))
                .andExpect(status().isOk())
                .andExpect(content().string("All transactions deleted"));

        verify(transactionService, times(1)).deleteAllTransactions();
    }

    @Test
    void testCreateTransaction_InsufficientBalance() throws Exception {
        when(transactionService.createTransaction(any(CreateTransactionRequest.class))).thenThrow(new RuntimeException("Insufficient balance"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(transactionService, times(1)).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void testCreateTransaction_AccountNotFound() throws Exception {
        when(transactionService.createTransaction(any(CreateTransactionRequest.class))).thenThrow(new RuntimeException("Account not found"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(transactionService, times(1)).createTransaction(any(CreateTransactionRequest.class));
    }

    @Test
    void testGetTransactionsByAccountId_Empty() throws Exception {
        when(transactionService.getTransactionsByAccountId(1L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(transactionService, times(1)).getTransactionsByAccountId(1L);
    }
}
