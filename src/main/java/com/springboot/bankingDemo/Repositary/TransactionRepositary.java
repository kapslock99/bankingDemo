package com.springboot.bankingDemo.Repositary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.bankingDemo.Entity.Transaction;

@Repository
public interface TransactionRepositary extends JpaRepository<Transaction, Integer>{

	List<Transaction> findByUserAccountNo(int userAccountNo);
}
