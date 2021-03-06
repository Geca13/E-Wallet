package com.example.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ewallet.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

}
