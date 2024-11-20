package com.grigoryos.propvue.controller;

import com.grigoryos.propvue.model.Product;
import com.grigoryos.propvue.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Products",
        description = "REST API для управления продуктами в различных центрах выполнения " +
                "(Fulfillment Centers) и поддержания данных о состоянии запасов"
)
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Operation(summary = "Get products", description = "Get a list of all products with optional filtering by status")
    @GetMapping
    public List<Product> getProducts(@RequestParam(required = false) String status) {
        if (status != null) {
            return productRepository.findByStatus(status);
        }
        return productRepository.findAll();
    }

    @Operation(summary = "Create a new product", description = "Create a new product")
    @PostMapping
    public ResponseEntity<String> addProduct(@RequestBody Product product) {
        productRepository.save(product);
        return ResponseEntity.ok("Product added successfully");
    }

    @Operation(summary = "Update a product", description = "Update an existing product by productId")
    @PutMapping("/{productId}")
    public ResponseEntity<String> updateProduct(@PathVariable String productId, @RequestBody Product productDetails) {
        return productRepository.findById(productId)
                .map(product -> {
                    product.setStatus(productDetails.getStatus());
                    product.setFulfillmentCenter(productDetails.getFulfillmentCenter());
                    product.setQuantity(productDetails.getQuantity());
                    product.setValue(productDetails.getValue());
                    productRepository.save(product);
                    return ResponseEntity.ok("Product updated successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a product", description = "Delete an existing product by productId")
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId) {
        return productRepository.findById(productId)
                .map(product -> {
                    productRepository.delete(product);
                    return ResponseEntity.ok("Product deleted successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get sum value of all sellable", description = "Get sum value of all products with \"Sellable\" status")
    @GetMapping("/value/sellable")
    public ResponseEntity<Double> getSellableValue() {
        double totalValue = productRepository.findByStatus("Sellable").stream()
                .map(product -> product.getQuantity() * product.getValue())
                .mapToDouble(Double::doubleValue)
                .sum();
        return ResponseEntity.ok(totalValue);
    }

    @Operation(summary = "Get sum value of all products of a Fulfillment Center",
            description = "Get sum value of all products of a Fulfillment Center \"fulfillmentCenterId\"")
    @GetMapping("/value/fulfillmentCenter/{fulfillmentCenterId}")
    public ResponseEntity<Double> getFulfillmentCenterValue(@PathVariable String fulfillmentCenterId) {
        double totalValue = productRepository.findByFulfillmentCenter(fulfillmentCenterId).stream()
                .map(product -> product.getQuantity() * product.getValue())
                .mapToDouble(Double::doubleValue)
                .sum();
        return ResponseEntity.ok(totalValue);
    }
}
