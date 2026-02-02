package com.shirt.pod.service.impl;

import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.exception.ValidationException;
import com.shirt.pod.mapper.BaseProductMapper;
import com.shirt.pod.model.dto.request.BaseProductCreateRequest;
import com.shirt.pod.model.dto.request.BaseProductFilterRequest;
import com.shirt.pod.model.dto.request.BaseProductUpdateRequest;
import com.shirt.pod.model.dto.response.BaseProductDTO;
import com.shirt.pod.model.entity.BaseProduct;
import com.shirt.pod.repository.BaseProductRepository;
import com.shirt.pod.service.BaseProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseProductServiceImpl implements BaseProductService {

    private final BaseProductRepository baseProductRepository;
    private final BaseProductMapper baseProductMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<BaseProductDTO> getAll(BaseProductFilterRequest filterRequest) {
        log.info("Fetching base products with filters - name: {}, material: {}, printTechnology: {}, active: {}",
                filterRequest.getName(), filterRequest.getMaterial(),
                filterRequest.getPrintTechnology(), filterRequest.getActive());

        // Convert empty strings to null for proper SQL handling
        String nameFilter = (filterRequest.getName() != null && filterRequest.getName().trim().isEmpty())
                ? null
                : filterRequest.getName();
        String materialFilter = (filterRequest.getMaterial() != null && filterRequest.getMaterial().trim().isEmpty())
                ? null
                : filterRequest.getMaterial();
        String printTechFilter = (filterRequest.getPrintTechnology() != null
                && filterRequest.getPrintTechnology().trim().isEmpty())
                        ? null
                        : filterRequest.getPrintTechnology();

        // Build pageable
        Sort.Direction direction = Sort.Direction.fromString(filterRequest.getOrder().toUpperCase());
        Pageable pageable = PageRequest.of(
                filterRequest.getPage() > 0 ? filterRequest.getPage() - 1 : 0,
                filterRequest.getSize(),
                Sort.by(direction, filterRequest.getSortBy()));

        // Use native query with filters
        Page<BaseProduct> productPage = baseProductRepository.searchWithFilters(
                nameFilter, materialFilter, printTechFilter, filterRequest.getActive(), pageable);

        log.info("Found {} base products (page {}/{}, size {})",
                productPage.getTotalElements(),
                productPage.getNumber() + 1,
                productPage.getTotalPages(),
                productPage.getSize());

        return productPage.map(baseProductMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public BaseProductDTO getById(Long id) {
        log.info("Fetching base product with id: {}", id);
        BaseProduct product = baseProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base product not found with id: " + id));
        return baseProductMapper.toDTO(product);
    }

    @Override
    @Transactional
    public BaseProductDTO create(BaseProductCreateRequest request) {
        log.info("Creating new base product with name: {}", request.getName());

        // Validate unique name
        if (baseProductRepository.existsByName(request.getName())) {
            throw new ValidationException("Base product with name '" + request.getName() + "' already exists");
        }

        BaseProduct product = baseProductMapper.toEntity(request);
        BaseProduct savedProduct = baseProductRepository.save(product);

        log.info("Created base product with id: {}", savedProduct.getId());
        return baseProductMapper.toDTO(savedProduct);
    }

    @Override
    @Transactional
    public BaseProductDTO update(Long id, BaseProductUpdateRequest request) {
        log.info("Updating base product with id: {}", id);

        BaseProduct product = baseProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base product not found with id: " + id));

        // Validate unique name if name is being updated
        if (request.getName() != null && !request.getName().equals(product.getName())) {
            if (baseProductRepository.existsByNameAndIdNot(request.getName(), id)) {
                throw new ValidationException("Base product with name '" + request.getName() + "' already exists");
            }
        }

        // Update only non-null fields
        baseProductMapper.updateEntity(request, product);
        BaseProduct updatedProduct = baseProductRepository.save(product);

        log.info("Updated base product with id: {}", id);
        return baseProductMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting base product with id: {}", id);

        BaseProduct product = baseProductRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base product not found with id: " + id));

        // Soft delete
        product.setActive(false);
        baseProductRepository.save(product);

        log.info("Deleted (soft) base product with id: {}", id);
    }
}
