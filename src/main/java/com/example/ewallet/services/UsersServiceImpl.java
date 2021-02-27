package com.example.ewallet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.ewallet.entity.Role;
import com.example.ewallet.entity.RoleName;
import com.example.ewallet.entity.Users;
import com.example.ewallet.repository.RoleRepository;
import com.example.ewallet.repository.UsersRepository;
import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.transaction.Transactional;

@Service
@Transactional
public class UsersServiceImpl implements UsersService {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	private UsersRepository userRepository;
	
	private Pattern pattern;
	private Matcher matcher;
	
	private static  final String PASSWORD_REGEX = 
	        ("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})");
	
	public UsersServiceImpl() {
		pattern = Pattern.compile(PASSWORD_REGEX );
	}
	
	public boolean validate(final String password) {
		matcher =pattern.matcher(password);
		return matcher.matches();
	}

	@Override
	public Users saveAdmin(Users user) throws InvalidPasswordException, userWithThatEmailAlreadyExistsException {
		
	    UsersServiceImpl validator = new UsersServiceImpl();
		Users userAdmin = new Users();
		userAdmin.setEmail(user.getEmail());
		 if(userRepository.existsByEmail(user.getEmail())) {
			 throw new userWithThatEmailAlreadyExistsException("The email is allready used , please click the forgot password link !!!");
		 }
		 userAdmin.setPassword(passwordEncoder.encode(user.getPassword()));
		    if(validator.validate(user.getPassword()) == false) {
		    	throw new InvalidPasswordException("Your chosen password doesnt fit our creteria , it must contain at least 1 number, UpperCase and LowerCase letters and 1 special character");
		    }
		    userAdmin.setFirstName(user.getFirstName());
		    userAdmin.setLastName(user.getLastName());
        
        Role role = roleRepository.findByRole(RoleName.ROLE_ADMIN);
        userAdmin.setRoles( Collections.singleton(role));
        userAdmin.setJoined(LocalDate.now());
		        return userRepository.save(user);
	}
	
	@Override
	public Users saveUser(Users user) throws InvalidPasswordException, userWithThatEmailAlreadyExistsException, InvalidAgeException {
		
	  UsersServiceImpl validator = new UsersServiceImpl();
	  Users regularUser = new Users();
		regularUser.setEmail(user.getEmail());
	     if(userRepository.existsByEmail(user.getEmail())) {
			 throw new userWithThatEmailAlreadyExistsException("The email is allready used , please click the forgot password link !!!");
		 }
	    regularUser.setPassword(passwordEncoder.encode(user.getPassword()));
		    if(validator.validate(user.getPassword()) == false) {
		    	throw new InvalidPasswordException("Your chosen password doesnt fit our creteria , it must contain at least 1 number, UpperCase and LowerCase letters and 1 special character");
		    }
		regularUser.setFirstName(user.getFirstName());
	    regularUser.setLastName(user.getLastName());
	    regularUser.setBirthDate(user.getBirthDate());
      Role role = roleRepository.findByRole(RoleName.ROLE_USER);
        regularUser.setRoles( Collections.singleton(role));
        regularUser.setJoined(LocalDate.now());
        regularUser.setEurBalance(0.00);
        regularUser.setUsdBalance(0.00);
        regularUser.setMkdBalance(0.00);
        regularUser.setVerified(false);
   
        Integer period= Period.between(regularUser.getBirthDate(), LocalDate.now()).getYears();
		 if (period < 18 ) {
			 throw new InvalidAgeException("You must be older then 18 years to be able to join our site."); 
		 }
		  return userRepository.save(regularUser);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Users user = userRepository.findByEmail(username);
		   if(user == null) {
			  throw new UsernameNotFoundException("You are not signUped with that email");
		}
		      return new UsersDetails(user);
	}
	
	@Override
	public Page<Users> findPagina(Integer pageNumber, Integer pageSize, String sortField, String sortDirection,String search) {
		
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
			Sort.by(sortField).descending();
		Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
		if(search != null) {
		    return	userRepository.findBySearch(search, pageable);
		}
		return userRepository.findAll(pageable);
	}
	
	public void forgotPassword(String token, String email) throws UserNotFoundException {
		
		Users user = userRepository.findByEmail(email);
		if(user != null) {
			user.setToken(token);
			userRepository.save(user);
		} else {
			
			throw new UserNotFoundException("We dont have user with "+ email + " email, in our database ");
		}
	}
	
	public Users getToken(String token) {
		return userRepository.findByToken(token);
	}
	
	public void updatePassword(Users user, String newPassword) throws InvalidPasswordException {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodePassword = encoder.encode(newPassword);
	       user.setPassword(encodePassword);
		   user.setToken(null);
		       userRepository.save(user);
	}
	
	
}
