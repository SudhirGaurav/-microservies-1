package com.sudhirudemystudy.microservices.currencyexchnageservice;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.sudhirudemystudy.microservices.currencyexchnageservice.repository.CurrecyExchangeRepository;

@RestController
public class CurrencyExchnageController {

	@Autowired
	private CurrecyExchangeRepository repository;
	
	@Autowired
	private Environment environment;
	
	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public CurrencyExchange retriveExchangeValue( @PathVariable String from , @PathVariable String to) {
		
		CurrencyExchange currencyExchange = repository.findByFromAndTo(from, to);
		if(currencyExchange == null) {
			throw new  RuntimeException("Db Entery not present for FROM:  "+from + " To : "+to); // this will propagate to the rest response 
		}
		String port =environment.getProperty("local.server.port");
		System.out.println("*******************port is***************** : "+port);
		currencyExchange.setEnvironment(port);
		
		long count = repository.count();
		System.out.println("Count is : "+count);
		
		return currencyExchange;
	}
}
