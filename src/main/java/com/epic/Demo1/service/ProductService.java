package com.epic.Demo1.service;

import com.epic.Demo1.DTO.ProductDTO;
import com.epic.Demo1.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public interface ProductService {


    public ProductDTO createProduct(ProductDTO productDTO);

    public List<ProductDTO> getAllProducts() ;

    public Optional<ProductDTO> getProductById(Long id);

    public ProductDTO updateProduct(Long id, ProductDTO productDTO) ;

    public void deleteProduct(Long id) ;
}
