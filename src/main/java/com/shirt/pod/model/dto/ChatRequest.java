package com.shirt.pod.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    private String message;
    private List<ChatMessage> history;
    private String image; // Optional: base64 image data URL for design review

}
