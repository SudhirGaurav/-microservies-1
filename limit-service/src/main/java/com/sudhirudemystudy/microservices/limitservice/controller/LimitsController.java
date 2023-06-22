package com.sudhirudemystudy.microservices.limitservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sudhirudemystudy.microservices.limitservice.Configuration;
import com.sudhirudemystudy.microservices.limitservice.beans.Limits;

@RestController
public class LimitsController {

	@Autowired
	Configuration configuration;
	
	@GetMapping("/")
	public String testMandatory() {
		return "Hello I am using Spring booot";
	}
	
	@GetMapping("/limits")
	public Limits retriveLimits() {
		System.out.println("This is first rest call ....");
		
		return new Limits(configuration.getMinimum(),configuration.getMaximum());
		//return new Limits(1,1000);
	}
}
