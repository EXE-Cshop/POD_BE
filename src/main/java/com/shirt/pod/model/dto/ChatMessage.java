package com.shirt.pod.model.dto;

import lombok.Data;

@Data
public class ChatMessage {
    private String role; // "user" or "assistant"
    private String content;
}
