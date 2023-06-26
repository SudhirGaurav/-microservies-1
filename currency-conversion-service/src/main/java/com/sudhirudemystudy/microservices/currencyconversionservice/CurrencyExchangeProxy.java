package com.sudhirudemystudy.microservices.currencyconversionservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


//Name is Application namer which is given in applicztion.properties
@FeignClient(name = "currency-exchange" , url = "localhost:8000")
public interface CurrencyExchangeProxy {

	//This is the metod of corrency exchange service 
	@GetMapping("/currency-exchange/from/{from}/to/{to}")
	public CurrencyConversion retriveExchangeValue( @PathVariable String from , @PathVariable String to) ;

}
