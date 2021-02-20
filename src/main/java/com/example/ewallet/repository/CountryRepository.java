package com.example.ewallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ewallet.entity.Country;


@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
	
    Country findByCountryName(String countryName);
	
	Boolean existsByCountryName(String countryName);

}
