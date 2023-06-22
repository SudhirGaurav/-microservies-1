package com.sudhirudemystudy.microservices.currencyexchnageservice;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyExchnageController {

	@Autowired
	private Environment environment;
	
	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public CurrencyExchange retriveExchangeValue( @PathVariable String from , @PathVariable String to) {
		
		CurrencyExchange currencyExchange = new CurrencyExchange(1000L,from,to,BigDecimal.valueOf(80));
		String port =environment.getProperty("local.server.port");
		System.out.println("*******************port is***************** : "+port);
		currencyExchange.setEnvironment(port);
		return currencyExchange;
	}
}
