package com.shirt.pod.service;

import com.shirt.pod.model.dto.request.CreateProductRequest;
import com.shirt.pod.model.dto.request.CreateProductVariantRequest;
import com.shirt.pod.model.dto.request.UpdateProductRequest;
import com.shirt.pod.model.dto.request.UpdateProductVariantRequest;
import com.shirt.pod.model.dto.response.ProductDetailDTO;
import com.shirt.pod.model.dto.response.ProductDTO;
import com.shirt.pod.model.dto.response.ProductVariantDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    List<ProductDTO> getAllProducts(Boolean activeOnly);

    Page<ProductDTO> getProducts(Long categoryId, String keyword, Pageable pageable);

    ProductDTO getProductById(Long id);

    ProductDetailDTO getProductDetailById(Long id);

    ProductDetailDTO getProductDetailBySlug(String slug);

    ProductDTO createProduct(CreateProductRequest request);

    ProductDTO updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);

    void activateProduct(Long id);

    void deactivateProduct(Long id);

    List<ProductDTO> getTrendingProducts();

    List<ProductDTO> getFeaturedProducts();

    List<ProductVariantDTO> getVariantsByProductId(Long productId);

    ProductVariantDTO createVariant(Long productId, CreateProductVariantRequest request);

    ProductVariantDTO updateVariant(Long variantId, UpdateProductVariantRequest request);

    void deleteVariant(Long variantId);
}
