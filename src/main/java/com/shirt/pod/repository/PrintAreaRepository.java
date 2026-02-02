package com.shirt.pod.repository;

import com.shirt.pod.model.entity.PrintArea;
import com.shirt.pod.model.entity.enums.PrintAreaName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrintAreaRepository extends JpaRepository<PrintArea, Long> {

    List<PrintArea> findByBaseProductId(Long baseProductId);

    Optional<PrintArea> findByBaseProductIdAndName(Long baseProductId, PrintAreaName name);

    boolean existsByBaseProductIdAndName(Long baseProductId, PrintAreaName name);

    boolean existsByBaseProductIdAndNameAndIdNot(Long baseProductId, PrintAreaName name, Long id);

    @Query(value = """
            SELECT * FROM print_areas pa
            WHERE (:baseProductId IS NULL OR pa.base_product_id = :baseProductId)
            AND (:name IS NULL OR pa.name = CAST(:name AS VARCHAR))
            """, countQuery = """
            SELECT COUNT(*) FROM print_areas pa
            WHERE (:baseProductId IS NULL OR pa.base_product_id = :baseProductId)
            AND (:name IS NULL OR pa.name = CAST(:name AS VARCHAR))
            """, nativeQuery = true)
    Page<PrintArea> searchWithFilters(
            @Param("baseProductId") Long baseProductId,
            @Param("name") String name,
            Pageable pageable);
}
