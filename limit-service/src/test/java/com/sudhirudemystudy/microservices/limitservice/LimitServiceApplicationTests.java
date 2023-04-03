package com.sudhirudemystudy.microservices.limitservice;

import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LimitServiceApplicationTests {

	@InjectMocks
	Object[] params = new Object[5];
	
	@Test
	void contextLoads() {
		System.out.println("Load........");
		assertEquals("Testing assertEquals", "1", "1");
	}
}
