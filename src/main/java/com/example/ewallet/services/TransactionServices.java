package com.example.ewallet.services;


import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.ewallet.entity.CreditCard;
import com.example.ewallet.entity.Deposit;
import com.example.ewallet.entity.Users;
import com.example.ewallet.repository.CreditCardRepository;
import com.example.ewallet.repository.DepositRepository;
import com.example.ewallet.repository.GecaPayTransferRepository;
import com.example.ewallet.repository.UsersRepository;

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

}
