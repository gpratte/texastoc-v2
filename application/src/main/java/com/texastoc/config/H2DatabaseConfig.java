package com.texastoc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Only run if the spring profile is "test"
 */
@Profile("test")
@Configuration
public class H2DatabaseConfig {

  @Bean
  public DataSource dataSource() {
    DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
    dataSourceBuilder.driverClassName("org.h2.Driver");
    dataSourceBuilder.url("jdbc:h2:mem:testdb");
    dataSourceBuilder.username("sa");
    dataSourceBuilder.password("");
    return dataSourceBuilder.build();
  }

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
