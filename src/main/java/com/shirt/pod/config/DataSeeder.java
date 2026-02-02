package com.shirt.pod.config;

import com.shirt.pod.model.entity.Permission;
import com.shirt.pod.model.entity.Role;
import com.shirt.pod.model.entity.User;
import com.shirt.pod.model.entity.enums.PermissionName;
import com.shirt.pod.model.entity.enums.RoleConstant;
import com.shirt.pod.model.entity.enums.UserStatus;
import com.shirt.pod.repository.PermissionRepository;
import com.shirt.pod.repository.RoleRepository;
import com.shirt.pod.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

        createDefaultAdminUser(superAdminRole);

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

    private void createDefaultAdminUser(Role superAdminRole) {
        String adminEmail = "admin";

        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin"))
                    .fullName("System Administrator")
                    .status(UserStatus.ACTIVE)
                    .roles(Set.of(superAdminRole))
                    .build();

            userRepository.save(admin);
            log.info("Created default admin user: {}", adminEmail);
            log.warn("IMPORTANT: Default admin password is 'admin'");
        } else {
            log.info("Admin user already exists: {}", adminEmail);
        }
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
