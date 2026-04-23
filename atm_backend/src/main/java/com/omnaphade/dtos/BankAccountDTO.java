package com.omnaphade.dtos;

import com.omnaphade.entites.AccountType;
import jakarta.transaction.Transactional;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Transactional
public class BankAccountDTO {
	private Long accountId;
	private String accountNumber;
	private AccountType accountType;
	private String bankName;
	private double balance;
	private LocalDate creationDate;
	private Long userId;
	private String pin;
}
