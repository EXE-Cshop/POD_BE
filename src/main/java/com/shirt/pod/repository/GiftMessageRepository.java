package com.shirt.pod.repository;

import com.shirt.pod.model.entity.GiftMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiftMessageRepository extends JpaRepository<GiftMessage, Long> {

    Optional<GiftMessage> findByUuid(String uuid);
 
    Optional<GiftMessage> findByOrderId(Long orderId);
 
    List<GiftMessage> findByOrderIdIn(Collection<Long> orderIds);
}
