package com.backend.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.seed")
public class SeedProperties {
    private boolean enabled = true;
    private int products = 10;
    private int customers = 10;
}
