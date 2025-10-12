package com.project.shopapp.service;

import com.project.shopapp.dto.ProductDTO;
import com.project.shopapp.dto.ProductImageDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.model.Product;
import com.project.shopapp.model.ProductImage;
import com.project.shopapp.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    Product getProductById(Long id) throws Exception;
    Page<ProductResponse> getAllProducts(PageRequest pageRequest);
    Product updateProduct(Long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(Long id);
    boolean existsByName(String name);
    ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception;
}
