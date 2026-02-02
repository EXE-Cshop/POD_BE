package com.shirt.pod.service;

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

import java.util.List;

public interface ProductService {

    List<ProductDTO> getAllProducts(Boolean activeOnly);

    ProductDTO getProductById(Long id);

    ProductDetailDTO getProductDetailById(Long id);

    ProductDTO createProduct(CreateProductRequest request);

    ProductDTO updateProduct(Long id, UpdateProductRequest request);

    void deleteProduct(Long id);

    void activateProduct(Long id);

    void deactivateProduct(Long id);

    List<ProductVariantDTO> getVariantsByProductId(Long productId);

    ProductVariantDTO createVariant(Long productId, CreateProductVariantRequest request);

    ProductVariantDTO updateVariant(Long variantId, UpdateProductVariantRequest request);

    void deleteVariant(Long variantId);

    List<PrintAreaDTO> getPrintAreasByProductId(Long productId);

    PrintAreaDTO createPrintArea(Long productId, CreatePrintAreaRequest request);

    PrintAreaDTO updatePrintArea(Long printAreaId, UpdatePrintAreaRequest request);

    void deletePrintArea(Long printAreaId);
}
