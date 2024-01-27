package com.springboot.bankingDemo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.bankingDemo.Entity.Transaction;
import com.springboot.bankingDemo.Entity.User;
import com.springboot.bankingDemo.Repositary.TransactionRepositary;
import com.springboot.bankingDemo.Repositary.UserRepositary;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class BankController {
	@Autowired
	UserRepositary userRepositary;
	
	@Autowired
	TransactionRepositary transactionRepositary; 
	
	@GetMapping("/")
	public String toLogin() {
		
		return "login";
	}
	
	@GetMapping("/registration")
	public String toRegister() {
		
		return "register";
	}
	
	@GetMapping("/validation")
	public String loginValidation(Model model , HttpServletRequest request , @RequestParam("username") String username , @RequestParam("password") String password) {
		
		User user = userRepositary.findByUsernameAndPassword(username, password);
		
		if(user == null) {
			return "failedLogin";
		}
		
		HttpSession session = request.getSession();
		
		session.setAttribute("username", username);
		session.setAttribute("password", password);
		session.setAttribute("accountNo", user.getAccountNo());
		
		model.addAttribute("name", user.getName());
		

		return "home";
		
		
	}
	
	@GetMapping("/toHome")
	public String toHome() {
		
		return "home";
	}
	
	@PostMapping("/createAccount")
	public String createAccount(@RequestParam("name") String name , @RequestParam("email") String email , @RequestParam("mobile") long mobile , @RequestParam("username") String username , @RequestParam("password") String password ,@RequestParam("gender") String gender) {
		
		User user = new User();
		
		user.setName(name);
		user.setEmail(email);
		user.setMobile(mobile);
		user.setUsername(username);
		user.setPassword(password);
		user.setGender(gender);
		user.setBalance(500);
		
		userRepositary.save(user);
		System.out.println("account created successfully");
		
		
		
		java.util.Date date = new java.util.Date();  
		String currentDate = "" + date;
		int userAccountNo = userRepositary.findByUsernameAndPassword(username, password).getAccountNo();
		
		Transaction transaction = new Transaction();
		transaction.setTransactionAmount(500);
		transaction.setTransactionTime(currentDate);
		transaction.setTransactionType("Created Account");
		transaction.setUserAccountNo(userAccountNo);
		
		transactionRepositary.save(transaction);
		System.out.println("transaction entry successful");
		
		return "created";
	}
	
	
	@GetMapping("/fetchBalance")
	public String fetchBalance(Model model , HttpSession session) {
		
		String username = (String) session.getAttribute("username");
		String password = (String) session.getAttribute("password");
		
		User user = userRepositary.findByUsernameAndPassword(username, password);
		
		model.addAttribute("balance", user.getBalance());
		
		return "balancePage" ;
	}
	
	@GetMapping("/toAddMoneyPage")
	public String toAddMoneyPage() {
		
		return "addMoneyPage";
	}
	
	@PostMapping("/depositeMoney")
	public String depositeMoney(HttpSession session , @RequestParam("amount") double amount) {
		
		java.util.Date date = new java.util.Date();  
		String currentDate = "" + date;
		
		String username = (String) session.getAttribute("username");
		String password = (String) session.getAttribute("password");
		User user = userRepositary.findByUsernameAndPassword(username, password);
		
		user.setBalance(user.getBalance()  + amount);
		userRepositary.save(user);
		
		Transaction transaction = new Transaction();
		transaction.setTransactionAmount(amount);
		transaction.setTransactionTime(currentDate);
		transaction.setTransactionType("Credit");
		transaction.setUserAccountNo(user.getAccountNo());
		
		transactionRepositary.save(transaction);
		System.out.println("transaction entry successful");
		
		
		return "transactionSuccess";
	}
	
	@GetMapping("/toWithdrawMoneyPage")
	public String toWithDrawMoneyPage() {
		
		return "withdrawMoneyPage";
	}
	
	@PostMapping("/debitMoney")
	public String debitMoney(HttpSession session , @RequestParam("amount") double amount) {
		
		java.util.Date date = new java.util.Date();  
		String currentDate = "" + date;
		
		String username = (String) session.getAttribute("username");
		String password = (String) session.getAttribute("password");
		User user = userRepositary.findByUsernameAndPassword(username, password);
		
		
		if(user.getBalance() < amount) {
			
			return "noBalance" ;
		}
		
		user.setBalance(user.getBalance() - amount);
		userRepositary.save(user);
		
		Transaction transaction = new Transaction();
		transaction.setTransactionAmount(amount);
		transaction.setTransactionTime(currentDate);
		transaction.setTransactionType("Debit");
		transaction.setUserAccountNo(user.getAccountNo());
		
		transactionRepositary.save(transaction);
		System.out.println("transaction entry successful");
		
		
		return "transactionSuccess";	
	}
	
	@GetMapping("/toTransferMoneyPage")
	public String toTransferMoneyPage() {
		
		return "transferMoneyPage";
	}
	
	@PostMapping("/sendMoney")
	public String sendMoney(HttpSession session , @RequestParam("accountNo") int accountNo , @RequestParam("amount") double amount) {
		
		java.util.Date date = new java.util.Date();  
		String currentDate = "" + date;
		
		String username = (String) session.getAttribute("username");
		String password = (String) session.getAttribute("password");
		User user = userRepositary.findByUsernameAndPassword(username, password);
		
		
		if(user.getBalance() < amount) {
			
			return "noBalance2" ;
		}
		
		User reciever = userRepositary.findByAccountNo(accountNo);
		
		if(reciever == null) {
			return "invalidAccount";
		}
		
		user.setBalance(user.getBalance() - amount);
		userRepositary.save(user);
		
		Transaction transaction = new Transaction();
		transaction.setTransactionAmount(amount);
		transaction.setTransactionTime(currentDate);
		transaction.setTransactionType("Transfered");
		transaction.setUserAccountNo(user.getAccountNo());
		
		transactionRepositary.save(transaction);
		
		reciever.setBalance(reciever.getBalance() + amount);
		userRepositary.save(reciever);
		
		Transaction transaction2 = new Transaction();
		transaction2.setTransactionAmount(amount);
		transaction2.setTransactionTime(currentDate);
		transaction2.setTransactionType("Recieved");
		transaction2.setUserAccountNo(reciever.getAccountNo());
		
		transactionRepositary.save(transaction2);
		
		return "transactionSuccess";
	}
	
	@GetMapping("/toTransactionPage")
	public String toTransactionPage(HttpSession session , Model model) {
		
		int accountNo = (Integer) session.getAttribute("accountNo");
		
		List<Transaction> transactions = transactionRepositary.findByUserAccountNo(accountNo);
		model.addAttribute("transactions", transactions);
		
		return "transactionPage";
	}
}
  
  
  
