package com.shirt.pod.repository;

import com.shirt.pod.model.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByBaseProductId(Long baseProductId);

    List<ProductVariant> findByBaseProductIdAndActiveTrue(Long baseProductId);

    Optional<ProductVariant> findBySku(String sku);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsByIdAndActiveTrue(Long id);

    @Query(value = """
            SELECT * FROM product_variants pv
            WHERE (:baseProductId IS NULL OR pv.base_product_id = :baseProductId)
            AND (:colorName IS NULL OR LOWER(pv.color_name) LIKE LOWER(CONCAT('%', :colorName, '%')))
            AND (:size IS NULL OR pv.size = :size)
            AND (:sku IS NULL OR LOWER(pv.sku) LIKE LOWER(CONCAT('%', :sku, '%')))
            AND (:active IS NULL OR pv.active = :active)
            """, countQuery = """
            SELECT COUNT(*) FROM product_variants pv
            WHERE (:baseProductId IS NULL OR pv.base_product_id = :baseProductId)
            AND (:colorName IS NULL OR LOWER(pv.color_name) LIKE LOWER(CONCAT('%', :colorName, '%')))
            AND (:size IS NULL OR pv.size = :size)
            AND (:sku IS NULL OR LOWER(pv.sku) LIKE LOWER(CONCAT('%', :sku, '%')))
            AND (:active IS NULL OR pv.active = :active)
            """, nativeQuery = true)
    Page<ProductVariant> searchWithFilters(
            @Param("baseProductId") Long baseProductId,
            @Param("colorName") String colorName,
            @Param("size") String size,
            @Param("sku") String sku,
            @Param("active") Boolean active,
            Pageable pageable);
}
