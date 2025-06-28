package com.booking.tennisbook.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true", matchIfMissing = true)
public class SeederConfig {
    // This configuration class enables/disables seeders based on property
    // Default is enabled (matchIfMissing = true)
    // To disable: set app.seeder.enabled=false in application.properties
} 