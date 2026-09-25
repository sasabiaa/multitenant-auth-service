package org.project.multitenantauthservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        // 1. Inisialisasi Configurer OAuth2 Authorization Server
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new
                OAuth2AuthorizationServerConfigurer();

        // 2. Terapkan configurer ke HttpSecurity & Aktifkan OpenID Connect (OIDC)
        http
                .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer.oidc(Customizer.withDefaults()) // Enable OpenID Connect
                )
                .authorizeHttpRequests((authorize) ->
                        authorize.anyRequest().authenticated()
                )
                // 3. Exception handling jika belum login (redirect ke halaman /login)
                .exceptionHandling((exceptions) ->
                        exceptions.defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                authorizationServerConfigurer.getEndpointsMatcher()
                        )
                )
                // 4. Menerima JWT untuk autentikasi internal (misal endpoint /userinfo)
                .oauth2ResourceServer((resourceServer) ->
                        resourceServer.jwt(Customizer.withDefaults())
                );

        return http.build();
    }


    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.csrf(
                //Matikan csrf, karena buat REST API, bukan aplikasi html biasa
                csrf -> csrf.disable())
                //Atur hak akses url
                .authorizeHttpRequests(auth -> auth
                        //boleh diakses tanpa login
                        .requestMatchers("/auth/**").permitAll()
                        //sisa api harus terauthenticated terlebih dahulu
                        .anyRequest().authenticated()
                )
                //Buat session jadi STATELESS(server nga perlu nyimpan session di memory)
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}