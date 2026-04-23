package com.omnaphade.dtos;

import com.omnaphade.entites.AccountType;
import jakarta.transaction.Transactional;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Transactional
public class CreateBankAccountRequest {
	private String accountNumber;
	private AccountType accountType;
	private String bankName;
	private double balance;
	private Long userId; // Link to existing user
	private String pin;
}
