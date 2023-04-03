package com.sudhirudemystudy.microservices.limitservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;




//import com.example.fl.HBUtil;
//import com.example.fl.WorkOrderTask;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Slf4j
class LimitServiceApplicationTests {
	
	@InjectMocks
	  Object[] params;

	//WorkOrderTask c = mock(WorkOrderTask.class);
	@Test
	void contextLoads() {
		
	System.out.println("Load........");
	assertEquals("1", "2");
	assertEquals("2", "1");
	
	
		
		
	}

}
