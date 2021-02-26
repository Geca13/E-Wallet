package com.example.ewallet.controllor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.ewallet.entity.Users;
import com.example.ewallet.repository.UsersRepository;
import com.example.ewallet.services.UsersDetails;





@Controller
public class UserController {
	
	@Autowired
	UsersRepository userRepository;

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
}
