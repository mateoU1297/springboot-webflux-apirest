package com.udemy.springboot.webflux.apirest.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterFunctionConfig {
	
	@Bean
	public RouterFunction<ServerResponse> routes(ProductoHandler handler) {
		return RouterFunctions.route(RequestPredicates.GET("/api/v2/productos"), handler::listar)
				.andRoute(RequestPredicates.GET("/api/v2/productos/{id}"), handler::ver)
				.andRoute(RequestPredicates.POST("/api/v2/productos"), handler::crear);
	}
}
