package com.example.ewallet.controllor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
	
	@Autowired
	BankAccountRepository baRepository;
	
	
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
        List<Users> contacts = services.contacts(user);
        model.addAttribute("contacts",contacts);
        
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
	
	@GetMapping("/findByContact/{id}")
	public String findRecipientByContact(@PathVariable("id")Integer id) {
		
		Users recipient = userRepository.findById(id).get();
        
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
	public String transferComplete (Model model, @AuthenticationPrincipal UsersDetails userD,@PathVariable("id") Integer id,@ModelAttribute("transfer") GecaPayTransfer transfer,@Param(value="currency")String currency) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        Users recipient = userRepository.findById(id).get();
        
        GecaPayTransfer newTransfer = new GecaPayTransfer();
        newTransfer.setTime(LocalDateTime.now());
        newTransfer.setDescription("Transfer from " + user.getFirstName()+ " " + user.getLastName() + " to " + recipient.getFirstName()+ " " + recipient.getLastName());
        newTransfer.setTo(recipient);
        newTransfer.setFrom(user);
        newTransfer.setAmount(transfer.getAmount());
		
		if(currency.equalsIgnoreCase("MKD")) {
			
			if(user.getMkdBalance() < transfer.getAmount() ) {
				return "redirect:/complete/"+recipient.getId()+"?tooHighAmount";
			}
			  
			   if(transfer.getAmount() <= 13000.00) {
			   recipient.setMkdBalance(recipient.getMkdBalance() + (transfer.getAmount() * 0.9801));
			   }
			   else if(transfer.getAmount() > 13000.00 && transfer.getAmount() <= 50000.00 ) {
				   recipient.setMkdBalance(recipient.getMkdBalance() + (transfer.getAmount() * 0.9861));
			   }
			   else if(transfer.getAmount() > 50001.00 && transfer.getAmount() <= 125000.00 ) {
				   recipient.setMkdBalance(recipient.getMkdBalance() + (transfer.getAmount() * 0.9901));
			   }
			   else if(transfer.getAmount() > 125001.00 && transfer.getAmount() <= 250000.00 ) {
				   recipient.setMkdBalance(recipient.getMkdBalance() + (transfer.getAmount() * 0.9921));
			   }
			   else if(transfer.getAmount() > 250001.00) {
				   
				   return "redirect:/complete/"+recipient.getId()+"?tooHighAmount2";
			   }
			   
			   user.setMkdBalance(user.getMkdBalance() - transfer.getAmount());
			   userRepository.save(user);
			   userRepository.save(recipient);
		}
		
        if(currency.equalsIgnoreCase("USD")) {
			
			if(user.getUsdBalance() < transfer.getAmount() ) {
				return "redirect:/complete/"+recipient.getId()+"?tooHighAmount";
			}
			
			if(transfer.getAmount() <= 250.00) {
				   recipient.setUsdBalance(recipient.getUsdBalance() + (transfer.getAmount() * 0.9801));
				   }
				   else if(transfer.getAmount() > 250.00 && transfer.getAmount() <= 999.99 ) {
					   recipient.setUsdBalance(recipient.getUsdBalance() + (transfer.getAmount() * 0.9861));
				   }
				   else if(transfer.getAmount() > 999.99 && transfer.getAmount() <= 2500.00 ) {
					   recipient.setUsdBalance(recipient.getUsdBalance() + (transfer.getAmount() * 0.9901));
				   }
				   else if(transfer.getAmount() > 2501.00 && transfer.getAmount() <= 5000.00 ) {
					   recipient.setUsdBalance(recipient.getUsdBalance() + (transfer.getAmount() * 0.9921));
				   }
				   else if(transfer.getAmount() > 5000.00) {
					   
					   return "redirect:/complete/"+recipient.getId()+"?tooHighAmount2";
				   }
			
			
			   recipient.setUsdBalance(recipient.getUsdBalance() + transfer.getAmount());
			   user.setMkdBalance(user.getUsdBalance() - transfer.getAmount());
			   userRepository.save(user);
			   userRepository.save(recipient);
		}
        
        if(currency.equalsIgnoreCase("EUR")) {
			
			if(user.getEurBalance() < transfer.getAmount() ) {
				return "redirect:/complete/"+recipient.getId()+"?tooHighAmount";
			}
			
			if(transfer.getAmount() <= 230.00) {
				   recipient.setEurBalance(recipient.getEurBalance() + (transfer.getAmount() * 0.9801));
				   }
				   else if(transfer.getAmount() > 230.00 && transfer.getAmount() <= 899.99 ) {
					   recipient.setEurBalance(recipient.getEurBalance() + (transfer.getAmount() * 0.9861));
				   }
				   else if(transfer.getAmount() > 899.99 && transfer.getAmount() <= 2300.00 ) {
					   recipient.setEurBalance(recipient.getEurBalance() + (transfer.getAmount() * 0.9901));
				   }
				   else if(transfer.getAmount() > 2301.00 && transfer.getAmount() <= 4600.00 ) {
					   recipient.setEurBalance(recipient.getEurBalance() + (transfer.getAmount() * 0.9921));
				   }
				   else if(transfer.getAmount() > 4600.00) {
					   
					   return "redirect:/complete/"+recipient.getId()+"?tooHighAmount2";
				   }
			
			
			   recipient.setEurBalance(recipient.getEurBalance() + transfer.getAmount());
			   user.setMkdBalance(user.getEurBalance() - transfer.getAmount());
			   userRepository.save(user);
			   userRepository.save(recipient);
		}
        
		gptRepository.save(newTransfer);
		return "redirect:/";
		
	}
	
	@GetMapping("/byCard/{id}")
	public String withdrawlToCard(Model model,@AuthenticationPrincipal UsersDetails userD,@PathVariable("id") Integer id,@ModelAttribute("withdrawl")Withdrawl withdrawl ) {
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        CreditCard card = cardRepository.findById(id).get();
        model.addAttribute("user", user);
        model.addAttribute("card", card);
        model.addAttribute("withdrawl", new Withdrawl());
		Double maximumSum = user.getUsdBalance() * 0.935;
		model.addAttribute("maximumSum", maximumSum);
		return "cardWithDrawl";
		
	}

	@PostMapping("/byCard/{id}")
	public String completeWithdrawlToCard(@AuthenticationPrincipal UsersDetails userD,@PathVariable("id") Integer id,@ModelAttribute("withdrawl")Withdrawl withdrawl ) {
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        if(withdrawl.getAmount()<20.00) {
			return "redirect:/byCard/"+id+"?minError";
		}
		if(withdrawl.getAmount()>1000.00) {
			return "redirect:/byCard/"+id+"?maxError";
		}
		if(withdrawl.getAmount()>user.getUsdBalance()) {
			return "redirect:/byCard/"+id+"?invalidAmount";
		}
		
		
		services.byCard(withdrawl, id, user);
	     return "redirect:/";

	}
	
	@GetMapping("/byBank/{id}")
	public String withdrawlToBank(Model model,@AuthenticationPrincipal UsersDetails userD,@PathVariable("id") Integer id) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        BankAccount account = baRepository.findById(id).get();
        model.addAttribute("user", user);
        model.addAttribute("account", account);
        model.addAttribute("withdrawl", new Withdrawl());
		Double maximumSum = (user.getEurBalance() -3.5) * 0.97;
		model.addAttribute("maximumSum", maximumSum);
		
		return "bankWithDrawl";
	}
	
	@PostMapping("/byBank/{id}")
	public String completeWithdrawlToBank(@AuthenticationPrincipal UsersDetails userD,@PathVariable("id") Integer id,@ModelAttribute("withdrawl")Withdrawl withdrawl ) {
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        if(withdrawl.getAmount()<100.00) {
			return "redirect:/byBank/"+id+"?minError";
		}
		if(withdrawl.getAmount()>5000.00) {
			return "redirect:/byBank/"+id+"?maxError";
		}
		if(withdrawl.getAmount()>user.getEurBalance()) {
			return "redirect:/byBank/"+id+"?invalidAmount";
		}
		
		services.byBankAccount(withdrawl, id, user);
		
	     return "redirect:/";

	}

}
