package sn.niir.blog_backend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import sn.niir.blog_backend.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // active @PreAuthorize sur les controllers/services
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable()) // inutile en stateless JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Authentification ouverte à tous
                        .requestMatchers("/api/auth/**").permitAll()
                        // Lecture publique des articles et commentaires
                        .requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                        // Ajout de commentaire ouvert à tout utilisateur connecté (n'importe quel rôle)
                        .requestMatchers(HttpMethod.POST, "/api/comments/**").authenticated()
                        // Gestion des utilisateurs réservée à l'admin
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        // Statistiques réservées à l'admin
                        .requestMatchers("/api/stats/**").hasRole("ADMIN")
                        // Upload d'images réservé à AUTHOR et ADMIN
                        .requestMatchers("/api/uploads/**").hasAnyRole("AUTHOR", "ADMIN")
                        // Modération des commentaires réservée à l'admin
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/**").hasRole("ADMIN")
                        // Écriture d'articles réservée à AUTHOR et ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/articles/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/articles/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/articles/**").hasAnyRole("AUTHOR", "ADMIN")
                        // Tout le reste nécessite d'être authentifié
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}