package com.example.ewallet.controllor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.ewallet.entity.CreditCard;
import com.example.ewallet.entity.Users;
import com.example.ewallet.repository.CreditCardRepository;
import com.example.ewallet.repository.UsersRepository;
import com.example.ewallet.services.UsersDetails;

@Controller
public class UserController {
	
	@Autowired
	UsersRepository userRepository;
	
	@Autowired
	CreditCardRepository cardRepository;

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
	
	@GetMapping("/allCardsByUser")
	public String cards(Model model,@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
        
		
		return "creditCardsList";
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
	
	
	
	
	
	
	
}
