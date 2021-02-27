package com.example.ewallet.services;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.ewallet.entity.Users;


public interface UsersService extends UserDetailsService {
	
	Users saveAdmin(Users user) throws InvalidPasswordException, userWithThatEmailAlreadyExistsException;
	
	public Users saveUser(Users user) throws InvalidPasswordException, userWithThatEmailAlreadyExistsException, InvalidAgeException;
	
	public Page<Users> findPagina(Integer pageNumber, Integer pageSize,String sortField, String sortDirection, String search);

}
