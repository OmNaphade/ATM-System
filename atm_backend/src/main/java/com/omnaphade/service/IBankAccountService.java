package com.omnaphade.service;



import com.omnaphade.dtos.BankAccountDTO;
import com.omnaphade.dtos.CreateBankAccountRequest;

import java.util.List;

public interface IBankAccountService {
	BankAccountDTO createAccount(CreateBankAccountRequest req);

	List<BankAccountDTO> getAllAccounts();

	BankAccountDTO getAccountById(Long id);

	BankAccountDTO getAccountByAccountNumber(String accountNumber);

	BankAccountDTO updateAccount(Long id, CreateBankAccountRequest req);

	void deleteAccount(Long id);

	String deleteAllAccounts();
}
