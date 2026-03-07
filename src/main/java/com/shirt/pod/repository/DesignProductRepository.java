package com.shirt.pod.repository;

import com.shirt.pod.model.entity.DesignProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignProductRepository extends JpaRepository<DesignProduct, Long> {

    List<DesignProduct> findByIsPublicTrueOrderByCreatedDateDesc();

    List<DesignProduct> findByUserIdOrderByCreatedDateDesc(Long userId);

    List<DesignProduct> findByUserIdAndIsPublicTrueOrderByCreatedDateDesc(Long userId);

    /** For debug: thiết kế lưu khi chưa đăng nhập (user_id IS NULL) */
    List<DesignProduct> findByUserIdIsNullOrderByCreatedDateDesc();
}
