package com.udemy.springboot.webflux.apirest.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.udemy.springboot.webflux.apirest.app.models.documents.Producto;

import reactor.core.publisher.Mono;


public interface ProductoDao extends ReactiveMongoRepository<Producto, String> {

	public Mono<Producto> findByNombre(String nombre);
}
