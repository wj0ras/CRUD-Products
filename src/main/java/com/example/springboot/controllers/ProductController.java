package com.example.springboot.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.springboot.dto.ProductRecordDto;
import com.example.springboot.model.ProductModel;
import com.example.springboot.repositories.ProductRepository;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import jakarta.validation.Valid;

@RestController
public class ProductController {
	
	@Autowired
	ProductRepository productRepository;
	
	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
		var productModel = new ProductModel();
		BeanUtils.copyProperties(productRecordDto, productModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
	}
	
	@GetMapping("/products")
	public ResponseEntity<List<ProductModel>> getAllProducts() {
		List<ProductModel> productList = productRepository.findAll();
		if(!productList.isEmpty()) {
			for (ProductModel product : productList) {
				UUID id = product.getId();
				product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
			}
		}
		return ResponseEntity.status(HttpStatus.OK).body(productList);
	}
	
	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value="id") UUID id) {
		Optional<ProductModel> productId = productRepository.findById(id);
		if(productId.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}
		productId.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Producs List"));
		return ResponseEntity.status(HttpStatus.OK).body(productId.get());
	}

	@PutMapping("/products/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable(value="id") UUID id,@RequestBody @Valid ProductRecordDto productRecordDto) {
		Optional<ProductModel> productId = productRepository.findById(id);
		if(productId.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}
		var productModel = productId.get();
		BeanUtils.copyProperties(productRecordDto, productModel);
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
		}
	
	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> delectProduct(@PathVariable(value="id") UUID id) {
		Optional<ProductModel> productId = productRepository.findById(id);
		if(productId.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
		}
		productRepository.delete(productId.get());
		return ResponseEntity.status(HttpStatus.OK).body("Product deleted sucessfully.");
	}
}
