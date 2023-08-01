package com.udemy.springboot.webflux.apirest.app;

import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Test
	void listarTest() {
		client.get().uri("/api/v2/productos").exchange().expectStatus().isOk().expectBodyList(Producto.class)
				.consumeWith(response -> {
					List<Producto> productos = response.getResponseBody();
					Assertions.assertThat(productos.size() > 0).isTrue();
				});
	}

	@Test
	void verTest() {
		Producto producto = productoService.findByNombre("Sony Camara HD Digital").block();

		client.get().uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId())).exchange()
				.expectStatus().isOk().expectBody().jsonPath("$.id").isNotEmpty().jsonPath("$.nombre")
				.isEqualTo("Sony Camara HD Digital");
	}

	@Test
	void crearTest() {
		Categoria categoria = productoService.findCategoriaByNombre("Muebles").block();

		Producto producto = new Producto("Mesa comedor", 100.00, categoria);

		client.post()
		.uri("/api/v2/productos")
		.body(Mono.just(producto), Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectBody()
		.jsonPath("$.id").isNotEmpty()
		.jsonPath("$.nombre").isEqualTo("Mesa comedor")
		.jsonPath("$.categoria.nombre").isEqualTo("Muebles");
	}

}
