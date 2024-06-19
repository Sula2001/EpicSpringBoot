package com.epic.Demo1.service;


import com.epic.Demo1.DTO.ProductDTO;
import com.epic.Demo1.entity.Product;
import com.epic.Demo1.exception.ProductNotFoundException;
import com.epic.Demo1.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@CacheConfig(cacheNames = "product")
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        try {
            Product product = mapToEntity(productDTO);
            Product savedProduct = productRepository.save(product);
            return mapToDTO(savedProduct);
        } catch (Exception e) {
            log.error("Error creating product: " + e.getMessage());
            return null; // You can throw a custom exception here if desired
        }
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        try {
            return productRepository.findAll().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving all products: " + e.getMessage());
            return null; // You can throw a custom exception here if desired
        }
    }

    @Override
    @Cacheable(key = "#id")
    public Optional<ProductDTO> getProductById(Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
            return Optional.of(mapToDTO(product));
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage());
            throw e; // Re-throw the exception to be handled by the test
        } catch (Exception e) {
            log.error("Error retrieving product by id: " + e.getMessage());
            throw new RuntimeException("Error retrieving product by id"); // You can throw a custom exception here if desired
        }
    }


    @Override
    @CachePut(key = "#id")
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setQuantity(productDTO.getQuantity());

            Product updatedProduct = productRepository.save(product);
            return mapToDTO(updatedProduct);
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage());
            throw e; // Re-throw the exception to be handled by the test
        } catch (Exception e) {
            log.error("Error updating product: " + e.getMessage());
            throw new RuntimeException("Error updating product"); // You can throw a custom exception here if desired
        }
    }


    @Override
    @CacheEvict(value = "product",allEntries = true)
    public void deleteProduct(Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
            productRepository.delete(product);
        } catch (ProductNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error deleting product: " + e.getMessage());
            throw new RuntimeException("Error deleting product"); // You can throw a custom exception here if desired
        }
    }

    private ProductDTO mapToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }

    private Product mapToEntity(ProductDTO productDTO) {
        return new Product(
                productDTO.getId(),
                productDTO.getName(),
                productDTO.getDescription(),
                productDTO.getPrice(),
                productDTO.getQuantity()
        );
    }
}
