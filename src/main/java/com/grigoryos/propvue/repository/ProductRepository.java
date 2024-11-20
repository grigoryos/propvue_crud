package com.grigoryos.propvue.repository;

import com.grigoryos.propvue.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByStatus(String status);

    List<Product> findByFulfillmentCenter(String string);
}
