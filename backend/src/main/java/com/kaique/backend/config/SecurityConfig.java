package com.kaique.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Libera a ponte CORS para o Angular não ser barrado na porta
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. Desativa o CSRF (uma proteção para formulários antigos, não usamos em APIs modernas)
            .csrf(csrf -> csrf.disable()) 
            
            // 3. A regra de ouro: Qualquer requisição precisa de autenticação!
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated() 
            )
            
            // 4. Dizemos que vamos usar o padrão de usuário e senha via cabeçalho (Basic Auth)
            .httpBasic(Customizer.withDefaults()); 

        return http.build();
    }

    // Criando o nosso usuário "Dono do Sistema" na memória do Java
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails usuarioAdmin = User.builder()
            .username("kaique")
            .password("{noop}123456") // O {noop} avisa o Java que não estamos criptografando a senha agora
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(usuarioAdmin);
    }

    // Ensinando o Spring Security a não bloquear o Angular
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200")); // Deixa o Angular entrar
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}