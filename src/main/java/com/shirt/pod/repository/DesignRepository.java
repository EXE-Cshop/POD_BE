package com.shirt.pod.repository;

import com.shirt.pod.model.entity.Design;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesignRepository extends JpaRepository<Design, Long> {

    Page<Design> findByIsPublicTrue(Pageable pageable);
}
