package com.shirt.pod.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Map;

/**
 * Virtual Try-On service using Hugging Face Spaces API (IDM-VTON / Kolors).
 * 100% Free, bypasses Gemini billing limit.
 */
@Service
@Slf4j
public class VirtualTryOnService {

    // Gradio API endpoint for yisol/IDM-VTON space (using the named endpoint
    // /tryon)
    private static final String HF_SPACE_URL = "https://yisol-idm-vton.hf.space/call/tryon";
    private static final String HF_UPLOAD_URL = "https://yisol-idm-vton.hf.space/upload";

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final int MAX_RETRIES = 3;

    @Value("${huggingface.api-key}")
    private String hfApiKey;

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(60_000); // 60s
        factory.setReadTimeout(180_000); // Tăng thời gian chờ lên 3 phút vì Serverless AI có thể mất thời gian xếp hàng
                                         // (Queue)
        return new RestTemplate(factory);
    }

    private String uploadImageToGradio(RestTemplate restTemplate, MultipartFile file) throws Exception {
        log.info("Uploading image to Gradio server...");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (hfApiKey != null && !hfApiKey.isEmpty() && !hfApiKey.equals("your_hf_token_here")) {
            headers.setBearerAuth(hfApiKey);
        }

        org.springframework.util.MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
        body.add("files", file.getResource());

        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body,
                headers);

        ResponseEntity<String> response = restTemplate.postForEntity(HF_UPLOAD_URL, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Gradio Upload failed: " + response.getStatusCode());
        }

        // Expected output: ["/tmp/gradio/123xyz..."]
        JsonNode root = mapper.readTree(response.getBody());
        if (root.isArray() && !root.isEmpty()) {
            return root.get(0).asText();
        }
        throw new RuntimeException("Unexpected Gradio upload response: " + response.getBody());
    }

    public byte[] processVirtualTryOn(MultipartFile personImage, MultipartFile garmentImage) {
        try {
            log.info("Starting Hugging Face Virtual Try-On. Person: {}KB, Garment: {}KB",
                    personImage.getSize() / 1024, garmentImage.getSize() / 1024);

            RestTemplate restTemplate = createRestTemplate();

            // 1. Gradio 4.x requirement: Upload images to get temporary file paths
            String personPath = uploadImageToGradio(restTemplate, personImage);
            String garmentPath = uploadImageToGradio(restTemplate, garmentImage);

            log.info("Uploaded to Gradio: person={}, garment={}", personPath, garmentPath);

            // 2. Build payload using the uploaded paths
            String requestBody = buildGradioRequest(personPath, garmentPath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Gửi Token để bypass limit nếu có
            if (hfApiKey != null && !hfApiKey.isEmpty() && !hfApiKey.equals("your_hf_token_here")) {
                headers.setBearerAuth(hfApiKey);
            }

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            // 3. Call inference API
            String responseBody = callHuggingFaceWithRetry(restTemplate, HF_SPACE_URL, request);

            byte[] resultImage = extractImageFromGradioResponse(responseBody);
            log.info("Hugging Face Try-On complete. Result: {}KB", resultImage.length / 1024);

            return resultImage;

        } catch (Exception e) {
            log.error("Hugging Face Virtual Try-On failed", e);
            throw new RuntimeException("Virtual Try-On processing failed: " + e.getMessage(), e);
        }
    }

    private String buildGradioRequest(String personPath, String garmentPath) throws Exception {
        ObjectNode root = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Parameter 0: dict (background mask object for ImageEditor)
        ObjectNode dictParam = mapper.createObjectNode();

        ObjectNode backgroundData = mapper.createObjectNode();
        backgroundData.put("path", personPath);

        ObjectNode metaData = mapper.createObjectNode();
        metaData.put("_type", "gradio.FileData");
        backgroundData.set("meta", metaData);

        dictParam.set("background", backgroundData);
        dictParam.set("layers", mapper.createArrayNode()); // empty layers
        dictParam.putNull("composite");
        dataArray.add(dictParam);

        // Parameter 1: garm_img (FileData object)
        ObjectNode garmImgData = mapper.createObjectNode();
        garmImgData.put("path", garmentPath);
        garmImgData.set("meta", metaData);
        dataArray.add(garmImgData);

        // Parameter 2: garment_des (string)
        dataArray.add("a shirt");

        // Parameter 3: is_checked (auto-masking)
        dataArray.add(true);

        // Parameter 4: is_checked_crop
        dataArray.add(false); // crop often causes issues if not tuned

        // Parameter 5: denoise_steps
        dataArray.add(30);

        // Parameter 6: seed
        dataArray.add(42);

        root.set("data", dataArray);
        return mapper.writeValueAsString(root);
    }

    private String callHuggingFaceWithRetry(RestTemplate restTemplate, String url, HttpEntity<String> request)
            throws Exception {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                log.info("Calling Hugging Face API (attempt {}/{})", attempt, MAX_RETRIES);
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    throw new RuntimeException("API returned: " + response.getStatusCode());
                }

                // Call endpoint returns event_id: {"event_id": "xxxx"}
                JsonNode root = mapper.readTree(response.getBody());
                if (root.has("event_id")) {
                    String eventId = root.get("event_id").asText();
                    return pollGradioEvent(restTemplate, url, eventId, request.getHeaders());
                }

                return response.getBody();

            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS
                        || e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
                    if (attempt < MAX_RETRIES) {
                        log.warn("Rate limited or queue full. Waiting 20s before retry...");
                        Thread.sleep(20000);
                    } else {
                        throw new RuntimeException(
                                "Hugging Face servers are currently too busy. Quá tải máy chủ, vui lòng thử lại sau.");
                    }
                } else {
                    throw new RuntimeException("API error (" + e.getStatusCode() + "): " + e.getResponseBodyAsString());
                }
            }
        }
        throw new RuntimeException("Unexpected state");
    }

    private String pollGradioEvent(RestTemplate restTemplate, String baseUrl, String eventId, HttpHeaders headers)
            throws Exception {
        String url = baseUrl + "/" + eventId;
        HttpEntity<String> request = new HttpEntity<>(headers);
        log.info("Polling Gradio event: {}", eventId);

        long startTime = System.currentTimeMillis();
        long timeoutMs = 180_000; // 3 minutes timeout

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Polling API returned: " + response.getStatusCode());
            }

            // Polling returns Server-Sent Events (SSE).
            // Look for event: complete and its data: [...]
            String body = response.getBody();
            if (body.contains("event: complete")) {
                int dataIndex = body.indexOf("data: ", body.indexOf("event: complete"));
                if (dataIndex != -1) {
                    // Extract JSON array string
                    int endLine = body.indexOf('\n', dataIndex);
                    if (endLine == -1)
                        endLine = body.length();
                    String jsonPart = body.substring(dataIndex + 6, endLine).trim();
                    return jsonPart; // This is the list of outputs
                }
            } else if (body.contains("event: error")) {
                throw new RuntimeException("Gradio API error during polling: " + body);
            }

            // Wait 2 seconds before polling again
            Thread.sleep(2000);
        }

        throw new RuntimeException("Gradio API polling timed out after 3 minutes");
    }

    private byte[] extractImageFromGradioResponse(String responseBody) throws Exception {
        // Here responseBody is the JSON array like [{"path": "...", "url": "..." },
        // {"path": "...", "url": "..."}]
        JsonNode data = mapper.readTree(responseBody);

        if (data != null && data.isArray() && !data.isEmpty()) {
            JsonNode firstItem = data.get(0);

            // Gradio 4.x JSON list of objects from SSE
            if (firstItem.has("url")) {
                String imgUrl = firstItem.get("url").asText();
                if (imgUrl.startsWith("/")) {
                    imgUrl = "https://yisol-idm-vton.hf.space" + imgUrl;
                }
                log.info("Downloading result image from URL: {}", imgUrl);
                RestTemplate restTemplate = new RestTemplate();
                return restTemplate.getForEntity(imgUrl, byte[].class).getBody();
            } else if (firstItem.has("path")) {
                String pathData = firstItem.get("path").asText();
                if (pathData.contains("base64,")) {
                    String base64Data = pathData.substring(pathData.indexOf("base64,") + 7);
                    return Base64.getDecoder().decode(base64Data);
                }
            }
        }

        throw new RuntimeException("Could not extract image from Hugging Face response. Payload: " + responseBody);
    }
}
