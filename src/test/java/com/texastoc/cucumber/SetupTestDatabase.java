package com.texastoc.cucumber;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;

@Configuration
public class SetupTestDatabase {

    @Bean
    CommandLineRunner init(JdbcTemplate jdbcTemplate) {
        return args -> {
            File file = ResourceUtils.getFile("classpath:create_toc_schema.sql");
            try (BufferedReader bf = Files.newBufferedReader(file.toPath())) {
                String line = null;
                while ((line = bf.readLine()) != null) {
                    jdbcTemplate.execute(line);
                }
            }

        };
    }
}
