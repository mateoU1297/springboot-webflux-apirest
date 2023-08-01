package com.udemy.springboot.webflux.apirest.app;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.udemy.springboot.webflux.apirest.app.models.documents.Categoria;
import com.udemy.springboot.webflux.apirest.app.models.documents.Producto;
import com.udemy.springboot.webflux.apirest.app.models.services.ProductoService;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringbootWebfluxApirestApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ProductoService productoService;
	
	@Value("${config.base.endpoint}")
	private String url;

	@Test
	void listarTest() {
		client.get().uri(url).exchange().expectStatus().isOk().expectBodyList(Producto.class)
				.consumeWith(response -> {
					List<Producto> productos = response.getResponseBody();
					Assertions.assertThat(productos.size() > 0).isTrue();
				});
	}

	@Test
	void verTest() {
		Producto producto = productoService.findByNombre("Sony Camara HD Digital").block();

		client.get().uri(url + "/{id}", Collections.singletonMap("id", producto.getId())).exchange()
				.expectStatus().isOk().expectBody().jsonPath("$.id").isNotEmpty().jsonPath("$.nombre")
				.isEqualTo("Sony Camara HD Digital");
	}

	@Test
	void crearTest() {
		Categoria categoria = productoService.findCategoriaByNombre("Muebles").block();

		Producto producto = new Producto("Mesa comedor", 100.00, categoria);

		client.post()
		.uri(url)
		.body(Mono.just(producto), Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo("Mesa comedor")
		.jsonPath("$.categoria.nombre").isEqualTo("Muebles");
	}
	
	@Test
	void editarTest() {
		
		Producto producto = productoService.findByNombre("Sony Notebook").block();
		Categoria categoria = productoService.findCategoriaByNombre("Electrónico").block();
		
		Producto productoEditado = new Producto("Asus Notebook", 700.00, categoria);
		
		client.put().uri(url + "/{id}", Collections.singletonMap("id", producto.getId()))
		.body(Mono.just(productoEditado), Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo("Asus Notebook")
		.jsonPath("$.categoria.nombre").isEqualTo("Electrónico");
	}
	
	@Test
	void eliminarTest() {
		Producto producto = productoService.findByNombre("Mica Cómoda 5 Cajones").block();
		client.delete()
		.uri(url + "/{id}", Collections.singletonMap("id", producto.getId()))
		.exchange()
		.expectStatus().isNoContent()
		.expectBody()
		.isEmpty();
		
		client.get()
		.uri(url + "/{id}", Collections.singletonMap("id", producto.getId()))
		.exchange()
		.expectStatus().isNotFound()
		.expectBody()
		.isEmpty();
	}

}
