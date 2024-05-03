package com.sudhirudemystudy.microservices.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	@Bean
	public RouteLocator gateWayRouter(RouteLocatorBuilder builder) {
		logger.info(".......Inside Apigateway MS ......");
		//Use URL for above : http://localhost:8765/get
		return builder.routes().route(p ->p.path("/get").uri("http://httpbin.org:"))
								.route(p ->p.path("/getsum/**").uri("lb://DATA-OPERATION-SERVICE"))
								.route(p ->p.path("/dataprovider/**").uri("lb://DEMO")).build();

	}
}
