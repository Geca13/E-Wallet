package com.example.ewallet.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import org.hibernate.annotations.NaturalId;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {
	
	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Email
	@NaturalId
	private String email;
	
	private String password;
	
	private String firstName;
	
	private String lastName;
	
	private Double mkdBalance;
	
	private Double usdBalance;
	
	private Double eurBalance;
	
	private LocalDate joined;
	
	private Boolean verified;
	
	@ManyToMany
	private List<CreditCard> card;
	
	@ManyToMany
	private List<BankAccount> accounts;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private LocalDate birthDate;
	
	@Size(max = 45)
	private String token;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "users_roles" , joinColumns = @JoinColumn(name = "users_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>() ;
	
	public Users(String password) {
		super();
		this.password = password;
	}

    public Users(@Email String email, String password, String firstName, String lastName,  LocalDate joined, LocalDate birthDate,
		Set<Role> roles) {
	super();
	this.email = email;
	this.password = password;
	this.firstName = firstName;
	this.lastName = lastName;
	this.joined = joined;
	this.birthDate = birthDate;
	this.roles = roles;
}
	
	
}
