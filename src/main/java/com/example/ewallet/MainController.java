package com.example.ewallet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.ewallet.entity.Users;
import com.example.ewallet.services.InvalidAgeException;
import com.example.ewallet.services.InvalidPasswordException;
import com.example.ewallet.services.UsersService;
import com.example.ewallet.services.userWithThatEmailAlreadyExistsException;


@Controller
@RequestMapping("/signUpForm")
public class MainController {
	
	private UsersService usersService;

	public MainController(UsersService usersService) {
		super();
		this.usersService = usersService;
	}
	
	@ModelAttribute("user")
	public Users user() {
		return new Users();
	}
	
	@GetMapping
	public String showSignUpForm(Model model) {
		
	    return "signUpForm";
	}
	
	@PostMapping
	public String registerAdmin(@ModelAttribute("user") Users user, Model model) {
		
		try {
			usersService.saveUser(user);
		} catch (InvalidPasswordException | userWithThatEmailAlreadyExistsException | InvalidAgeException e) {
			model.addAttribute("error", e.getMessage());
			return "signUpForm";
		}
		
		return  "redirect:/signUpForm?success" ;
	}
	
}
 