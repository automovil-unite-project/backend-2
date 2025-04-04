package com.automovil_unite.backend.infrastructure.config.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.automovil_unite.backend.infrastructure.persistence.repository")
@EnableJpaAuditing
public class PersistenceConfig {
    // La configuración adicional se realiza mediante application.yml
}
