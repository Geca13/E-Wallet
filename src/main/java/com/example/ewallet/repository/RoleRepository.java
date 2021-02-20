package com.example.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.ewallet.entity.Role;
import com.example.ewallet.entity.RoleName;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	Role findByRole(RoleName role);

}
