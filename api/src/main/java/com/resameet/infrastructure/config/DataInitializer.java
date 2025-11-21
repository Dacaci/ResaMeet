package com.resameet.infrastructure.config;

import com.resameet.domain.model.Role;
import com.resameet.domain.model.Role.RoleName;
import com.resameet.domain.model.User;
import com.resameet.domain.repository.RoleRepository;
import com.resameet.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
            .orElseGet(() -> {
                Role role = new Role();
                role.setName(RoleName.ADMIN);
                return roleRepository.save(role);
            });

        Role userRole = roleRepository.findByName(RoleName.USER)
            .orElseGet(() -> {
                Role role = new Role();
                role.setName(RoleName.USER);
                return roleRepository.save(role);
            });

        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@resameet.com");
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@resameet.com");
            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);
            user.setRoles(userRoles);
            userRepository.save(user);
        }
    }
}
