package com.epic.Demo1;

import com.epic.Demo1.DTO.ProductDTO;
import com.epic.Demo1.entity.Product;
import com.epic.Demo1.exception.ProductNotFoundException;
import com.epic.Demo1.repository.ProductRepository;
import com.epic.Demo1.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product(1L, "Test Product", "Test Description", new BigDecimal("10.00"), 100);
        productDTO = new ProductDTO(1L, "Test Product", "Test Description", new BigDecimal("10.00"), 100);
    }

    @Test
    public void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO createdProduct = productService.createProduct(productDTO);

        assertNotNull(createdProduct);
        assertEquals(productDTO.getName(), createdProduct.getName());
    }

    @Test
    public void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        List<ProductDTO> products = productService.getAllProducts();

        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
    }

    @Test
    public void testGetProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<ProductDTO> foundProduct = productService.getProductById(1L);

        assertTrue(foundProduct.isPresent());
        assertEquals(productDTO.getName(), foundProduct.get().getName());
    }

    @Test
    public void testGetProductByIdNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    public void testUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO updatedProduct = productService.updateProduct(1L, productDTO);

        assertNotNull(updatedProduct);
        assertEquals(productDTO.getName(), updatedProduct.getName());
    }

    @Test
    public void testUpdateProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(1L, productDTO));
    }

    @Test
    public void testDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    public void testDeleteProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
    }
}

