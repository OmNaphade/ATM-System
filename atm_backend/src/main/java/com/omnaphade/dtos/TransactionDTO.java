package com.omnaphade.dtos;

import com.omnaphade.entites.TransactionType;
import jakarta.transaction.Transactional;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Transactional
public class TransactionDTO {
	private Long transactionId;
	private TransactionType type;
	private double amount;
	private LocalDateTime timestamp;
	private Long accountId;
	private Long toAccountId;
	private String referenceId;
}
