package com.texastoc.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.stream.Collectors;

/**
 * Only run if the spring profile is "test"
 */
@Profile("test")
@Configuration
public class SetupDatabase {
    @Bean
    CommandLineRunner init(JdbcTemplate jdbcTemplate) {
        return args -> {
            InputStream resource = new ClassPathResource(
                "create_toc_schema.sql").getInputStream();
            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jdbcTemplate.execute(line);
                }
            }
        };
    }
}
