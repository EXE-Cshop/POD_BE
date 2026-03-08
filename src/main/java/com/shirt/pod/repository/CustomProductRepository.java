package com.shirt.pod.repository;

import com.shirt.pod.model.entity.CustomProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomProductRepository extends JpaRepository<CustomProduct, Long> {
    List<CustomProduct> findByUserId(Long userId);
}
