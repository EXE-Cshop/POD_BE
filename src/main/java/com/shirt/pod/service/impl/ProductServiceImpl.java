package com.shirt.pod.service.impl;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.exception.ResourceNotFoundException;
import com.shirt.pod.mapper.ProductMapper;
import com.shirt.pod.model.dto.request.CreateProductRequest;
import com.shirt.pod.model.dto.request.CreateProductVariantRequest;
import com.shirt.pod.model.dto.request.UpdateProductRequest;
import com.shirt.pod.model.dto.request.UpdateProductVariantRequest;
import com.shirt.pod.model.dto.response.*;
import com.shirt.pod.model.entity.*;
import com.shirt.pod.repository.*;
import com.shirt.pod.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final ReviewRepository reviewRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    // ========== Product CRUD ==========

    @Override
    public List<ProductDTO> getAllProducts(Boolean activeOnly) {
        log.debug("Getting all products, activeOnly: {}", activeOnly);

        List<Product> products;
        if (Boolean.TRUE.equals(activeOnly)) {
            products = productRepository.findAll().stream()
                    .filter(Product::getActive)
                    .collect(Collectors.toList());
        } else {
            products = productRepository.findAll();
        }

        log.info("Found {} products", products.size());
        return productMapper.toDTOList(products);
    }

    @Override
    public Page<ProductDTO> getProducts(Long categoryId, String keyword, Pageable pageable) {
        log.debug("Getting paginated products, categoryId: {}, keyword: {}", categoryId, keyword);
        Page<Product> productPage = productRepository.findWithFilters(categoryId, keyword, pageable);
        return productPage.map(productMapper::toDTO);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        log.debug("Getting product by id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", id);
                });

        log.info("Found product: {}", product.getName());
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getProductDetailById(Long id) {
        log.debug("Getting product detail by id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", id);
                });

        return buildProductDetailDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO getProductDetailBySlug(String slug) {
        log.debug("Getting product detail by slug: {}", slug);

        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Product not found with slug: {}", slug);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "slug", slug);
                });

        return buildProductDetailDTO(product);
    }

    private ProductDetailDTO buildProductDetailDTO(Product product) {
        ProductDetailDTO dto = productMapper.toDetailDTO(product);
        
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());
        dto.setVariants(productMapper.toVariantDTOList(variants));

        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderAsc(product.getId());
        List<ProductImageDTO> imageDTOs = images.stream()
                .map(img -> ProductImageDTO.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .sortOrder(img.getSortOrder())
                        .isPrimary(img.getIsPrimary())
                        .build())
                .collect(Collectors.toList());
        dto.setImages(imageDTOs);

        List<Review> reviews = reviewRepository.findByProductId(product.getId());
        List<ReviewDTO> reviewDTOs = reviews.stream()
                .map(rev -> ReviewDTO.builder()
                        .id(rev.getId())
                        .productId(product.getId())
                        .userId(rev.getUser().getId())
                        .userName(rev.getUser().getFullName())
                        .rating(rev.getRating())
                        .comment(rev.getComment())
                        .verified(rev.getVerified())
                        .createdDate(rev.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
        dto.setReviews(reviewDTOs);

        if (product.getCategory() != null) {
            Category cat = product.getCategory();
            dto.setCategory(CategoryDTO.builder()
                    .id(cat.getId())
                    .name(cat.getName())
                    .slug(cat.getSlug())
                    .description(cat.getDescription())
                    .imageUrl(cat.getImageUrl())
                    .sortOrder(cat.getSortOrder())
                    .active(cat.getActive())
                    .build());
        }

        log.info("Built product detail for product: {} with {} variants, {} images, {} reviews",
                product.getName(), variants.size(), images.size(), reviews.size());
        return dto;
    }

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        log.debug("Creating product: {}", request.getName());

        if (productRepository.existsBySlug(request.getName())) {
            log.warn("Product slug/name already exists: {}", request.getName());
            throw new AppException(ErrorCode.DUPLICATE_NAME, request.getName());
        }

        Product product = productMapper.toEntity(request);
        product.setSlug(generateSlug(request.getName()));

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }

        Product savedProduct = productRepository.save(product);

        log.info("Created product with id: {}, name: {}", savedProduct.getId(), savedProduct.getName());
        return productMapper.toDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        log.debug("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", id);
                });

        productMapper.updateEntity(request, product);
        
        if (request.getName() != null) {
            product.setSlug(generateSlug(request.getName()));
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);

        log.info("Updated product with id: {}, name: {}", updatedProduct.getId(), updatedProduct.getName());
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", id);
                });

        productRepository.delete(product);
        log.info("Deleted product with id: {}, name: {}", id, product.getName());
    }

    @Override
    @Transactional
    public void activateProduct(Long id) {
        log.debug("Activating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", id);
                });

        product.setActive(true);
        productRepository.save(product);

        log.info("Activated product with id: {}", id);
    }

    @Override
    @Transactional
    public void deactivateProduct(Long id) {
        log.debug("Deactivating product with id: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", id);
                });

        product.setActive(false);
        productRepository.save(product);

        log.info("Deactivated product with id: {}", id);
    }

    @Override
    public List<ProductDTO> getTrendingProducts() {
        log.debug("Getting trending products");
        List<Product> products = productRepository.findByIsTrendingTrueAndActiveTrue();
        return productMapper.toDTOList(products);
    }

    @Override
    public List<ProductDTO> getFeaturedProducts() {
        log.debug("Getting featured products");
        List<Product> products = productRepository.findByIsFeaturedTrueAndActiveTrue();
        return productMapper.toDTOList(products);
    }

    // ========== Variant Management ==========

    @Override
    public List<ProductVariantDTO> getVariantsByProductId(Long productId) {
        log.debug("Getting variants for product id: {}", productId);

        if (!productRepository.existsById(productId)) {
            log.warn("Product not found with id: {}", productId);
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", productId);
        }

        List<ProductVariant> variants = productVariantRepository.findByProductId(productId);
        log.info("Found {} variants for product id: {}", variants.size(), productId);
        return productMapper.toVariantDTOList(variants);
    }

    @Override
    @Transactional
    public ProductVariantDTO createVariant(Long productId, CreateProductVariantRequest request) {
        log.debug("Creating variant for product id: {}, SKU: {}", productId, request.getSku());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", productId);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", productId);
                });

        if (productVariantRepository.existsBySku(request.getSku())) {
            log.warn("SKU already exists: {}", request.getSku());
            throw new AppException(ErrorCode.SKU_ALREADY_EXISTS, request.getSku());
        }

        ProductVariant variant = productMapper.toVariantEntity(request);
        variant.setProduct(product);
        ProductVariant savedVariant = productVariantRepository.save(variant);

        log.info("Created variant with id: {}, SKU: {}", savedVariant.getId(), savedVariant.getSku());
        return productMapper.toVariantDTO(savedVariant);
    }

    @Override
    @Transactional
    public ProductVariantDTO updateVariant(Long variantId, UpdateProductVariantRequest request) {
        log.debug("Updating variant with id: {}", variantId);

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> {
                    log.warn("Variant not found with id: {}", variantId);
                    return new AppException(ErrorCode.VARIANT_NOT_FOUND, "id", variantId);
                });

        if (request.getSku() != null && !request.getSku().equals(variant.getSku())) {
            if (productVariantRepository.existsBySku(request.getSku())) {
                log.warn("SKU already exists: {}", request.getSku());
                throw new AppException(ErrorCode.SKU_ALREADY_EXISTS, request.getSku());
            }
        }

        productMapper.updateVariantEntity(request, variant);
        ProductVariant updatedVariant = productVariantRepository.save(variant);

        log.info("Updated variant with id: {}, SKU: {}", updatedVariant.getId(), updatedVariant.getSku());
        return productMapper.toVariantDTO(updatedVariant);
    }

    @Override
    @Transactional
    public void deleteVariant(Long variantId) {
        log.debug("Deleting variant with id: {}", variantId);

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> {
                    log.warn("Variant not found with id: {}", variantId);
                    return new AppException(ErrorCode.VARIANT_NOT_FOUND, "id", variantId);
                });

        productVariantRepository.delete(variant);
        log.info("Deleted variant with id: {}, SKU: {}", variantId, variant.getSku());
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
