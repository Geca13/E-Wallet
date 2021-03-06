package com.example.ewallet.controllor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.ewallet.entity.BankAccount;
import com.example.ewallet.entity.CreditCard;
import com.example.ewallet.entity.Users;
import com.example.ewallet.repository.BankAccountRepository;
import com.example.ewallet.repository.CreditCardRepository;
import com.example.ewallet.repository.UsersRepository;
import com.example.ewallet.services.UsersDetails;


@Controller
public class UserController {
	
	@Autowired
	UsersRepository userRepository;
	
	@Autowired
	CreditCardRepository cardRepository;
	
	@Autowired
	BankAccountRepository baRepository;

	@GetMapping("/login")
	public String login(Model model) {
		
		 return "login";
	}
	
	@GetMapping("/contact")
	public String contactPage(Model model) {
		
		 return "contact";
	}
	
	@GetMapping("/how")
	public String servicesPage(Model model) {
		
		 return "services";
	}
	
	@GetMapping("/aboutUs")
	public String aboutUsPage(Model model) {
		
		 return "aboutUs";
	}
	
	
	
	@GetMapping("/")
	public String dashboard(Model model,@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
		 return "index";
	}
	
	@GetMapping("/deposite")
	public String depositeForms(Model model,@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
		
		return "depositOptions";
	}
	
	@GetMapping("/withdraw")
	public String withdrawForms(Model model,@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
		
		return "withdrawOptions";
	}
	
	@GetMapping("/allCardsByUser")
	public String cards(Model model,@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
        
		return "creditCardsList";
	}
	
	@GetMapping("/allAccountsByUser")
	public String accounts(Model model,@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
        
		return "bankAccountsList";
	}
	
	@GetMapping("/addNewCCard")
	public String newCard(Model model,@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
        model.addAttribute("card", new CreditCard());
		
	 return "addNewCard";
	}
	
	@PostMapping("/addNewCCard")
	public String saveNewCard(Model model,@AuthenticationPrincipal UsersDetails userD, @ModelAttribute("card")CreditCard card) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        CreditCard c1 = new CreditCard();
        c1.setCardHolderName(card.getCardHolderName());
        c1.setCardNumber(card.getCardNumber());
        c1.setCvv(card.getCvv());
        c1.setMonth(card.getMonth());
        c1.setYear(card.getYear());
        cardRepository.save(c1);
        user.getCard().add(c1);
        userRepository.save(user);
		return "redirect:/allCardsByUser";
	}
	
	@GetMapping("/addNewBAccount")
	public String newBankAccount(Model model,@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
        model.addAttribute("account", new BankAccount());
		
	 return "addNewBankAccount";
	}
	
	@PostMapping("/addNewBAccount")
	public String saveBankAccount(Model model,@AuthenticationPrincipal UsersDetails userD, @ModelAttribute("account")BankAccount account) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        BankAccount newAccount = new BankAccount();
        newAccount.setAccountHolderName(account.getAccountHolderName());
        newAccount.setAccountNumber(account.getAccountNumber());
        newAccount.setBankName(account.getBankName());
        
        baRepository.save(newAccount);
        user.getAccounts().add(newAccount);
        userRepository.save(user);
		return "redirect:/allAccountsByUser";
	}
	
	
}
