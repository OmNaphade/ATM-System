package com.omnaphade.service;

import com.omnaphade.custom_exception.InsufficientResourcesException;
import com.omnaphade.custom_exception.ResourceNotFoundException;
import com.omnaphade.dtos.CreateTransactionRequest;
import com.omnaphade.dtos.TransactionDTO;
import com.omnaphade.entites.*;
import com.omnaphade.repository.BankAccountRepository;
import com.omnaphade.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepo;

    @Mock
    private BankAccountRepository accountRepo;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private BankAccount sourceAccount;
    private BankAccount targetAccount;
    private Transaction transaction;
    private CreateTransactionRequest depositRequest;
    private CreateTransactionRequest withdrawRequest;
    private CreateTransactionRequest transferRequest;
    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserId(1L);
        user.setName("John Doe");

        sourceAccount = new BankAccount();
        sourceAccount.setAccountId(1L);
        sourceAccount.setAccountNumber("1234567890");
        sourceAccount.setBalance(BigDecimal.valueOf(1000.0));
        sourceAccount.setUser(user);

        targetAccount = new BankAccount();
        targetAccount.setAccountId(2L);
        targetAccount.setAccountNumber("0987654321");
        targetAccount.setBalance(BigDecimal.valueOf(500.0));
        targetAccount.setUser(user);

        transaction = new Transaction();
        transaction.setTransactionId(1L);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.valueOf(500.0));
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setReferenceId("ref123");
        transaction.setAccount(sourceAccount);

        depositRequest = new CreateTransactionRequest();
        depositRequest.setAccountId(1L);
        depositRequest.setAmount(BigDecimal.valueOf(500.0));
        depositRequest.setType(TransactionType.DEPOSIT);
        depositRequest.setReferenceId("ref123");

        withdrawRequest = new CreateTransactionRequest();
        withdrawRequest.setAccountId(1L);
        withdrawRequest.setAmount(BigDecimal.valueOf(200.0));
        withdrawRequest.setType(TransactionType.WITHDRAW);
        withdrawRequest.setReferenceId("ref456");

        transferRequest = new CreateTransactionRequest();
        transferRequest.setAccountId(1L);
        transferRequest.setToAccountId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(300.0));
        transferRequest.setType(TransactionType.TRANSFER);
        transferRequest.setReferenceId("ref789");

        transactionDTO = new TransactionDTO();
        transactionDTO.setTransactionId(1L);
        transactionDTO.setAccountId(1L);
        transactionDTO.setAmount(500.0);
        transactionDTO.setType(TransactionType.DEPOSIT);
        transactionDTO.setTimestamp(LocalDateTime.now());
        transactionDTO.setReferenceId("ref123");
    }

    @Test
    void testCreateTransaction_Deposit_Success() {
        when(transactionRepo.findByReferenceId("ref123")).thenReturn(Optional.empty());
        when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(sourceAccount));
        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = transactionService.createTransaction(depositRequest);

        assertEquals(TransactionType.DEPOSIT, result.getType());
        assertEquals(500.0, result.getAmount());
        verify(accountRepo, times(1)).findByIdForUpdate(1L);
        verify(transactionRepo, times(1)).save(any(Transaction.class));
        assertEquals(BigDecimal.valueOf(1500.0), sourceAccount.getBalance());
    }

    @Test
    void testCreateTransaction_Withdraw_Success() {
        when(transactionRepo.findByReferenceId("ref456")).thenReturn(Optional.empty());
        when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(sourceAccount));
        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = transactionService.createTransaction(withdrawRequest);

        assertEquals(TransactionType.WITHDRAW, result.getType());
        assertEquals(200.0, result.getAmount());
        verify(accountRepo, times(1)).findByIdForUpdate(1L);
        assertEquals(BigDecimal.valueOf(800.0), sourceAccount.getBalance());
    }

    @Test
    void testCreateTransaction_Withdraw_InsufficientBalance() {
        withdrawRequest.setAmount(BigDecimal.valueOf(1500.0));
        when(transactionRepo.findByReferenceId("ref456")).thenReturn(Optional.empty());
        when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(sourceAccount));

        assertThrows(InsufficientResourcesException.class, () -> transactionService.createTransaction(withdrawRequest));
        verify(accountRepo, times(1)).findByIdForUpdate(1L);
        verify(transactionRepo, never()).save(any(Transaction.class));
    }

    @Test
    void testCreateTransaction_Transfer_Success() {
        when(transactionRepo.findByReferenceId("ref789")).thenReturn(Optional.empty());
        when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepo.findByIdForUpdate(2L)).thenReturn(Optional.of(targetAccount));
        when(transactionRepo.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = transactionService.createTransaction(transferRequest);

        assertEquals(TransactionType.TRANSFER, result.getType());
        assertEquals(300.0, result.getAmount());
        verify(accountRepo, times(1)).findByIdForUpdate(1L);
        verify(accountRepo, times(1)).findByIdForUpdate(2L);
        assertEquals(BigDecimal.valueOf(700.0), sourceAccount.getBalance());
        assertEquals(BigDecimal.valueOf(800.0), targetAccount.getBalance());
    }

    @Test
    void testCreateTransaction_Transfer_SourceNotFound() {
        when(transactionRepo.findByReferenceId("ref789")).thenReturn(Optional.empty());
        when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.createTransaction(transferRequest));
        verify(accountRepo, times(1)).findByIdForUpdate(1L);
        verify(accountRepo, never()).findByIdForUpdate(2L);
    }

    @Test
    void testCreateTransaction_Transfer_TargetNotFound() {
        when(transactionRepo.findByReferenceId("ref789")).thenReturn(Optional.empty());
        when(accountRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(sourceAccount));
        when(accountRepo.findByIdForUpdate(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.createTransaction(transferRequest));
        verify(accountRepo, times(1)).findByIdForUpdate(1L);
        verify(accountRepo, times(1)).findByIdForUpdate(2L);
    }

    @Test
    void testCreateTransaction_Idempotent() {
        when(transactionRepo.findByReferenceId("ref123")).thenReturn(Optional.of(transaction));

        TransactionDTO result = transactionService.createTransaction(depositRequest);

        assertEquals("ref123", result.getReferenceId());
        verify(transactionRepo, times(1)).findByReferenceId("ref123");
        verify(accountRepo, never()).findByIdForUpdate(anyLong());
        verify(transactionRepo, never()).save(any(Transaction.class));
    }

    @Test
    void testGetAllTransactions() {
        when(transactionRepo.findAll()).thenReturn(java.util.Arrays.asList(transaction));

        java.util.List<TransactionDTO> result = transactionService.getAllTransactions();

        assertEquals(1, result.size());
        verify(transactionRepo, times(1)).findAll();
    }

    @Test
    void testGetTransactionsByAccountId() {
        when(transactionRepo.findByAccount_AccountId(1L)).thenReturn(java.util.Arrays.asList(transaction));

        java.util.List<TransactionDTO> result = transactionService.getTransactionsByAccountId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getAccountId());
        verify(transactionRepo, times(1)).findByAccount_AccountId(1L);
    }

    @Test
    void testGetTransactionById_Success() {
        when(transactionRepo.findById(1L)).thenReturn(Optional.of(transaction));

        TransactionDTO result = transactionService.getTransactionById(1L);

        assertEquals(1L, result.getTransactionId());
        verify(transactionRepo, times(1)).findById(1L);
    }

    @Test
    void testGetTransactionById_NotFound() {
        when(transactionRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(1L));
        verify(transactionRepo, times(1)).findById(1L);
    }

    @Test
    void testDeleteTransaction_Success() {
        when(transactionRepo.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> transactionService.deleteTransaction(1L));
        verify(transactionRepo, times(1)).existsById(1L);
        verify(transactionRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteTransaction_NotFound() {
        when(transactionRepo.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> transactionService.deleteTransaction(1L));
        verify(transactionRepo, times(1)).existsById(1L);
        verify(transactionRepo, never()).deleteById(1L);
    }

    @Test
    void testDeleteAllTransactions() {
        transactionService.deleteAllTransactions();

        verify(transactionRepo, times(1)).deleteAll();
    }

    @Test
    void testValidateBalance_Sufficient() {
        assertDoesNotThrow(() -> {
            java.lang.reflect.Method method = TransactionServiceImpl.class.getDeclaredMethod("validateBalance", BankAccount.class, BigDecimal.class);
            method.setAccessible(true);
            method.invoke(transactionService, sourceAccount, BigDecimal.valueOf(500.0));
        });
    }

    @Test
    void testValidateBalance_Insufficient() {
        assertThrows(InsufficientResourcesException.class, () -> {
            java.lang.reflect.Method method = TransactionServiceImpl.class.getDeclaredMethod("validateBalance", BankAccount.class, BigDecimal.class);
            method.setAccessible(true);
            method.invoke(transactionService, sourceAccount, BigDecimal.valueOf(1500.0));
        });
    }
}
