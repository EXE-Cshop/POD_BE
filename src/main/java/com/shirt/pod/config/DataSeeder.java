package com.shirt.pod.config;

import com.shirt.pod.model.entity.*;
import com.shirt.pod.model.entity.enums.PermissionName;
import com.shirt.pod.model.entity.enums.RoleConstant;
import com.shirt.pod.model.entity.enums.UserStatus;
import com.shirt.pod.model.entity.enums.DiscountType;
import com.shirt.pod.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final PromotionRepository promotionRepository;
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

        Role userRole = createOrUpdateRole(
                RoleConstant.USER.name(),
                "Regular User - Customer shopping access",
                Set.of());
        log.info("Created/Updated USER role with {} permissions", userRole.getPermissions().size());

        User admin = createDefaultSuperAdminUser(superAdminRole);
        User demoUser = createDefaultRegularUser(userRole);
        ensureCart(admin);
        ensureCart(demoUser);

        seedCategoriesProductsAndPromotions(admin);

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

    private User createDefaultSuperAdminUser(Role superAdminRole) {
        String adminEmail = "superadmin@cshop.local";
        User admin;

        if (!userRepository.existsByEmail(adminEmail)) {
            admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@123456"))
                    .fullName("System Administrator")
                    .status(UserStatus.ACTIVE)
                    .roles(new HashSet<>(Set.of(superAdminRole)))
                    .build();

            admin = userRepository.save(admin);
            log.info("Created default admin user: {}", adminEmail);
            log.warn("IMPORTANT: Default admin password is 'Admin@123456'");
        } else {
            admin = userRepository.findByEmail(adminEmail).orElseThrow();
            log.info("Admin user already exists: {}", adminEmail);
        }
        return admin;
    }

    private User createDefaultRegularUser(Role userRole) {
        String userEmail = "user@cshop.local";
        User user;

        if (!userRepository.existsByEmail(userEmail)) {
            user = User.builder()
                    .email(userEmail)
                    .password(passwordEncoder.encode("User@123456"))
                    .fullName("Demo Customer")
                    .status(UserStatus.ACTIVE)
                    .roles(new HashSet<>(Set.of(userRole)))
                    .build();

            user = userRepository.save(user);
            log.info("Created default user account: {}", userEmail);
            log.warn("IMPORTANT: Default user password is 'User@123456'");
        } else {
            user = userRepository.findByEmail(userEmail).orElseThrow();
            log.info("Default user already exists: {}", userEmail);
        }
        return user;
    }

    private void ensureCart(User user) {
        cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    private void seedCategoriesProductsAndPromotions(User admin) {
        if (productRepository.count() > 0) {
            log.info("Products already seeded, skipping product flow seeding.");
            return;
        }

        // 1. Seed Categories
        Category streetwear = Category.builder()
                .name("Streetwear")
                .slug("streetwear")
                .description("Urban style, oversized fit, and trending designs")
                .imageUrl("https://images.unsplash.com/photo-1556905055-8f358a7a47b2?q=80&w=500")
                .sortOrder(1)
                .active(true)
                .build();
        streetwear = categoryRepository.save(streetwear);

        Category minimalist = Category.builder()
                .name("Minimalist")
                .slug("minimalist")
                .description("Simple, clean, and elegant shirts for everyday wear")
                .imageUrl("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?q=80&w=500")
                .sortOrder(2)
                .active(true)
                .build();
        minimalist = categoryRepository.save(minimalist);

        Category anime = Category.builder()
                .name("Anime & Manga")
                .slug("anime-manga")
                .description("Otaku style trending designs from your favorite anime series")
                .imageUrl("https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=500")
                .sortOrder(3)
                .active(true)
                .build();
        anime = categoryRepository.save(anime);

        // 2. Seed Products
        Product t1 = Product.builder()
                .name("Oversized Cyberpunk Tee")
                .slug("oversized-cyberpunk-tee")
                .description("Unleash the future with this streetwear cyberpunk graphics oversized black t-shirt.")
                .basePrice(new BigDecimal("290000"))
                .material("Cotton 100% 250gsm")
                .imageUrl("https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?q=80&w=500")
                .category(streetwear)
                .isTrending(true)
                .isFeatured(true)
                .tags("streetwear,cyberpunk,oversized,black")
                .active(true)
                .build();
        t1 = productRepository.save(t1);

        Product t2 = Product.builder()
                .name("Classic Minimalist White Tee")
                .slug("classic-minimalist-white-tee")
                .description("Pure comfort, timeless design. The perfect white t-shirt for any minimal style.")
                .basePrice(new BigDecimal("199000"))
                .material("Organic Cotton 100%")
                .imageUrl("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?q=80&w=500")
                .category(minimalist)
                .isTrending(false)
                .isFeatured(true)
                .tags("minimalist,classic,white,organic")
                .active(true)
                .build();
        t2 = productRepository.save(t2);

        Product t3 = Product.builder()
                .name("Trending Anime Chibi Tee")
                .slug("trending-anime-chibi-tee")
                .description("Cute chibi style manga character t-shirt for anime fans. Perfect street wear style.")
                .basePrice(new BigDecimal("249000"))
                .material("Cotton 100% 230gsm")
                .imageUrl("https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?q=80&w=500")
                .category(anime)
                .isTrending(true)
                .isFeatured(false)
                .tags("anime,chibi,manga,cute")
                .active(true)
                .build();
        t3 = productRepository.save(t3);

        // 3. Seed Product Images
        productImageRepository.save(ProductImage.builder()
                .product(t1)
                .imageUrl("https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?q=80&w=500")
                .sortOrder(1)
                .isPrimary(true)
                .build());
        productImageRepository.save(ProductImage.builder()
                .product(t1)
                .imageUrl("https://images.unsplash.com/photo-1562157873-818bc0726f68?q=80&w=500")
                .sortOrder(2)
                .isPrimary(false)
                .build());

        productImageRepository.save(ProductImage.builder()
                .product(t2)
                .imageUrl("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?q=80&w=500")
                .sortOrder(1)
                .isPrimary(true)
                .build());

        // 4. Seed Product Variants
        productVariantRepository.save(ProductVariant.builder()
                .product(t1)
                .colorName("Black")
                .colorHex("#000000")
                .size("M")
                .sku("CYBER-BLK-M")
                .stockQuantity(80)
                .frontImageUrl("https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?q=80&w=500")
                .backImageUrl("")
                .priceAdjustment(BigDecimal.ZERO)
                .active(true)
                .build());
        productVariantRepository.save(ProductVariant.builder()
                .product(t1)
                .colorName("Black")
                .colorHex("#000000")
                .size("L")
                .sku("CYBER-BLK-L")
                .stockQuantity(120)
                .frontImageUrl("https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?q=80&w=500")
                .backImageUrl("")
                .priceAdjustment(new BigDecimal("15000"))
                .active(true)
                .build());

        productVariantRepository.save(ProductVariant.builder()
                .product(t2)
                .colorName("White")
                .colorHex("#FFFFFF")
                .size("M")
                .sku("MIN-WHT-M")
                .stockQuantity(150)
                .frontImageUrl("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?q=80&w=500")
                .backImageUrl("")
                .priceAdjustment(BigDecimal.ZERO)
                .active(true)
                .build());
        productVariantRepository.save(ProductVariant.builder()
                .product(t2)
                .colorName("White")
                .colorHex("#FFFFFF")
                .size("L")
                .sku("MIN-WHT-L")
                .stockQuantity(100)
                .frontImageUrl("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?q=80&w=500")
                .backImageUrl("")
                .priceAdjustment(BigDecimal.ZERO)
                .active(true)
                .build());

        // 5. Seed Promotions
        promotionRepository.save(Promotion.builder()
                .code("SUMMER2026")
                .description("Get 10% off on all trending streetwear shirts")
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(new BigDecimal("10"))
                .minOrderAmount(new BigDecimal("200000"))
                .maxUsageCount(100)
                .usedCount(0)
                .startDate(Instant.now())
                .endDate(Instant.now().plus(60, ChronoUnit.DAYS))
                .active(true)
                .build());

        promotionRepository.save(Promotion.builder()
                .code("CSHOP50")
                .description("Flat 50,000 VND discount for new customers")
                .discountType(DiscountType.FIXED_AMOUNT)
                .discountValue(new BigDecimal("50000"))
                .minOrderAmount(new BigDecimal("300000"))
                .maxUsageCount(500)
                .usedCount(0)
                .startDate(Instant.now())
                .endDate(Instant.now().plus(90, ChronoUnit.DAYS))
                .active(true)
                .build());

        log.info("Successfully seeded Categories, Products, Variants, and Promotions");
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
