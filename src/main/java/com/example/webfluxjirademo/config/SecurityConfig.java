package com.example.webfluxjirademo.config;

import com.example.webfluxjirademo.repository.AuthenticationRepository;
import com.example.webfluxjirademo.security.AuthenticationFailureHandler;
import com.example.webfluxjirademo.security.AuthenticationSuccessHandler;
import com.example.webfluxjirademo.security.LogoutSuccessHandler;
import com.example.webfluxjirademo.security.SecurityContextRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;
    private final SecurityContextRepository securityContextRepository;
    private final AuthenticationRepository authenticationRepository;

    public SecurityConfig(AuthenticationSuccessHandler authenticationSuccessHandler,
                          AuthenticationFailureHandler authenticationFailureHandler,
                          LogoutSuccessHandler logoutSuccessHandler,
                          SecurityContextRepository securityContextRepository, AuthenticationRepository authenticationRepository) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.securityContextRepository = securityContextRepository;
        this.authenticationRepository = authenticationRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        List<UserDetails> userDetails = authenticationRepository.findAll()
                .map(authentication -> User.builder()
                        .username(authentication.getUsername())
                        .passwordEncoder(passwordEncoder()::encode)
                        .password(authentication.getPassword())
                        .authorities(new ArrayList<>()).build())
                .toStream()
                .collect(Collectors.toList());
        return new MapReactiveUserDetailsService(userDetails);
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .securityContextRepository(securityContextRepository)
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .authenticationSuccessHandler(authenticationSuccessHandler)
                        .authenticationFailureHandler(authenticationFailureHandler))
                .logout(logout -> logout.logoutSuccessHandler(logoutSuccessHandler))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/login").permitAll()
                        .anyExchange().authenticated());

        return http.build();
    }
}
