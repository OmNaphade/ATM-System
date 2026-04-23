package com.omnaphade.dtos;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Transactional
public class CreateBankAccountRequest {
	@NotBlank(message = "Account number is required")
	private String accountNumber;

	@NotNull(message = "Account type is required")

	@NotBlank(message = "Bank name is required")
	private String bankName;

	private double balance;

	@NotNull(message = "User ID is required")

	@NotBlank(message = "PIN is required")
	private String pin;
}
