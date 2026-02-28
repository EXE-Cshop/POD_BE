package com.shirt.pod.config;

import com.shirt.pod.model.entity.*;
import com.shirt.pod.model.entity.enums.PermissionName;
import com.shirt.pod.model.entity.enums.RoleConstant;
import com.shirt.pod.model.entity.enums.UserStatus;
import com.shirt.pod.model.entity.enums.OrderStatus;
import com.shirt.pod.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BaseProductRepository baseProductRepository;
    private final PrintAreaRepository printAreaRepository;
    private final ProductVariantRepository productVariantRepository;
    private final SavedDesignRepository savedDesignRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("========== Starting Data Seeder ==========");

        Set<Permission> allPermissions = syncPermissions();
        log.info("Synced {} permissions", allPermissions.size());

        Role superAdminRole = createOrUpdateRole(
                RoleConstant.SUPER_ADMIN.name(),
                "Super Administrator - Full system access",
                allPermissions);
        log.info("Created/Updated SUPER_ADMIN role with {} permissions", superAdminRole.getPermissions().size());

        Set<Permission> userPermissions = getBasicViewPermissions(allPermissions);
        Role userRole = createOrUpdateRole(
                RoleConstant.USER.name(),
                "Regular User - Basic access",
                userPermissions);
        log.info("Created/Updated USER role with {} permissions", userRole.getPermissions().size());

        User admin = createDefaultAdminUser(superAdminRole);

        seedProductsAndFlows(admin);

        log.info("========== Data Seeder Completed ==========");
    }

    private Set<Permission> syncPermissions() {
        Set<Permission> permissions = new HashSet<>();

        for (PermissionName permName : PermissionName.values()) {
            Permission permission = permissionRepository.findByName(permName.name())
                    .orElseGet(() -> {
                        Permission newPermission = Permission.builder()
                                .name(permName.name())
                                .description(generatePermissionDescription(permName.name()))
                                .build();
                        log.debug("Creating new permission: {}", permName.name());
                        return permissionRepository.save(newPermission);
                    });
            permissions.add(permission);
        }

        return permissions;
    }

    private Role createOrUpdateRole(String roleName, String description, Set<Permission> permissions) {
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .name(roleName)
                            .description(description)
                            .permissions(new HashSet<>())
                            .build();
                    log.debug("Creating new role: {}", roleName);
                    return newRole;
                });

        role.setDescription(description);
        role.setPermissions(permissions);

        return roleRepository.save(role);
    }

    private Set<Permission> getBasicViewPermissions(Set<Permission> allPermissions) {
        Set<Permission> basicPermissions = new HashSet<>();

        for (Permission permission : allPermissions) {
            if (permission.getName().endsWith("_VIEW")) {
                basicPermissions.add(permission);
            }
        }

        return basicPermissions;
    }

    private User createDefaultAdminUser(Role superAdminRole) {
        String adminEmail = "admin";
        User admin;

        if (!userRepository.existsByEmail(adminEmail)) {
            admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin"))
                    .fullName("System Administrator")
                    .status(UserStatus.ACTIVE)
                    .roles(new HashSet<>(Set.of(superAdminRole)))
                    .build();

            admin = userRepository.save(admin);
            log.info("Created default admin user: {}", adminEmail);
            log.warn("IMPORTANT: Default admin password is 'admin'");
        } else {
            admin = userRepository.findByEmail(adminEmail).orElseThrow();
            log.info("Admin user already exists: {}", adminEmail);
        }
        return admin;
    }

    private void seedProductsAndFlows(User admin) {
        if (baseProductRepository.count() > 0) {
            log.info("Products already seeded, skipping product flow seeding.");
            return;
        }

        // 1. Seed Base Product
        BaseProduct shirt = BaseProduct.builder()
                .name("Áo Thun Premium")
                .description("Áo thun cotton 100% chất lượng cao")
                .basePrice(new BigDecimal("250000"))
                .material("Cotton 100%")
                .active(true)
                .build();
        shirt = baseProductRepository.save(shirt);

        // 2. Seed Print Areas
        PrintArea frontArea = PrintArea.builder()
                .baseProduct(shirt)
                .name(com.shirt.pod.model.entity.enums.PrintAreaName.FRONT_CENTER)
                .widthMm(new BigDecimal("300.0"))
                .heightMm(new BigDecimal("400.0"))
                .topOffsetPercent(15.0)
                .leftOffsetPercent(25.0)
                .widthPercent(50.0)
                .heightPercent(60.0)
                .build();
        printAreaRepository.save(frontArea);

        // 3. Seed Variants
        ProductVariant blackM = ProductVariant.builder()
                .baseProduct(shirt)
                .colorName("Đen")
                .colorHex("#000000")
                .size("M")
                .sku("TSHIRT-BLACK-M")
                .stockQuantity(100)
                .frontImageUrl("https://res.cloudinary.com/di5j3h6wi/image/upload/v1/samples/tshirt-black-front")
                .backImageUrl("https://res.cloudinary.com/di5j3h6wi/image/upload/v1/samples/tshirt-black-back")
                .priceAdjustment(BigDecimal.ZERO)
                .active(true)
                .build();
        blackM = productVariantRepository.save(blackM);

        ProductVariant whiteL = ProductVariant.builder()
                .baseProduct(shirt)
                .colorName("Trắng")
                .colorHex("#FFFFFF")
                .size("L")
                .sku("TSHIRT-WHITE-L")
                .stockQuantity(50)
                .frontImageUrl("https://res.cloudinary.com/di5j3h6wi/image/upload/v1/samples/tshirt-white-front")
                .backImageUrl("https://res.cloudinary.com/di5j3h6wi/image/upload/v1/samples/tshirt-white-back")
                .priceAdjustment(new BigDecimal("10000"))
                .active(true)
                .build();
        productVariantRepository.save(whiteL);

        // 4. Seed Saved Design
        Map<String, Object> designData = new HashMap<>();
        designData.put("elements", List.of(Map.of("type", "text", "content", "Hello World", "color", "#FF0000")));
        SavedDesign design = SavedDesign.builder()
                .name("Cool Design 01")
                .designJsonData(designData)
                .previewImageUrl("https://res.cloudinary.com/di5j3h6wi/image/upload/v1/samples/preview-design")
                .isTemplate(false)
                .build();
        savedDesignRepository.save(design);

        // 5. Seed Order
        Order sampleOrder = Order.builder()
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("260000"))
                .shippingFee(new BigDecimal("30000"))
                .recipientName("Nguyen Khach Hang")
                .recipientPhone("0988888888")
                .shippingAddress("123 Street, City")
                .paymentMethod("COD")
                .paymentStatus("UNPAID")
                .userId(admin.getId())
                .build();

        sampleOrder = orderRepository.save(sampleOrder);

        OrderItem item = OrderItem.builder()
                .orderId(sampleOrder.getId())
                .quantity(1)
                .unitPrice(new BigDecimal("250000"))
                .productionStatus("WAITING")
                .build();

        sampleOrder.setOrderItems(new ArrayList<>(List.of(item)));
        orderRepository.save(sampleOrder);

        // 6. Seed Cart
        Cart cart = Cart.builder()
                .user(admin)
                .build();
        cartRepository.save(cart);

        log.info("Successfully seeded product flows (Product, Design, Order, Cart)");
    }

    private String generatePermissionDescription(String permissionName) {
        String[] parts = permissionName.split("_");
        if (parts.length >= 2) {
            String action = parts[parts.length - 1];
            String resource = String.join(" ", java.util.Arrays.copyOf(parts, parts.length - 1));
            return String.format("%s %s", capitalize(action), resource.toLowerCase());
        }
        return permissionName;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
