package com.omnaphade.service;

import com.omnaphade.custom_exception.ResourceNotFoundException;
import com.omnaphade.dtos.BankAccountDTO;
import com.omnaphade.dtos.CreateBankAccountRequest;
import com.omnaphade.entites.BankAccount;
import com.omnaphade.entites.User;
import com.omnaphade.repository.BankAccountRepository;
import com.omnaphade.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BankAccountServiceImpl implements IBankAccountService {

	private static final Logger logger = LoggerFactory.getLogger(BankAccountServiceImpl.class);

	private BankAccountRepository Accountrepo;

	private ModelMapper mapper;

	private UserRepository userRepo;

	private PasswordEncoder passwordEncoder;

    public BankAccountServiceImpl(BankAccountRepository accountrepo, ModelMapper mapper, UserRepository userRepo, PasswordEncoder passwordEncoder) {
        Accountrepo = accountrepo;
        this.mapper = mapper;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
	public BankAccountDTO createAccount(CreateBankAccountRequest req) {
		User user = userRepo.findById(req.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		BankAccount acct = new BankAccount();
		acct.setAccountNumber(req.getAccountNumber());
		acct.setAccountType(req.getAccountType());
		acct.setBankName(req.getBankName());
		acct.setBalance(BigDecimal.valueOf(req.getBalance()));
		acct.setCreationDate(LocalDate.now());
		acct.setPin(passwordEncoder.encode(req.getPin()));
		acct.setUser(user);
		BankAccount saved = Accountrepo.save(acct);
		logger.info("Created new bank account with ID: {}", saved.getAccountId());
		return mapper.map(saved, BankAccountDTO.class);
	}

	@Override
	public List<BankAccountDTO> getAllAccounts() {
		List<BankAccount> accounts = Accountrepo.findAll();
		logger.info("Retrieved {} bank accounts", accounts.size());
		List<BankAccountDTO> dtos = new ArrayList<>();
		for (BankAccount acct : accounts) {
			BankAccountDTO dto = mapper.map(acct, BankAccountDTO.class);
			dtos.add(dto);
		}
		return dtos;
	}

	@Override
	public BankAccountDTO getAccountById(Long id) {
		Optional<BankAccount> optional = Accountrepo.findById(id);
		if (optional.isPresent()) {
			return mapper.map(optional.get(), BankAccountDTO.class);
		} else {
			throw new ResourceNotFoundException("Account not found");
		}
	}

	@Override
	public BankAccountDTO getAccountByAccountNumber(String accountNumber) {
		Optional<BankAccount> optional = Accountrepo.findByAccountNumber(accountNumber);
		if (optional.isPresent()) {
			return mapper.map(optional.get(), BankAccountDTO.class);
		} else {
			throw new ResourceNotFoundException("Account not found");
		}
	}

	@Override
	public BankAccountDTO updateAccount(Long id, CreateBankAccountRequest req) {
		Optional<BankAccount> optional = Accountrepo.findById(id);
		if (!optional.isPresent()) {
			throw new ResourceNotFoundException("Account not found");
		}
		BankAccount acct = optional.get();
		acct.setAccountNumber(req.getAccountNumber());
		acct.setAccountType(req.getAccountType());
		acct.setBankName(req.getBankName());
		acct.setBalance(BigDecimal.valueOf(req.getBalance()));
		User user = userRepo.findById(req.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		acct.setUser(user);
		acct.setPin(passwordEncoder.encode(req.getPin()));
		BankAccount updated = Accountrepo.save(acct);
		logger.info("Updated bank account with ID: {}", id);
		return mapper.map(updated, BankAccountDTO.class);
	}

	@Override
	public void deleteAccount(Long id) {
		boolean exists = Accountrepo.existsById(id);
		if (!exists) {
			throw new ResourceNotFoundException("Account not found");
		}
		Accountrepo.deleteById(id);
		logger.info("Deleted bank account with ID: {}", id);
	}

	@Override
	public String deleteAllAccounts() {
		Accountrepo.deleteAll();
		logger.info("Deleted all bank accounts");
		return "All Accounts deleted successfully!";
	}
}
