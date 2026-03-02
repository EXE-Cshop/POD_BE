package com.shirt.pod.controller;

import com.shirt.pod.model.dto.ChatRequest;
import com.shirt.pod.model.dto.ChatResponse;
import com.shirt.pod.service.ChatBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
@Slf4j
public class ChatBotController {

    private final ChatBotService chatBotService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        log.info("Chatbot request: {} (has image: {})", request.getMessage(), request.getImage() != null);
        String reply = chatBotService.chat(request.getMessage(), request.getHistory(), request.getImage());
        return ResponseEntity.ok(new ChatResponse(reply));
    }
}
