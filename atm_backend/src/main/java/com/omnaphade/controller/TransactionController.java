package com.omnaphade.controller;

import com.omnaphade.dtos.CreateTransactionRequest;
import com.omnaphade.dtos.TransactionDTO;
import com.omnaphade.service.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction API")
public class TransactionController {

    private final ITransactionService transactionService;

    public TransactionController(ITransactionService service) {
        this.transactionService = service;
    }

    @Operation(summary = "Create transaction")
    @PostMapping
    public ResponseEntity<TransactionDTO> create(@RequestBody CreateTransactionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(req));
    }

    @Operation(summary = "Get all transactions")
    @GetMapping
    public List<TransactionDTO> getAll() {
        return transactionService.getAllTransactions();
    }

    @Operation(summary = "Get transaction by ID")
    @GetMapping("/{id}")
    public TransactionDTO getById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    @Operation(summary = "Get all transactions by account ID")
    @GetMapping("/account/{accountId}")
    public List<TransactionDTO> getByAccount(@PathVariable Long accountId) {
        return transactionService.getTransactionsByAccountId(accountId);
    }

    @Operation(summary = "Delete transaction by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete All Transactions")
    @DeleteMapping("/reset")
    public String deleteAllTransaction() { return transactionService.deleteAllTransactions(); }
}

