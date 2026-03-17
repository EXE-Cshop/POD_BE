package com.shirt.pod.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "gift_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiftMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, unique = true, length = 36)
    private String uuid;

    private String mediaUrl;

    private String recipientName;

    private String themeName;

    private String photoUrl;

    private String videoUrl;

    @Column(columnDefinition = "TEXT")
    private String messageText;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
