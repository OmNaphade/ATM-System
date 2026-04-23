package com.omnaphade.dtos;

import com.omnaphade.entites.TransactionType;
import jakarta.transaction.Transactional;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Transactional
public class CreateTransactionRequest {

	private Long accountId;
	private Long toAccountId;
	private BigDecimal amount;
	private TransactionType type;

	// idempotency key
	private String referenceId;
}
