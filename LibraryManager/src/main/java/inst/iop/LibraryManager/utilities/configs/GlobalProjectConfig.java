package inst.iop.LibraryManager.utilities.configs;

import static inst.iop.LibraryManager.utilities.configs.SecurityConstants.*;
import inst.iop.LibraryManager.utilities.filters.JwtAuthenticationFilter;
import inst.iop.LibraryManager.utilities.filters.TrailingSlashFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;


import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(proxyTargetClass = true)
public class GlobalProjectConfig {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final TrailingSlashFilter trailingSlashFilter;
  private final AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(
            c -> {
              CorsConfigurationSource source = request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://localhost:8080"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                return config;
              };
              c.configurationSource(source);
            }
        )
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            (authorize) -> authorize
                .requestMatchers(
                    LOGIN_URL, REGISTER_URL, CONFIRM_REGISTRATION_URL, DOCUMENTATION_URL, SWAGGER_UI_URL, ACTUATOR_URL,
                    SWAGGER_FAVICON_URI
                )
                .permitAll()
                .anyRequest()
                .authenticated()
        )
        .sessionManagement(
            (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(trailingSlashFilter, DisableEncodeUrlFilter.class);
    return http.build();
  }
}
