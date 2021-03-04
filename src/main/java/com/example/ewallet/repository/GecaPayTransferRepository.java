package com.example.ewallet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ewallet.entity.GecaPayTransfer;
import com.example.ewallet.entity.Users;

@Repository
public interface GecaPayTransferRepository extends JpaRepository<GecaPayTransfer, Integer> {

	List<GecaPayTransfer> findByFrom(Users user);
}
