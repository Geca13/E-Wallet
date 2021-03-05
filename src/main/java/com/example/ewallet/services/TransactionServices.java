package com.example.ewallet.services;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.ewallet.entity.BankAccount;
import com.example.ewallet.entity.CreditCard;
import com.example.ewallet.entity.Deposit;
import com.example.ewallet.entity.GecaPayTransfer;
import com.example.ewallet.entity.Users;
import com.example.ewallet.entity.Withdrawl;
import com.example.ewallet.repository.BankAccountRepository;
import com.example.ewallet.repository.CreditCardRepository;
import com.example.ewallet.repository.DepositRepository;
import com.example.ewallet.repository.GecaPayTransferRepository;
import com.example.ewallet.repository.UsersRepository;
import com.example.ewallet.repository.WithdrawlRepository;

@Service
public class TransactionServices {
	
	@Autowired
	DepositRepository depositRepository;
	
	@Autowired
	CreditCardRepository cardRepository;
	
	@Autowired
	UsersRepository userRepository;
	
	@Autowired
	GecaPayTransferRepository gptRepository;
	
	@Autowired
	WithdrawlRepository withdrawlRepository;
	
	@Autowired
	BankAccountRepository baRepository;
	
	public Deposit newDeposit ( Users user,@PathVariable("id")Integer id, @ModelAttribute("transaction")Deposit deposit,
			  String month, String year, String cvv) throws InvalidCardException {
		Deposit newTransaction = new Deposit();
		CreditCard card = cardRepository.findById(id).get();
		if(!card.getMonth().equals(month) || !card.getYear().equals(year) || !card.getCvv().equals(cvv)) {
			
			throw new InvalidCardException("Please check your card details the ones you entered are incorect");
		}
		
		
		newTransaction.setDescription("Deposit with Credit/Debit card ending with *"+ card.getCardNumber().substring(12));
		newTransaction.setAmount(deposit.getAmount());
		newTransaction.setUser(user);
		newTransaction.setTime(LocalDateTime.now());
		user.setMkdBalance(user.getMkdBalance()+deposit.getAmount());
		userRepository.save(user);
		return depositRepository.save(newTransaction);
		
	}
	
	public List<Users> contacts(Users user){
		
		HashSet<Users> contactUsers = new HashSet<>();
		List<GecaPayTransfer> users = gptRepository.findByFrom(user);
		for (GecaPayTransfer gecaPayTransfer : users) {
			contactUsers.add(gecaPayTransfer.getTo());
		}
		List<Users> list = new ArrayList<>(contactUsers);
		
		return list;
		
	}
	
	public Withdrawl byCard (Withdrawl withdrawl, Integer id, Users user) {
		CreditCard card = cardRepository.findById(id).get();
		Withdrawl request = new Withdrawl();
		request.setAmount(withdrawl.getAmount() * 0.935);
		request.setTime(LocalDateTime.now());
		request.setUser(user);
		request.setDescription("Withdrawl to card with number " + card.getCardNumber());
		request.setProcessed(false);
		user.setUsdBalance(user.getUsdBalance()-withdrawl.getAmount());
		userRepository.save(user);
		return withdrawlRepository.save(request);
		 
	}
	
	public Withdrawl byBankAccount (Withdrawl withdrawl, Integer id, Users user) {
		BankAccount account = baRepository.findById(id).get();
		Withdrawl request = new Withdrawl();
		request.setAmount(withdrawl.getAmount());
		request.setTime(LocalDateTime.now());
		request.setUser(user);
		request.setDescription("Withdrawl to an account with number " + account.getAccountNumber());
		request.setProcessed(false);
		
		return withdrawlRepository.save(request);
		 
	}
	
	

}
