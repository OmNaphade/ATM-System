package com.omnaphade.service;

import com.omnaphade.custom_exception.InsufficientResourcesException;
import com.omnaphade.custom_exception.ResourceNotFoundException;
import com.omnaphade.dtos.CreateTransactionRequest;
import com.omnaphade.dtos.TransactionDTO;
import com.omnaphade.entites.BankAccount;
import com.omnaphade.entites.Transaction;
import com.omnaphade.entites.TransactionType;
import com.omnaphade.repository.BankAccountRepository;
import com.omnaphade.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TransactionServiceImpl implements ITransactionService {

	private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	private final TransactionRepository transactionRepo;
	private final BankAccountRepository accountRepo;

	public TransactionServiceImpl(TransactionRepository transactionRepo,
	                              BankAccountRepository accountRepo) {
		this.transactionRepo = transactionRepo;
		this.accountRepo = accountRepo;
	}

	@Override
	public TransactionDTO createTransaction(CreateTransactionRequest req) {

		// Generate reference ID if not provided
		String referenceId = req.getReferenceId();
		if (referenceId == null || referenceId.isEmpty()) {
			referenceId = UUID.randomUUID().toString();
		}

		// ✅ 1. Idempotency check
		Optional<Transaction> existing = transactionRepo.findByReferenceId(referenceId);
		if (existing.isPresent()) {
			logger.info("Idempotent transaction found for referenceId: {}, returning existing", referenceId);
			return mapToDTO(existing.get());
		}

		// ✅ 2. Lock accounts (prevents race condition)
		BankAccount source = accountRepo.findByIdForUpdate(req.getAccountId())
				.orElseThrow(() -> new ResourceNotFoundException("Source account not found"));

		BankAccount target = null;

		if (req.getType() == TransactionType.TRANSFER) {
			target = accountRepo.findByIdForUpdate(req.getToAccountId())
					.orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));
		}

		// ✅ 3. Business logic
		switch (req.getType()) {

			case DEPOSIT ->
					source.setBalance(source.getBalance().add(req.getAmount()));

			case WITHDRAW -> {
				validateBalance(source, req.getAmount());
				source.setBalance(source.getBalance().subtract(req.getAmount()));
			}

			case TRANSFER -> {
				validateBalance(source, req.getAmount());

				source.setBalance(source.getBalance().subtract(req.getAmount()));
				target.setBalance(target.getBalance().add(req.getAmount()));
			}
		}

		// ✅ 4. Create transaction
		Transaction tx = new Transaction();
		tx.setAccount(source);
		tx.setToAccount(target);
		tx.setAmount(req.getAmount());
		tx.setType(req.getType());
		tx.setTransactionTime(LocalDateTime.now());
		tx.setReferenceId(referenceId);

		Transaction saved = transactionRepo.save(tx);
		logger.info("Created {} transaction with ID: {}", req.getType(), saved.getTransactionId());

		return mapToDTO(saved);
	}

	private void validateBalance(BankAccount account, BigDecimal amount) {
		if (account.getBalance().compareTo(amount) < 0) {
			logger.warn("Insufficient balance for account ID: {}, required: {}, available: {}", account.getAccountId(), amount, account.getBalance());
			throw new InsufficientResourcesException("Insufficient balance");
		}
	}

	private TransactionDTO mapToDTO(Transaction tx) {
		TransactionDTO dto = new TransactionDTO();
		dto.setTransactionId(tx.getTransactionId());
		dto.setAccountId(tx.getAccount().getAccountId());

		if (tx.getToAccount() != null) {
			dto.setToAccountId(tx.getToAccount().getAccountId());
		}

		dto.setAmount(tx.getAmount().doubleValue());
		dto.setType(tx.getType());
		dto.setTimestamp(tx.getTransactionTime());
		dto.setReferenceId(tx.getReferenceId());

		return dto;
	}

	// ================= OTHER METHODS =================

	@Override
	public List<TransactionDTO> getAllTransactions() {
		List<Transaction> transactions = transactionRepo.findAll();
		logger.info("Retrieved {} transactions", transactions.size());
		return transactions.stream()
				.map(this::mapToDTO)
				.toList();
	}

	@Override
	public List<TransactionDTO> getTransactionsByAccountId(Long accountId) {
		List<Transaction> transactions = transactionRepo.findByAccount_AccountId(accountId);
		logger.info("Retrieved {} transactions for account ID: {}", transactions.size(), accountId);
		return transactions.stream()
				.map(this::mapToDTO)
				.toList();
	}

	@Override
	public TransactionDTO getTransactionById(Long id) {
		Transaction tx = transactionRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
		return mapToDTO(tx);
	}

	@Override
	public void deleteTransaction(Long id) {
		if (!transactionRepo.existsById(id)) {
			throw new ResourceNotFoundException("Transaction not found");
		}
		transactionRepo.deleteById(id);
		logger.info("Deleted transaction with ID: {}", id);
	}

	@Override
	public String deleteAllTransactions() {
		transactionRepo.deleteAll();
		logger.info("Deleted all transactions");
		return "All Transactions deleted successfully!";
	}
}
