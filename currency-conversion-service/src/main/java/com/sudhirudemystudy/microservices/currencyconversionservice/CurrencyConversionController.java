package com.sudhirudemystudy.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.websocket.server.PathParam;

@RestController
public class CurrencyConversionController {

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
		return new CurrencyConversion(currencyCoversion.getId(), from, to, quantity, currencyCoversion.getConversionMultiple(), convertedValue,currencyCoversion.getEnvironment());
		
		
	
	}
}
