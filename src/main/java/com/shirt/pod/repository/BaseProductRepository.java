package com.shirt.pod.repository;

import com.shirt.pod.model.entity.BaseProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BaseProductRepository extends JpaRepository<BaseProduct, Long> {

        List<BaseProduct> findByActiveTrue();

        Optional<BaseProduct> findByIdAndActiveTrue(Long id);

        boolean existsByName(String name);

        boolean existsByNameAndIdNot(String name, Long id);

        @Query(value = """
                        SELECT * FROM base_products bp
                        WHERE (:name IS NULL OR LOWER(bp.name) LIKE LOWER(CONCAT('%', :name, '%')))
                        AND (:material IS NULL OR LOWER(bp.material) LIKE LOWER(CONCAT('%', :material, '%')))
                        AND (:printTechnology IS NULL OR LOWER(bp.print_technology) LIKE LOWER(CONCAT('%', :printTechnology, '%')))
                        AND (:active IS NULL OR bp.active = :active)
                        """, countQuery = """
                        SELECT COUNT(*) FROM base_products bp
                        WHERE (:name IS NULL OR LOWER(bp.name) LIKE LOWER(CONCAT('%', :name, '%')))
                        AND (:material IS NULL OR LOWER(bp.material) LIKE LOWER(CONCAT('%', :material, '%')))
                        AND (:printTechnology IS NULL OR LOWER(bp.print_technology) LIKE LOWER(CONCAT('%', :printTechnology, '%')))
                        AND (:active IS NULL OR bp.active = :active)
                        """, nativeQuery = true)
        Page<BaseProduct> searchWithFilters(
                        @Param("name") String name,
                        @Param("material") String material,
                        @Param("printTechnology") String printTechnology,
                        @Param("active") Boolean active,
                        Pageable pageable);

        @Query("SELECT DISTINCT p FROM BaseProduct p JOIN p.variants v " +
                        "WHERE v.stockQuantity > 0 AND v.stockQuantity <= :threshold AND v.active = true AND p.active = true")
        List<BaseProduct> findProductsWithLowStock(@Param("threshold") Integer threshold);

        @Query("SELECT DISTINCT p FROM BaseProduct p JOIN p.variants v " +
                        "WHERE v.stockQuantity = 0 AND v.active = true AND p.active = true")
        List<BaseProduct> findProductsWithOutOfStock();

        
        @Query("SELECT DISTINCT p FROM BaseProduct p JOIN p.variants v " +
                        "WHERE v.stockQuantity > :threshold AND v.active = true AND p.active = true")
        List<BaseProduct> findProductsWithInStock(@Param("threshold") Integer threshold);
}
