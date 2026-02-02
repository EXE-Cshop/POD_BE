package com.shirt.pod.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inbound_request_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Log extends BaseEntityCreatedOnly {

    @Column(name = "http_code")
    private Integer httpCode;

    @Column(columnDefinition = "varchar(20)")
    private String method;

    @Column(name = "request_param", columnDefinition = "varchar(2000)")
    private String requestParam;

    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    @Column(columnDefinition = "TEXT")
    private String uri;
}
