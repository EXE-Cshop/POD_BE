package com.shirt.pod.service.impl;

import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.model.dto.request.CreateCategoryRequest;
import com.shirt.pod.model.dto.request.UpdateCategoryRequest;
import com.shirt.pod.model.dto.response.CategoryDTO;
import com.shirt.pod.model.entity.Category;
import com.shirt.pod.repository.CategoryRepository;
import com.shirt.pod.repository.ProductRepository;
import com.shirt.pod.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<CategoryDTO> getAllActiveCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + slug));
        return toDTO(category);
    }

    @Override
    public CategoryDTO getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        return toDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO create(CreateCategoryRequest request) {
        String slug = request.getSlug() != null ? request.getSlug() : generateSlug(request.getName());
        Category category = Category.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .active(true)
                .build();
        return toDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDTO update(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        if (request.getName() != null) category.setName(request.getName());
        if (request.getSlug() != null) category.setSlug(request.getSlug());
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getImageUrl() != null) category.setImageUrl(request.getImageUrl());
        if (request.getSortOrder() != null) category.setSortOrder(request.getSortOrder());
        if (request.getActive() != null) category.setActive(request.getActive());
        return toDTO(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO toDTO(Category c) {
        long productCount = productRepository.countByCategoryId(c.getId());
        return CategoryDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .slug(c.getSlug())
                .description(c.getDescription())
                .imageUrl(c.getImageUrl())
                .sortOrder(c.getSortOrder())
                .active(c.getActive())
                .productCount(productCount)
                .build();
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
