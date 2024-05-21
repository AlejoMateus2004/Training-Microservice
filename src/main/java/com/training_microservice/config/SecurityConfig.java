package com.training_microservice.config;


import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         return http
                 .csrf(AbstractHttpConfigurer::disable)
                 .cors(Customizer.withDefaults())
                 .authorizeHttpRequests(auth -> auth
                     .requestMatchers(HttpMethod.POST,"/training").authenticated()
                    .requestMatchers("/training/**").authenticated()
                    .requestMatchers("/doc/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html/**", "/webjars/**", "/swagger-resources/**").permitAll()
                    .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                    .anyRequest().authenticated()
                 )
                 .headers(headers -> headers.frameOptions(Customizer.withDefaults()).disable())
                 .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                 .sessionManagement(session ->
                         session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                 .httpBasic(Customizer.withDefaults())
                 .build();
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}