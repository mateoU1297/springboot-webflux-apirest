package com.udemy.springboot.webflux.apirest.app;

import java.net.URI;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.udemy.springboot.webflux.apirest.app.models.documents.Producto;
import com.udemy.springboot.webflux.apirest.app.models.services.ProductoService;

import reactor.core.publisher.Mono;

@Component
public class ProductoHandler {

	@Autowired
	private ProductoService productoService;
	
	public Mono<ServerResponse> listar(ServerRequest request) {
		return ServerResponse.ok()
		.contentType(MediaType.APPLICATION_JSON)
		.body(productoService.findAll(), Producto.class);
	}
	
	public Mono<ServerResponse> ver(ServerRequest request) {
		String id = request.pathVariable("id");
		
		return productoService.findById(id).flatMap(p -> ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromObject(p)))
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> crear(ServerRequest request) {
		Mono<Producto> producto = request.bodyToMono(Producto.class);
		
		return producto.flatMap(p -> {
			if(p.getCreateAt() == null) {
				p.setCreateAt(new Date());
			}
			return productoService.save(p);
		}).flatMap(p -> ServerResponse
				.created(URI.create("/api/v2/productos/".concat(p.getId())))
				.body(BodyInserters.fromObject(p)));
	}
}
