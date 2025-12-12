package com.project.shopapp.service;

import com.project.shopapp.dto.ProductDTO;
import com.project.shopapp.dto.ProductImageDTO;
import com.project.shopapp.exception.DataNotFoundException;
import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.model.Category;
import com.project.shopapp.model.Product;
import com.project.shopapp.model.ProductImage;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.repository.ProductImageRepository;
import com.project.shopapp.repository.ProductRepository;
import com.project.shopapp.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException("Cannot find category with id: " + productDTO.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(Long productId) throws Exception {
        Optional<Product> optionalProduct = productRepository.getDetailProduct(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            validateAndFixThumbnail(product);
            return product;
        }
        throw new DataNotFoundException("Cannot find product with id: " + productId);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        Page<Product> productsPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return productsPage.map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(id);

        if (existingProduct != null) {
            Category existingCategory = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() ->
                            new DataNotFoundException("Cannot find category with id: " + productDTO.getCategoryId()));
            if (productDTO.getName() != null && !productDTO.getName().isEmpty()) {
                existingProduct.setName(productDTO.getName());
            }

            existingProduct.setCategory(existingCategory);

            if (productDTO.getPrice() >= 0) {
                existingProduct.setPrice(productDTO.getPrice());
            }

            if (productDTO.getDescription() != null && !productDTO.getDescription().isEmpty()) {
                existingProduct.setDescription(productDTO.getDescription());
            }

            if (productDTO.getThumbnail() != null && !productDTO.getThumbnail().isEmpty()) {
                existingProduct.setThumbnail(productDTO.getThumbnail());
            }

            validateAndFixThumbnail(existingProduct);
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() ->
                        new DataNotFoundException("Cannot find product with id: " + productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();

        // Không cho insert quá 5 ảnh cho 1 sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if (size > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException("Number of images must be less than or equal to " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }

        if (existingProduct.getThumbnail() == null ) {
            existingProduct.setThumbnail(newProductImage.getImageUrl());
        }

        return productImageRepository.save(newProductImage);
    }

    private void validateAndFixThumbnail(Product product) {
        if (product == null) return;

        String currentThumbnail = product.getThumbnail();
        List<ProductImage> productImages = productImageRepository.findByProductId(product.getId());

        boolean isValid = currentThumbnail != null &&
                productImages.stream().anyMatch(img -> img.getImageUrl().equals(currentThumbnail));

        if (!isValid) {
            if (!productImages.isEmpty()) {
                product.setThumbnail(productImages.get(0).getImageUrl());
            } else {
                product.setThumbnail(null);
            }
            productRepository.save(product);
        }
    }
}
