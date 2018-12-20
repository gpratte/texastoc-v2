package com.texastoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication
public class TocV2Application {

	public static void main(String[] args) {
		SpringApplication.run(TocV2Application.class, args);
	}

//	@Configuration
//	@Order(SecurityProperties.BASIC_AUTH_ORDER)
//	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//		@Override
//		protected void configure(HttpSecurity httpSecurity) throws Exception {
//			// @formatter:off
//			httpSecurity
//			  .cors().and()
//			  .csrf().disable()
//			  .httpBasic().and()
//			  .authorizeRequests()
//			    .antMatchers("/", "/index.html", "/home", "/login", "/h2-console/*")
//                  .permitAll()
//			    .anyRequest()
//                  .authenticated();
//			// @formatter:on
//		}
//	}

}
