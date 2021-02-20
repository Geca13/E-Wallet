package com.example.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ewallet.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

}
