package com.example.ewallet.controllor;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.ewallet.entity.Message;
import com.example.ewallet.entity.SupportTicket;
import com.example.ewallet.entity.Users;
import com.example.ewallet.repository.MessageRepository;
import com.example.ewallet.repository.SupportTicketRepository;
import com.example.ewallet.repository.UsersRepository;
import com.example.ewallet.services.UsersDetails;

@Controller
public class SupportController {

	@Autowired
	UsersRepository userRepository;
	
	@Autowired
	MessageRepository messageRepository;
	
	@Autowired
	SupportTicketRepository ticketRepository;
	
	@GetMapping("/newTicket")
	public String openTicketPage(Model model,@AuthenticationPrincipal UsersDetails userD) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
        model.addAttribute("user", user);
        model.addAttribute("ticket", new SupportTicket());
        model.addAttribute("message", new Message());
		
		return "supportPage";
		
	}
	
	@PostMapping("/newTicket")
	public String createTicket(@AuthenticationPrincipal UsersDetails userD,@ModelAttribute("ticket") SupportTicket ticket,@Param(value="message") String message) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		SupportTicket newTicket = new SupportTicket();
	     newTicket.setUser(user);
		 newTicket.setTime(LocalDateTime.now());
		 newTicket.setTopic(ticket.getTopic());
		 newTicket.setStatus("New");
		 newTicket.setOpened(true);
		
		
		Message newMessage = new Message();
		 newMessage.setTime(LocalDateTime.now());
		 newMessage.setUser(user);
		 newMessage.setMessage(message);
		  messageRepository.save(newMessage);
		 newTicket.getMessages().add(newMessage);
		
		 ticketRepository.save(newTicket);
		
		    return "redirect:/";
		
	}
	
	@PostMapping("/openTicket/{id}")
	public String openTicket(@PathVariable("id")Integer id) {
		
		SupportTicket ticket = ticketRepository.findById(id).get();
		ticket.setStatus("Opened");
		ticketRepository.save(ticket);
		
		return null;
		
	}
	
	@PostMapping("/reply/{id}")
	public String replyToTicket(@AuthenticationPrincipal UsersDetails userD,@PathVariable("id")Integer id, Message message ) {
		
		String userEmail = userD.getUsername();
        Users user = userRepository.findByEmail(userEmail);
		SupportTicket ticket = ticketRepository.findById(id).get();
		Message newMessage = new Message();
		 newMessage.setTime(LocalDateTime.now());
		 newMessage.setUser(user);
		 newMessage.setMessage(message.getMessage());
		  messageRepository.save(message);
		 ticket.getMessages().add(message);
		 ticket.setStatus("Answered");
		  ticketRepository.save(ticket);
		
	     return null;
		
	}
	
	@PostMapping("/closeTicket/{id}")
	public String closeTicket(@PathVariable("id")Integer id) {
		
		SupportTicket ticket = ticketRepository.findById(id).get();
		ticket.setOpened(false);
		ticketRepository.save(ticket);
		
		return null;
		
	}
	
	
}
