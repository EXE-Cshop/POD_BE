package com.shirt.pod.repository;

import com.shirt.pod.model.entity.Sticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StickerRepository extends JpaRepository<Sticker, Long> {

    List<Sticker> findAllByOrderByIdAsc();
}
