package com.example.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ewallet.entity.Withdrawl;

@Repository
public interface WithdrawlRepository extends JpaRepository<Withdrawl, Integer> {

}
