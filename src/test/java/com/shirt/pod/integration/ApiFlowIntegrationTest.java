//package com.shirt.pod.integration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.shirt.pod.model.dto.request.LoginRequest;
//import com.shirt.pod.model.entity.BaseProduct;
//import com.shirt.pod.model.entity.ProductVariant;
//import com.shirt.pod.repository.BaseProductRepository;
//import com.shirt.pod.repository.ProductVariantRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.util.List;
//import java.util.Map;
//
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class ApiFlowIntegrationTest {
//
//        @Autowired
//        private MockMvc mockMvc;
//
//        @Autowired
//        private ObjectMapper objectMapper;
//
//        @Autowired
//        private BaseProductRepository baseProductRepository;
//
//        @Autowired
//        private ProductVariantRepository productVariantRepository;
//
//        private String authToken;
//
//        @BeforeEach
//        void setUp() throws Exception {
//                LoginRequest loginRequest = LoginRequest.builder()
//                                .email("admin")
//                                .password("admin")
//                                .build();
//
//                MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(loginRequest)))
//                                .andExpect(status().isOk())
//                                .andReturn();
//
//                String response = result.getResponse().getContentAsString();
//                Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
//                Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
//                authToken = "Bearer " + data.get("accessToken");
//        }
//
//        @Test
//        void testFullFlow() throws Exception {
//                mockMvc.perform(get("/api/v1/products")
//                                .header("Authorization", authToken))
//                                .andExpect(status().isOk())
//                                .andExpect(jsonPath("$.code").value(200));
//
//                List<BaseProduct> products = baseProductRepository.findAll();
//                if (products.isEmpty()) {
//                        throw new RuntimeException("No products found in DB.");
//                }
//                BaseProduct shirt = products.stream()
//                                .filter(p -> p.getName().contains("Premium"))
//                                .findFirst()
//                                .orElse(products.get(0));
//                List<ProductVariant> variants = productVariantRepository.findByBaseProductId(shirt.getId());
//                if (variants.isEmpty()) {
//                        throw new RuntimeException("Product " + shirt.getName() + " has no variants.");
//                }
//                ProductVariant variant = variants.get(0);
//
//                Map<String, Object> cartRequest = Map.of(
//                                "productVariantId", variant.getId(),
//                                "quantity", 1);
//
//                mockMvc.perform(post("/api/v1/cart/items")
//                                .header("Authorization", authToken)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(cartRequest)))
//                                .andExpect(status().isOk());
//
//                Map<String, Object> checkoutRequest = Map.of(
//                                "shippingAddress", "Test Address",
//                                "paymentMethod", "COD",
//                                "note", "Test Order");
//
//                mockMvc.perform(post("/api/checkout")
//                                .header("Authorization", authToken)
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(checkoutRequest)))
//                                .andExpect(status().isOk())
//                                .andExpect(jsonPath("$.data.id").exists());
//        }
//}
