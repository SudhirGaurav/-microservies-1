package com.sudhirudemystudy.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.websocket.server.PathParam;

@RestController
public class CurrencyConversionController {
	
	private Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);
	@Autowired
	private CurrencyExchangeProxy proxy;

	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion getCurrencyConversion(@PathVariable String from, @PathVariable String to , @PathVariable BigDecimal quantity) {
		
		Map<String, String> uriVariable = new HashMap<>();
		uriVariable.put("from", from);
		uriVariable.put("to", to);
		//Callling currency_exchnage MS .
		ResponseEntity<CurrencyConversion> responseEntity = new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversion.class, uriVariable);
		CurrencyConversion currencyCoversion = responseEntity.getBody();
		BigDecimal convertedValue= quantity.multiply(currencyCoversion.getConversionMultiple());
		return new CurrencyConversion(currencyCoversion.getId(), from, to, quantity, currencyCoversion.getConversionMultiple(), convertedValue,currencyCoversion.getEnvironment()+" "+" Rest_Template");
		
		//Note :as u can see , for one microservice call ,we need to write 20 lines of code .IN real work  appl, there could be 1000 MS , think once how many line of code is needed for for .
		//ThereFore , Spring cloud provided a FW called Feng ,which will handle to call multiple microservices
	
	}
	
	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversion getCurrencyConversionFeign(@PathVariable String from, @PathVariable String to , @PathVariable BigDecimal quantity) {
		logger.info("I am in currency Conversion Feign load balancer  ...............");
		System.out.println("Calling Feign to call currency-Exchange MS");
		CurrencyConversion currencyCoversion =proxy.retriveExchangeValue(from, to);
		BigDecimal convertedValue= quantity.multiply(currencyCoversion.getConversionMultiple());
		return new CurrencyConversion(currencyCoversion.getId(), from, to, quantity, currencyCoversion.getConversionMultiple(), convertedValue,currencyCoversion.getEnvironment()+" "+"feign");
	}
}
