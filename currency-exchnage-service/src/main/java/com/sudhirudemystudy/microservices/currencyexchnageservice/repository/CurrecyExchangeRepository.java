package com.sudhirudemystudy.microservices.currencyexchnageservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sudhirudemystudy.microservices.currencyexchnageservice.CurrencyExchange;

public interface CurrecyExchangeRepository extends JpaRepository<CurrencyExchange, Long>{
	
	public CurrencyExchange findByFromAndTo(String from , String to);

}
