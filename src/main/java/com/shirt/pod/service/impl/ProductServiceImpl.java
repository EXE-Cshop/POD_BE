package com.shirt.pod.service.impl;

import com.shirt.pod.exception.AppException;
import com.shirt.pod.exception.ErrorCode;
import com.shirt.pod.mapper.ProductMapper;
import com.shirt.pod.model.dto.request.CreatePrintAreaRequest;
import com.shirt.pod.model.dto.request.CreateProductRequest;
import com.shirt.pod.model.dto.request.CreateProductVariantRequest;
import com.shirt.pod.model.dto.request.UpdatePrintAreaRequest;
import com.shirt.pod.model.dto.request.UpdateProductRequest;
import com.shirt.pod.model.dto.request.UpdateProductVariantRequest;
import com.shirt.pod.model.dto.response.PrintAreaDTO;
import com.shirt.pod.model.dto.response.ProductDetailDTO;
import com.shirt.pod.model.dto.response.ProductDTO;
import com.shirt.pod.model.dto.response.ProductVariantDTO;
import com.shirt.pod.model.entity.BaseProduct;
import com.shirt.pod.model.entity.PrintArea;
import com.shirt.pod.model.entity.ProductVariant;
import com.shirt.pod.repository.BaseProductRepository;
import com.shirt.pod.repository.PrintAreaRepository;
import com.shirt.pod.repository.ProductVariantRepository;
import com.shirt.pod.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final BaseProductRepository baseProductRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PrintAreaRepository printAreaRepository;
    private final ProductMapper productMapper;

    // ========== Product CRUD ==========

    @Override
    public List<ProductDTO> getAllProducts(Boolean activeOnly) {
        log.debug("Getting all products, activeOnly: {}", activeOnly);

        List<BaseProduct> products;
        if (Boolean.TRUE.equals(activeOnly)) {
            products = baseProductRepository.findByActiveTrue();
        } else {
            products = baseProductRepository.findAll();
        }

        log.info("Found {} products", products.size());
        return productMapper.toDTOList(products);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        log.debug("Getting product by id: {}", id);

        BaseProduct product = baseProductRepository.findById(id)
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

        BaseProduct product = baseProductRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", id);
                });

        ProductDetailDTO dto = productMapper.toDetailDTO(product);
        List<ProductVariant> variants = productVariantRepository.findByBaseProductId(id);
        List<PrintArea> printAreas = printAreaRepository.findByBaseProductId(id);
        dto.setVariants(productMapper.toVariantDTOList(variants));
        dto.setPrintAreas(productMapper.toPrintAreaDTOList(printAreas));

        log.info("Found product detail: {} with {} variants and {} print areas",
                product.getName(), variants.size(), printAreas.size());
        return dto;
    }

    @Override
    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        log.debug("Creating product: {}", request.getName());

        // Check duplicate name
        if (baseProductRepository.existsByName(request.getName())) {
            log.warn("Product name already exists: {}", request.getName());
            throw new AppException(ErrorCode.DUPLICATE_NAME, request.getName());
        }

        BaseProduct product = productMapper.toEntity(request);
        BaseProduct savedProduct = baseProductRepository.save(product);

        log.info("Created product with id: {}, name: {}", savedProduct.getId(), savedProduct.getName());
        return productMapper.toDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        log.debug("Updating product with id: {}", id);

        BaseProduct product = baseProductRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", id);
                });

        // Check duplicate name (if name is being updated)
        if (request.getName() != null && !request.getName().equals(product.getName())) {
            if (baseProductRepository.existsByNameAndIdNot(request.getName(), id)) {
                log.warn("Product name already exists: {}", request.getName());
                throw new AppException(ErrorCode.DUPLICATE_NAME, request.getName());
            }
        }

        productMapper.updateEntity(request, product);
        BaseProduct updatedProduct = baseProductRepository.save(product);

        log.info("Updated product with id: {}, name: {}", updatedProduct.getId(), updatedProduct.getName());
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);

        BaseProduct product = baseProductRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", id);
                });

        baseProductRepository.delete(product);
        log.info("Deleted product with id: {}, name: {}", id, product.getName());
    }

    @Override
    @Transactional
    public void activateProduct(Long id) {
        log.debug("Activating product with id: {}", id);

        BaseProduct product = baseProductRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", id);
                });

        if (Boolean.TRUE.equals(product.getActive())) {
            log.warn("Product {} is already active", id);
            // Product is already active, no need to activate again
            return;
        }

        product.setActive(true);
        baseProductRepository.save(product);

        log.info("Activated product with id: {}", id);
    }

    @Override
    @Transactional
    public void deactivateProduct(Long id) {
        log.debug("Deactivating product with id: {}", id);

        BaseProduct product = baseProductRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", id);
                });

        if (Boolean.FALSE.equals(product.getActive())) {
            log.warn("Product {} is already inactive", id);
            // Product is already inactive, no need to deactivate again
            return;
        }

        product.setActive(false);
        baseProductRepository.save(product);

        log.info("Deactivated product with id: {}", id);
    }

    // ========== Variant Management ==========

    @Override
    public List<ProductVariantDTO> getVariantsByProductId(Long productId) {
        log.debug("Getting variants for product id: {}", productId);

        // Verify product exists
        if (!baseProductRepository.existsById(productId)) {
            log.warn("Product not found with id: {}", productId);
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", productId);
        }

        List<ProductVariant> variants = productVariantRepository.findByBaseProductId(productId);
        log.info("Found {} variants for product id: {}", variants.size(), productId);
        return productMapper.toVariantDTOList(variants);
    }

    @Override
    @Transactional
    public ProductVariantDTO createVariant(Long productId, CreateProductVariantRequest request) {
        log.debug("Creating variant for product id: {}, SKU: {}", productId, request.getSku());

        // Verify product exists
        BaseProduct product = baseProductRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", productId);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", productId);
                });

        // Check duplicate SKU
        if (productVariantRepository.existsBySku(request.getSku())) {
            log.warn("SKU already exists: {}", request.getSku());
            throw new AppException(ErrorCode.SKU_ALREADY_EXISTS, request.getSku());
        }

        ProductVariant variant = productMapper.toVariantEntity(request);
        variant.setBaseProduct(product);
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

        // Check duplicate SKU (if SKU is being updated)
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

    // ========== PrintArea Management ==========

    @Override
    public List<PrintAreaDTO> getPrintAreasByProductId(Long productId) {
        log.debug("Getting print areas for product id: {}", productId);

        // Verify product exists
        if (!baseProductRepository.existsById(productId)) {
            log.warn("Product not found with id: {}", productId);
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", productId);
        }

        List<PrintArea> printAreas = printAreaRepository.findByBaseProductId(productId);
        log.info("Found {} print areas for product id: {}", printAreas.size(), productId);
        return productMapper.toPrintAreaDTOList(printAreas);
    }

    @Override
    @Transactional
    public PrintAreaDTO createPrintArea(Long productId, CreatePrintAreaRequest request) {
        log.debug("Creating print area for product id: {}, name: {}", productId, request.getName());

        // Verify product exists
        BaseProduct product = baseProductRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", productId);
                    return new AppException(ErrorCode.PRODUCT_NOT_FOUND, "id", productId);
                });

        PrintArea printArea = productMapper.toPrintAreaEntity(request);
        printArea.setBaseProduct(product);
        PrintArea savedPrintArea = printAreaRepository.save(printArea);

        log.info("Created print area with id: {}, name: {}", savedPrintArea.getId(), savedPrintArea.getName());
        return productMapper.toPrintAreaDTO(savedPrintArea);
    }

    @Override
    @Transactional
    public PrintAreaDTO updatePrintArea(Long printAreaId, UpdatePrintAreaRequest request) {
        log.debug("Updating print area with id: {}", printAreaId);

        PrintArea printArea = printAreaRepository.findById(printAreaId)
                .orElseThrow(() -> {
                    log.warn("Print area not found with id: {}", printAreaId);
                    return new AppException(ErrorCode.PRINT_AREA_NOT_FOUND, "id", printAreaId);
                });

        productMapper.updatePrintAreaEntity(request, printArea);
        PrintArea updatedPrintArea = printAreaRepository.save(printArea);

        log.info("Updated print area with id: {}, name: {}", updatedPrintArea.getId(), updatedPrintArea.getName());
        return productMapper.toPrintAreaDTO(updatedPrintArea);
    }

    @Override
    @Transactional
    public void deletePrintArea(Long printAreaId) {
        log.debug("Deleting print area with id: {}", printAreaId);

        PrintArea printArea = printAreaRepository.findById(printAreaId)
                .orElseThrow(() -> {
                    log.warn("Print area not found with id: {}", printAreaId);
                    return new AppException(ErrorCode.PRINT_AREA_NOT_FOUND, "id", printAreaId);
                });

        printAreaRepository.delete(printArea);
        log.info("Deleted print area with id: {}, name: {}", printAreaId, printArea.getName());
    }
}
