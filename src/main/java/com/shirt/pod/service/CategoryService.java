package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.CreateCategoryRequest;
import com.shirt.pod.model.dto.request.UpdateCategoryRequest;
import com.shirt.pod.model.dto.response.CategoryDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllActiveCategories();
    List<CategoryDTO> getAllCategories();
    CategoryDTO getBySlug(String slug);
    CategoryDTO getById(Long id);
    CategoryDTO create(CreateCategoryRequest request);
    CategoryDTO update(Long id, UpdateCategoryRequest request);
    void delete(Long id);
}
