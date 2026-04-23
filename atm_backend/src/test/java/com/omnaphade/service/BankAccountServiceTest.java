package com.omnaphade.service;

import com.omnaphade.custom_exception.ResourceNotFoundException;
import com.omnaphade.dtos.BankAccountDTO;
import com.omnaphade.dtos.CreateBankAccountRequest;
import com.omnaphade.entites.AccountType;
import com.omnaphade.entites.BankAccount;
import com.omnaphade.entites.Role;
import com.omnaphade.entites.User;
import com.omnaphade.repository.BankAccountRepository;
import com.omnaphade.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository accountRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private BankAccountServiceImpl bankAccountService;

    private User user;
    private BankAccount account;
    private CreateBankAccountRequest createRequest;
    private BankAccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setRole(Role.CUSTOMER);

        account = new BankAccount();
        account.setAccountId(1L);
        account.setAccountNumber("1234567890");
        account.setAccountType(AccountType.SAVINGS);
        account.setBankName("Test Bank");
        account.setBalance(BigDecimal.valueOf(1000.0));
        account.setCreationDate(LocalDate.now());
        account.setUser(user);

        createRequest = new CreateBankAccountRequest();
        createRequest.setAccountNumber("1234567890");
        createRequest.setAccountType(AccountType.SAVINGS);
        createRequest.setBankName("Test Bank");
        createRequest.setBalance(1000.0);
        createRequest.setUserId(1L);

        accountDTO = new BankAccountDTO();
        accountDTO.setAccountId(1L);
        accountDTO.setAccountNumber("1234567890");
        accountDTO.setAccountType(AccountType.SAVINGS);
        accountDTO.setBankName("Test Bank");
        accountDTO.setBalance(1000.0);
        accountDTO.setCreationDate(LocalDate.now());
        accountDTO.setUserId(1L);
    }

    @Test
    void testCreateAccount_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepo.save(any(BankAccount.class))).thenReturn(account);
        when(mapper.map(account, BankAccountDTO.class)).thenReturn(accountDTO);

        BankAccountDTO result = bankAccountService.createAccount(createRequest);

        assertEquals("1234567890", result.getAccountNumber());
        assertEquals(BigDecimal.valueOf(1000.0).doubleValue(), result.getBalance());
        verify(userRepo, times(1)).findById(1L);
        verify(accountRepo, times(1)).save(any(BankAccount.class));
    }

    @Test
    void testCreateAccount_UserNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bankAccountService.createAccount(createRequest));
        verify(userRepo, times(1)).findById(1L);
        verify(accountRepo, never()).save(any(BankAccount.class));
    }

    @Test
    void testCreateAccount_NullBalance() {
        createRequest.setBalance(0.0);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepo.save(any(BankAccount.class))).thenReturn(account);
        when(mapper.map(account, BankAccountDTO.class)).thenReturn(accountDTO);

        BankAccountDTO result = bankAccountService.createAccount(createRequest);

        assertNotNull(result);
        verify(accountRepo, times(1)).save(any(BankAccount.class));
    }

    @Test
    void testGetAllAccounts_Success() {
        List<BankAccount> accounts = Arrays.asList(account);
        when(accountRepo.findAll()).thenReturn(accounts);
        when(mapper.map(account, BankAccountDTO.class)).thenReturn(accountDTO);

        List<BankAccountDTO> result = bankAccountService.getAllAccounts();

        assertEquals(1, result.size());
        assertEquals("1234567890", result.get(0).getAccountNumber());
        verify(accountRepo, times(1)).findAll();
    }

    @Test
    void testGetAllAccounts_EmptyList() {
        when(accountRepo.findAll()).thenReturn(Arrays.asList());

        List<BankAccountDTO> result = bankAccountService.getAllAccounts();

        assertTrue(result.isEmpty());
        verify(accountRepo, times(1)).findAll();
    }

    @Test
    void testGetAccountById_Success() {
        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(mapper.map(account, BankAccountDTO.class)).thenReturn(accountDTO);

        BankAccountDTO result = bankAccountService.getAccountById(1L);

        assertEquals("1234567890", result.getAccountNumber());
        verify(accountRepo, times(1)).findById(1L);
    }

    @Test
    void testGetAccountById_NotFound() {
        when(accountRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bankAccountService.getAccountById(1L));
        verify(accountRepo, times(1)).findById(1L);
    }

    @Test
    void testUpdateAccount_Success() {
        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepo.save(any(BankAccount.class))).thenReturn(account);
        when(mapper.map(account, BankAccountDTO.class)).thenReturn(accountDTO);

        CreateBankAccountRequest updateRequest = new CreateBankAccountRequest();
        updateRequest.setAccountNumber("0987654321");
        updateRequest.setAccountType(AccountType.CURRENT);
        updateRequest.setBankName("Updated Bank");
        updateRequest.setBalance(2000.0);
        updateRequest.setUserId(1L);

        BankAccountDTO result = bankAccountService.updateAccount(1L, updateRequest);

        assertNotNull(result);
        verify(accountRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).findById(1L);
        verify(accountRepo, times(1)).save(any(BankAccount.class));
    }

    @Test
    void testUpdateAccount_NotFound() {
        when(accountRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bankAccountService.updateAccount(1L, createRequest));
        verify(accountRepo, times(1)).findById(1L);
        verify(userRepo, never()).findById(anyLong());
    }

    @Test
    void testUpdateAccount_UserNotFound() {
        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bankAccountService.updateAccount(1L, createRequest));
        verify(accountRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).findById(1L);
    }

    @Test
    void testDeleteAccount_Success() {
        when(accountRepo.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> bankAccountService.deleteAccount(1L));
        verify(accountRepo, times(1)).existsById(1L);
        verify(accountRepo, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteAccount_NotFound() {
        when(accountRepo.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> bankAccountService.deleteAccount(1L));
        verify(accountRepo, times(1)).existsById(1L);
        verify(accountRepo, never()).deleteById(1L);
    }

    @Test
    void testDeleteAllAccounts() {
        bankAccountService.deleteAllAccounts();

        verify(accountRepo, times(1)).deleteAll();
    }

    @Test
    void testCreateAccount_WithDifferentAccountTypes() {
        createRequest.setAccountType(AccountType.CURRENT);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepo.save(any(BankAccount.class))).thenReturn(account);
        when(mapper.map(account, BankAccountDTO.class)).thenReturn(accountDTO);

        BankAccountDTO result = bankAccountService.createAccount(createRequest);

        assertEquals(AccountType.SAVINGS, result.getAccountType()); // Since account is set to SAVINGS
        verify(accountRepo, times(1)).save(any(BankAccount.class));
    }
}
