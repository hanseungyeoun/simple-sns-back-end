package com.example.simplesms.config;

import com.example.simplesms.dto.user.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaAuditingConfiguration {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("");
            }

            if (!(authentication.getPrincipal() instanceof UserPrincipal)) {
                return Optional.of("");
            }

            UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
            return Optional.of(user.getUsername());
        };
    }
}
