package com.example.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ewallet.entity.GecaPayTransfer;

@Repository
public interface GecaPayTransferRepository extends JpaRepository<GecaPayTransfer, Integer> {

}
