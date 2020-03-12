package com.texastoc.security;

import com.texastoc.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.texastoc.security.SecurityConstants.SIGN_UP_URL;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurity extends WebSecurityConfigurerAdapter {

  private final UserDetailsServiceImpl userDetailsService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public WebSecurity(UserDetailsServiceImpl userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder,
                     JwtTokenProvider jwtTokenProvider) {
    this.userDetailsService = userDetailsService;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http.cors().and()
      .csrf().disable()
      .headers().frameOptions().sameOrigin().and()
      .authorizeRequests()
      .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
      .antMatchers(HttpMethod.POST, "/login").permitAll()
      .antMatchers("/h2-console").permitAll()
      .antMatchers("/h2-console/*").permitAll()
      .antMatchers("/actuator/*").permitAll()
      .anyRequest().authenticated().and()
      .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtTokenProvider))
      .addFilter(new JwtAuthorizationFilter(authenticationManager(), jwtTokenProvider, userDetailsService))
      // this disables session creation on Spring Security
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    // @formatter:on
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
    return source;
  }

}
