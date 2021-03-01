package com.example.ewallet.controllor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.ewallet.entity.Transaction;
import com.example.ewallet.entity.Users;
import com.example.ewallet.repository.CreditCardRepository;
import com.example.ewallet.repository.TransactionRepository;
import com.example.ewallet.repository.UsersRepository;
import com.example.ewallet.services.InvalidCardException;
import com.example.ewallet.services.TransactionServices;
import com.example.ewallet.services.UsersDetails;

@Controller
public class TransactionsController {
	
	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	CreditCardRepository cardRepository;
	
	@Autowired
	UsersRepository userRepository;
	
	@Autowired
	TransactionServices services;
	
	
	@GetMapping("/cardDeposite/{id}")
	public String cardDepositForm(Model model ,@AuthenticationPrincipal UsersDetails userD,@PathVariable("id")Integer id ) {
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
        model.addAttribute("card", cardRepository.findById(id).get());
        model.addAttribute("transaction", new Transaction());

		return "cardDeposit";
	}
	
	@PostMapping("/cardDeposite/{id}")
	public String completeDeposit(Model model ,@AuthenticationPrincipal UsersDetails userD,@PathVariable("id")Integer id, @ModelAttribute("transaction")Transaction transaction,
			@Param(value="month")String month,@Param(value="year") String year,@Param(value="cvv") String cvv) {

		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		try {
			services.newDeposit(user, id, transaction, month, year, cvv);
		} catch (InvalidCardException e) {
			model.addAttribute("message", e.getMessage());
	        model.addAttribute("user", user);
	        model.addAttribute("card", cardRepository.findById(id).get());
			return "cardDeposit";
		}
		
		
		return "redirect:/";
	}
	

}
