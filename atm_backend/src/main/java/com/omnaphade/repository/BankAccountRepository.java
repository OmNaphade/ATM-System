package com.omnaphade.repository;

import com.omnaphade.entites.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT b FROM BankAccount b WHERE b.accountId = :id")
	Optional<BankAccount> findByIdForUpdate(@Param("id") Long id);

	Optional<BankAccount> findByAccountNumber(String accountNumber);
}