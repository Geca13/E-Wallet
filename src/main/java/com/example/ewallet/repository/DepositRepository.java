package com.example.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ewallet.entity.Deposit;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, Integer> {

}
