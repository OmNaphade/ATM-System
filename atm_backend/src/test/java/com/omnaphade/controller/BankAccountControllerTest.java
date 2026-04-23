package com.omnaphade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnaphade.dtos.BankAccountDTO;
import com.omnaphade.dtos.CreateBankAccountRequest;
import com.omnaphade.entites.AccountType;
import com.omnaphade.service.IBankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankAccountController.class)
class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBankAccountService bankAccountService;

    @Autowired
    private ObjectMapper objectMapper;

    private BankAccountDTO accountDTO;
    private CreateBankAccountRequest createRequest;

    @BeforeEach
    void setUp() {
        accountDTO = new BankAccountDTO();
        accountDTO.setAccountId(1L);
        accountDTO.setAccountNumber("1234567890");
        accountDTO.setAccountType(AccountType.SAVINGS);
        accountDTO.setBankName("Test Bank");
        accountDTO.setBalance(1000.0);
        accountDTO.setCreationDate(LocalDate.now());
        accountDTO.setUserId(1L);

        createRequest = new CreateBankAccountRequest();
        createRequest.setAccountNumber("1234567890");
        createRequest.setAccountType(AccountType.SAVINGS);
        createRequest.setBankName("Test Bank");
        createRequest.setBalance(1000.0);
        createRequest.setUserId(1L);
    }

    @Test
    void testGetAllAccounts_Success() throws Exception {
        List<BankAccountDTO> accounts = Arrays.asList(accountDTO);
        when(bankAccountService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"));

        verify(bankAccountService, times(1)).getAllAccounts();
    }

    @Test
    void testGetAccountById_Success() throws Exception {
        when(bankAccountService.getAccountById(1L)).thenReturn(accountDTO);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"));

        verify(bankAccountService, times(1)).getAccountById(1L);
    }

    @Test
    void testGetAccountById_NotFound() throws Exception {
        when(bankAccountService.getAccountById(1L)).thenThrow(new RuntimeException("Account not found"));

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isBadRequest());

        verify(bankAccountService, times(1)).getAccountById(1L);
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        when(bankAccountService.createAccount(any(CreateBankAccountRequest.class))).thenReturn(accountDTO);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"));

        verify(bankAccountService, times(1)).createAccount(any(CreateBankAccountRequest.class));
    }

    @Test
    void testCreateAccount_InvalidRequest() throws Exception {
        createRequest.setAccountNumber(""); // Invalid

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(bankAccountService, never()).createAccount(any(CreateBankAccountRequest.class));
    }

    @Test
    void testUpdateAccount_Success() throws Exception {
        when(bankAccountService.updateAccount(eq(1L), any(CreateBankAccountRequest.class))).thenReturn(accountDTO);

        mockMvc.perform(put("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"));

        verify(bankAccountService, times(1)).updateAccount(eq(1L), any(CreateBankAccountRequest.class));
    }

    @Test
    void testDeleteAccount_Success() throws Exception {
        doNothing().when(bankAccountService).deleteAccount(1L);

        mockMvc.perform(delete("/api/accounts/1"))
                .andExpect(status().isNoContent());

        verify(bankAccountService, times(1)).deleteAccount(1L);
    }

    @Test
    void testDeleteAllAccounts() throws Exception {
        when(bankAccountService.deleteAllAccounts()).thenReturn("All accounts deleted");

        mockMvc.perform(delete("/api/accounts/reset"))
                .andExpect(status().isOk())
                .andExpect(content().string("All accounts deleted"));

        verify(bankAccountService, times(1)).deleteAllAccounts();
    }

    @Test
    void testCreateAccount_UserNotFound() throws Exception {
        when(bankAccountService.createAccount(any(CreateBankAccountRequest.class))).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(bankAccountService, times(1)).createAccount(any(CreateBankAccountRequest.class));
    }

    @Test
    void testUpdateAccount_NotFound() throws Exception {
        when(bankAccountService.updateAccount(eq(1L), any(CreateBankAccountRequest.class))).thenThrow(new RuntimeException("Account not found"));

        mockMvc.perform(put("/api/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isBadRequest());

        verify(bankAccountService, times(1)).updateAccount(eq(1L), any(CreateBankAccountRequest.class));
    }
}
