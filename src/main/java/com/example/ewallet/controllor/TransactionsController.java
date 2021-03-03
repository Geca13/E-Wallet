package com.example.ewallet.controllor;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.ewallet.entity.Deposit;
import com.example.ewallet.entity.GecaPayTransfer;
import com.example.ewallet.entity.Users;
import com.example.ewallet.repository.CreditCardRepository;
import com.example.ewallet.repository.DepositRepository;
import com.example.ewallet.repository.GecaPayTransferRepository;
import com.example.ewallet.repository.UsersRepository;
import com.example.ewallet.services.InvalidCardException;
import com.example.ewallet.services.TransactionServices;
import com.example.ewallet.services.UsersDetails;

@Controller
public class TransactionsController {
	
	@Autowired
	DepositRepository depositRepository;
	
	@Autowired
	CreditCardRepository cardRepository;
	
	@Autowired
	UsersRepository userRepository;
	
	@Autowired
	TransactionServices services;
	
	@Autowired
	GecaPayTransferRepository gptRepository;
	
	
	@GetMapping("/cardDeposite/{id}")
	public String cardDepositForm(Model model ,@AuthenticationPrincipal UsersDetails userD,@PathVariable("id")Integer id ) {
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
        model.addAttribute("card", cardRepository.findById(id).get());
        model.addAttribute("deposit", new Deposit());

		return "cardDeposit";
	}
	
	@PostMapping("/cardDeposite/{id}")
	public String completeDeposit(Model model ,@AuthenticationPrincipal UsersDetails userD,@PathVariable("id")Integer id, @ModelAttribute("deposit")Deposit deposit,
			@Param(value="month")String month,@Param(value="year") String year,@Param(value="cvv") String cvv) {

		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		try {
			services.newDeposit(user, id, deposit, month, year, cvv);
		} catch (InvalidCardException e) {
			model.addAttribute("message", e.getMessage());
	        model.addAttribute("user", user);
	        model.addAttribute("card", cardRepository.findById(id).get());
			return "cardDeposit";
		}
		
		
		return "redirect:/";
	}
	
	@GetMapping("/transferPage")
	public String getTransferPage(Model model ,@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
        
		return "transfer";
	}
	
	@GetMapping("/findRecepient")
	public String findRecipientByEmail(@Param(value = "email")String email) {
		
		Users recipient = userRepository.findByEmail(email);
		 if(recipient == null) {
			 return "redirect:/transferPage?userNotFound";
		 }
        
		return "redirect:/complete/" + recipient.getId() ;
	}
	
	@GetMapping("/complete/{id}")
	public String completeTransferPage(Model model, @AuthenticationPrincipal UsersDetails userD,@PathVariable("id") Integer id) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        Users recipient = userRepository.findById(id).get();
        model.addAttribute("user", user);
        model.addAttribute("recipient", recipient);
        model.addAttribute("transfer", new GecaPayTransfer());
		return "completeTransfer";
		
	}
	
	@PostMapping("/complete/{id}")
	public String transferComplete (Model model, @AuthenticationPrincipal UsersDetails userD,@PathVariable("id") Integer id,@ModelAttribute("transfer") GecaPayTransfer transfer) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        Users recipient = userRepository.findById(id).get();
        
        GecaPayTransfer newTransfer = new GecaPayTransfer();
        newTransfer.setTime(LocalDateTime.now());
        newTransfer.setDescription("Transfer from " + user.getFirstName()+ " " + user.getLastName() + " to " + recipient.getFirstName()+ " " + recipient.getLastName());
        newTransfer.setTo(recipient);
        newTransfer.setFrom(user);
        newTransfer.setAmount(transfer.getAmount());
		
		if(transfer.getAmount() < 5 ) {
			return "redirect:/complete/"+recipient.getId()+"?tooSmallAmount";
		}
		
		if(user.getMkdBalance() < transfer.getAmount() ) {
			return "redirect:/complete/"+recipient.getId()+"?tooHighAmount";
		}
		recipient.setMkdBalance(recipient.getMkdBalance() + transfer.getAmount());
		user.setMkdBalance(user.getMkdBalance() - transfer.getAmount());
		userRepository.save(user);
		userRepository.save(recipient);
		gptRepository.save(newTransfer);
		return "redirect:/";
		
	}

}
