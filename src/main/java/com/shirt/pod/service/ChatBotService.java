package com.shirt.pod.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shirt.pod.model.dto.ChatMessage;
import com.shirt.pod.model.dto.ChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class ChatBotService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = """
            Bạn là trợ lý AI chính thức của cửa hàng áo thun POD Print (Print On Demand).
            Tên bạn: POD Print AI Assistant.

            === PHẠM VI TRẢ LỜI (CHỈ trả lời các chủ đề sau) ===

            1. **Hướng dẫn sử dụng trang web:**
               - Cách duyệt sản phẩm trong Catalog
               - Cách thêm sản phẩm vào giỏ hàng và thanh toán (Checkout)
               - Cách xem lại đơn hàng (My Orders)
               - Cách đăng ký, đăng nhập tài khoản

            2. **Công cụ Design Editor (Thiết kế áo):**
               - Cách vào Design Editor: chọn sản phẩm trong Catalog → bấm "Customize This Product"
               - Cách upload hình ảnh, thêm text, chỉnh màu sắc lên áo
               - Cách xem trước thiết kế trên sản phẩm
               - Cách lưu và đặt hàng thiết kế của bạn

            3. **Virtual Try-On (Thử đồ ảo):**
               - Cách sử dụng: Upload ảnh cá nhân + chọn áo → AI ghép áo lên người
               - Mẹo chụp ảnh đẹp: ảnh toàn thân, nền sáng, đứng thẳng, ánh sáng đều
               - Tránh: ảnh bị cắt, tay khoanh, ngồi, nền rối

            4. **Tư vấn chọn size áo:**
               - S: Ngực 86-90cm, Cao 155-165cm, Nặng 45-55kg
               - M: Ngực 90-96cm, Cao 160-170cm, Nặng 55-65kg
               - L: Ngực 96-102cm, Cao 165-175cm, Nặng 65-75kg
               - XL: Ngực 102-108cm, Cao 170-180cm, Nặng 75-85kg
               - 2XL: Ngực 108-114cm, Cao 175-185cm, Nặng 85-95kg

            5. **Gợi ý phối đồ / style:**
               - Tư vấn kiểu áo theo dáng người
               - Regular fit, Slim fit, Oversize
               - Gợi ý phối màu

            6. **Thông tin giao hàng & đơn hàng:**
               - Nội thành: 1-2 ngày làm việc
               - Ngoại thành: 3-5 ngày làm việc
               - Miễn phí ship cho đơn từ 500.000đ

            7. **Đánh giá thiết kế áo (Design Review):**
               Khi khách hàng gửi ảnh thiết kế áo, hãy đánh giá chuyên nghiệp:
               - Bố cục: cân đối, tỉ lệ vàng, vị trí các element
               - Phối màu: hài hòa, tương phản tốt, dễ nhìn
               - Typography: font chữ phù hợp, kích thước, khoảng cách
               - Tổng thể: áo có bắt mắt không, có chuyên nghiệp không
               - Cho điểm /10 và gợi ý 2-3 cách cải thiện cụ thể
               - Trả lời thân thiện, khích lệ sáng tạo

            === QUY TẮC BẮT BUỘC ===
            - Trả lời bằng tiếng Việt, thân thiện, ngắn gọn
            - Dùng emoji phù hợp
            - Dùng **bold** cho từ khóa quan trọng
            - KHÔNG trả lời câu hỏi ngoài phạm vi
            - Nếu ngoài phạm vi: "Mình chỉ hỗ trợ các vấn đề liên quan đến POD Print thôi nhé! 😊"
            - KHÔNG bịa thông tin về giá cả cụ thể
            - Giữ câu trả lời tối đa 200 từ
            """;

    /**
     * Chat with text only
     */
    public String chat(String userMessage, List<ChatMessage> history) {
        return chat(userMessage, history, null);
    }

    /**
     * Chat with text + optional image (for design review)
     */
    public String chat(String userMessage, List<ChatMessage> history, String imageBase64) {
        try {
            String url = String.format(
                    "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                    model, apiKey);

            ObjectNode root = mapper.createObjectNode();

            // System instruction
            ObjectNode systemInstruction = mapper.createObjectNode();
            ArrayNode systemParts = mapper.createArrayNode();
            systemParts.add(mapper.createObjectNode().put("text", SYSTEM_PROMPT));
            systemInstruction.set("parts", systemParts);
            root.set("systemInstruction", systemInstruction);

            // Conversation contents
            ArrayNode contents = mapper.createArrayNode();

            // History
            if (history != null) {
                int start = Math.max(0, history.size() - 10);
                for (int i = start; i < history.size(); i++) {
                    ChatMessage msg = history.get(i);
                    ObjectNode content = mapper.createObjectNode();
                    content.put("role", "assistant".equals(msg.getRole()) ? "model" : "user");
                    ArrayNode parts = mapper.createArrayNode();
                    parts.add(mapper.createObjectNode().put("text", msg.getContent()));
                    content.set("parts", parts);
                    contents.add(content);
                }
            }

            // Current user message (with optional image)
            ObjectNode userContent = mapper.createObjectNode();
            userContent.put("role", "user");
            ArrayNode userParts = mapper.createArrayNode();

            // Add text part
            userParts.add(mapper.createObjectNode().put("text", userMessage));

            // Add image part if provided (multimodal)
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                log.info("Design review request - including image in Gemini request");
                String base64Data = imageBase64;
                String mimeType = "image/png";

                // Strip data URL prefix if present: "data:image/png;base64,..."
                if (imageBase64.contains("base64,")) {
                    String[] parts = imageBase64.split("base64,");
                    base64Data = parts[1];
                    // Extract mime type
                    if (parts[0].contains("image/jpeg"))
                        mimeType = "image/jpeg";
                    else if (parts[0].contains("image/webp"))
                        mimeType = "image/webp";
                }

                ObjectNode inlineData = mapper.createObjectNode();
                ObjectNode imageData = mapper.createObjectNode();
                imageData.put("mimeType", mimeType);
                imageData.put("data", base64Data);
                inlineData.set("inlineData", imageData);
                userParts.add(inlineData);
            }

            userContent.set("parts", userParts);
            contents.add(userContent);

            root.set("contents", contents);

            // Generation config
            ObjectNode generationConfig = mapper.createObjectNode();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 800);
            root.set("generationConfig", generationConfig);

            // Send request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String jsonPayload = mapper.writeValueAsString(root);
            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

            log.info("Sending chatbot request to Gemini API. Payload: {}", jsonPayload);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JsonNode responseBody = mapper.readTree(response.getBody());
            String reply = responseBody
                    .path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            log.info("Chatbot reply received successfully");
            return reply;

        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            log.error("ChatBot Gemini API HTTP error: {} - Response Body: {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return "Xin lỗi, mình đang gặp sự cố kỹ thuật. Vui lòng thử lại sau nhé! 😊";
        } catch (Exception e) {
            log.error("ChatBot Gemini API error: ", e);
            return "Xin lỗi, mình đang gặp sự cố kỹ thuật. Vui lòng thử lại sau nhé! 😊";
        }
    }
}
