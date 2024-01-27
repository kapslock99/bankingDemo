package com.springboot.bankingDemo.Repositary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.bankingDemo.Entity.User;

@Repository
public interface UserRepositary extends JpaRepository<User, Integer>{
	
	User findByUsernameAndPassword(String username , String password);
	
	User findByAccountNo(int accountNo);
}
