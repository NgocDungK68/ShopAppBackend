package com.project.shopapp.service;

import com.project.shopapp.dto.CategoryDTO;
import com.project.shopapp.model.Category;
import com.project.shopapp.model.Product;
import com.project.shopapp.repository.CategoryRepository;
import com.project.shopapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = Category
                .builder()
                .name(categoryDTO.getName())
                .build();

        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category existingCategory = getCategoryById(categoryId);
        existingCategory.setName(categoryDTO.getName());
        categoryRepository.save(existingCategory);
        return existingCategory;
    }

    @Override
    @Transactional
    public Category deleteCategory(Long id) throws Exception {
        Category category = categoryRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        List<Product> products = productRepository.findByCategory(category);
        if (!products.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated products");
        } else {
            categoryRepository.deleteById(id);
            return category;
        }
    }
}
