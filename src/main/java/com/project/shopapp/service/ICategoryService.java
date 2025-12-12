package com.project.shopapp.service;

import com.project.shopapp.dto.CategoryDTO;
import com.project.shopapp.model.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category);
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
    Category updateCategory(Long categoryId, CategoryDTO category);
    Category deleteCategory(Long id) throws Exception;
}
