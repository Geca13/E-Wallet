package com.example.ewallet.services;


import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.ewallet.entity.CreditCard;
import com.example.ewallet.entity.Transaction;
import com.example.ewallet.entity.Users;
import com.example.ewallet.repository.CreditCardRepository;
import com.example.ewallet.repository.TransactionRepository;
import com.example.ewallet.repository.UsersRepository;

@Service
public class TransactionServices {
	
	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	CreditCardRepository cardRepository;
	
	@Autowired
	UsersRepository userRepository;
	
	public Transaction newDeposit ( Users user,@PathVariable("id")Integer id, @ModelAttribute("transaction")Transaction transaction,
			  String month, String year, String cvv) throws InvalidCardException {
		Transaction newTransaction = new Transaction();
		CreditCard card = cardRepository.findById(id).get();
		if(!card.getMonth().equals(month) || !card.getYear().equals(year) || !card.getCvv().equals(cvv)) {
			
			throw new InvalidCardException("Please check your card details the ones you entered are incorect");
		}
		
		
		newTransaction.setDescription("Deposit with Credit/Debit card ending with *"+ card.getCardNumber().substring(12));
		newTransaction.setAmount(transaction.getAmount());
		newTransaction.setUser(user);
		newTransaction.setTime(LocalDateTime.now());
		user.setMkdBalance(user.getMkdBalance()+transaction.getAmount());
		userRepository.save(user);
		return transactionRepository.save(newTransaction);
		
	}

}
