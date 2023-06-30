package com.sudhirudemystudy.microservices.currencyexchnageservice.circuitbreaker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class CircuitBreakerController {
	
	Logger logger = LoggerFactory.getLogger(CircuitBreakerController.class);

	
	//@Retry(name = "default" ) // this will retry 3 time[Interview]
	@Retry(name = "retry-sample-api" , fallbackMethod = "hardcodedResponse")// this will be used in application.properties
	@GetMapping("/sample-api") 
	public String sampleApi() {
		logger.info("calling sample-api");
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/dummy-url", String.class); // Just to call failure  dummy URL
		return forEntity.getBody();
	}
	
	
	public String hardcodedResponse(Exception ex) { // Exception is mandatory , otherwise exception will come in console
		logger.info("OOPS methos call broke ....");
		return "OOPS methos call broke ....";
	}
	
	
	@CircuitBreaker(name = "default" ,fallbackMethod = "hardcodedResponse")//TODO: Need to read the circuitBreker concept
	@GetMapping("/sample-api-circuitbreaker") 
	public String sampleApiForCircuitBreaker() {
		logger.info("calling Circuit Breaker ");
		ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/dummy-url", String.class);
		return forEntity.getBody();
	}
	
	
	//ReateLimiter : u can limit no of call in fixed time. Ex: 2 calls in 10sec 
	@RateLimiter(name = "default" )
	@GetMapping("/sample-api-ratelimiter") 
	public String sampleApiForRateLimiter() {
		logger.info("calling Rate Limiter ");
		return "calling Rate Limiter";
		
	}
	
	//@Bulkhead(name="default") // no of calls alloweed concurrentely 
	
}
