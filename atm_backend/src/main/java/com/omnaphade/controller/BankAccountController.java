package com.omnaphade.controller;

import com.omnaphade.dtos.BankAccountDTO;
import com.omnaphade.dtos.CreateBankAccountRequest;
import com.omnaphade.service.IBankAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Bank Account API")
public class BankAccountController {

    private final IBankAccountService accountService;

    public BankAccountController(IBankAccountService service) {
        this.accountService = service;
    }

    @Operation(summary = "Create new bank account")
    @PostMapping
    public ResponseEntity<BankAccountDTO> createAccount(@RequestBody CreateBankAccountRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(req));
    }

    @Operation(summary = "Get all bank accounts")
    @GetMapping
    public List<BankAccountDTO> getAll() {
        return accountService.getAllAccounts();
    }

    @Operation(summary = "Get account by number")
    @GetMapping("/number/{accountNumber}")
    public BankAccountDTO getByAccountNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByAccountNumber(accountNumber);
    }

    @Operation(summary = "Get account by ID")
    @GetMapping("/{id}")
    public BankAccountDTO getById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @Operation(summary = "Update account")
    @PutMapping("/{id}")
    public BankAccountDTO update(@PathVariable Long id, @RequestBody CreateBankAccountRequest req) {
        return accountService.updateAccount(id, req);
    }

    @Operation(summary = "Delete account")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete All Accounts")
    @DeleteMapping("/reset")
    public String deleteAllAccount() { return accountService.deleteAllAccounts(); }
}

