package com.omnaphade.security;

import com.omnaphade.entites.BankAccount;
import com.omnaphade.repository.BankAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountUserDetailsService implements UserDetailsService {

    private final BankAccountRepository bankAccountRepository;

    public AccountUserDetailsService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found: " + accountNumber));

        return User.builder()
                .username(account.getAccountNumber())
                .password(account.getPin())
                .roles("USER")
                .build();
    }
}
