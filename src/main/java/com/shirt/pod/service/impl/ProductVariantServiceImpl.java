package com.shirt.pod.service.impl;

import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.exception.ValidationException;
import com.shirt.pod.mapper.ProductVariantMapper;
import com.shirt.pod.model.dto.request.ProductVariantCreateRequest;
import com.shirt.pod.model.dto.request.ProductVariantFilterRequest;
import com.shirt.pod.model.dto.request.ProductVariantUpdateRequest;
import com.shirt.pod.model.dto.response.ProductVariantDTO;
import com.shirt.pod.model.entity.BaseProduct;
import com.shirt.pod.model.entity.ProductVariant;
import com.shirt.pod.repository.BaseProductRepository;
import com.shirt.pod.repository.ProductVariantRepository;
import com.shirt.pod.service.ProductVariantService;
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
public class ProductVariantServiceImpl implements ProductVariantService {

        private final ProductVariantRepository productVariantRepository;
        private final BaseProductRepository baseProductRepository;
        private final ProductVariantMapper productVariantMapper;

        @Override
        @Transactional(readOnly = true)
        public Page<ProductVariantDTO> getAll(ProductVariantFilterRequest filterRequest) {
                log.info(
                                "Fetching product variants with filters - baseProductId: {}, colorName: {}, size: {}, sku: {}, active: {}",
                                filterRequest.getBaseProductId(), filterRequest.getColorName(),
                                filterRequest.getSize(), filterRequest.getSku(), filterRequest.getActive());

                // Convert empty strings to null for proper SQL handling
                String colorNameFilter = (filterRequest.getColorName() != null
                                && filterRequest.getColorName().trim().isEmpty())
                                                ? null
                                                : filterRequest.getColorName();
                String sizeFilter = (filterRequest.getSize() != null && filterRequest.getSize().trim().isEmpty())
                                ? null
                                : filterRequest.getSize();
                String skuFilter = (filterRequest.getSku() != null && filterRequest.getSku().trim().isEmpty())
                                ? null
                                : filterRequest.getSku();

                // Build pageable - convert camelCase to snake_case for native query
                String sortColumn = filterRequest.getSortBy();
                // Map common camelCase field names to snake_case column names
                sortColumn = switch (sortColumn) {
                        case "createdDate" -> "created_date";
                        case "colorName" -> "color_name";
                        case "stockQuantity" -> "stock_quantity";
                        case "priceAdjustment" -> "price_adjustment";
                        case "baseProductId" -> "base_product_id";
                        case "frontImageUrl" -> "front_image_url";
                        case "backImageUrl" -> "back_image_url";
                        default -> sortColumn;
                };
                Sort.Direction direction = Sort.Direction.fromString(filterRequest.getOrder().toUpperCase());
                Pageable pageable = PageRequest.of(
                                filterRequest.getPage() > 0 ? filterRequest.getPage() - 1 : 0,
                                filterRequest.getPageSize(),
                                Sort.by(direction, sortColumn));

                // Use native query with filters
                Page<ProductVariant> variantPage = productVariantRepository.searchWithFilters(
                                filterRequest.getBaseProductId(), colorNameFilter, sizeFilter, skuFilter,
                                filterRequest.getActive(), pageable);

                log.info("Found {} product variants (page {}/{}, size {})",
                                variantPage.getTotalElements(),
                                variantPage.getNumber() + 1,
                                variantPage.getTotalPages(),
                                variantPage.getSize());

                return variantPage.map(productVariantMapper::toDTO);
        }

        @Override
        @Transactional(readOnly = true)
        public ProductVariantDTO getById(Long id) {
                log.info("Fetching product variant with id: {}", id);
                ProductVariant variant = productVariantRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product variant not found with id: " + id));
                return productVariantMapper.toDTO(variant);
        }

        @Override
        @Transactional
        public ProductVariantDTO create(ProductVariantCreateRequest request) {
                log.info("Creating new product variant with SKU: {}", request.getSku());

                // Validate base product exists
                BaseProduct baseProduct = baseProductRepository.findById(request.getBaseProductId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Base product not found with id: " + request.getBaseProductId()));

                // Validate unique SKU
                if (productVariantRepository.existsBySku(request.getSku())) {
                        throw new ValidationException(
                                        "Product variant with SKU '" + request.getSku() + "' already exists");
                }

                ProductVariant variant = productVariantMapper.toEntity(request);
                variant.setBaseProduct(baseProduct);
                ProductVariant savedVariant = productVariantRepository.save(variant);

                log.info("Created product variant with id: {}", savedVariant.getId());
                return productVariantMapper.toDTO(savedVariant);
        }

        @Override
        @Transactional
        public ProductVariantDTO update(Long id, ProductVariantUpdateRequest request) {
                log.info("Updating product variant with id: {}", id);

                ProductVariant variant = productVariantRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product variant not found with id: " + id));

                // Validate unique SKU if SKU is being updated
                if (request.getSku() != null && !request.getSku().equals(variant.getSku())) {
                        if (productVariantRepository.existsBySkuAndIdNot(request.getSku(), id)) {
                                throw new ValidationException(
                                                "Product variant with SKU '" + request.getSku() + "' already exists");
                        }
                }

                // Update only non-null fields
                productVariantMapper.updateEntity(request, variant);
                ProductVariant updatedVariant = productVariantRepository.save(variant);

                log.info("Updated product variant with id: {}", id);
                return productVariantMapper.toDTO(updatedVariant);
        }

        @Override
        @Transactional
        public void delete(Long id) {
                log.info("Deleting product variant with id: {}", id);

                ProductVariant variant = productVariantRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Product variant not found with id: " + id));

                // Soft delete
                variant.setActive(false);
                productVariantRepository.save(variant);

                log.info("Deleted (soft) product variant with id: {}", id);
        }
}
