package com.omnaphade.service;


import com.omnaphade.dtos.CreateTransactionRequest;
import com.omnaphade.dtos.TransactionDTO;

import java.util.List;

public interface ITransactionService {
	TransactionDTO createTransaction(CreateTransactionRequest req);

	List<TransactionDTO> getAllTransactions();

	List<TransactionDTO> getTransactionsByAccountId(Long accountId);

	TransactionDTO getTransactionById(Long id);

	void deleteTransaction(Long id);

    String deleteAllTransactions();
}
