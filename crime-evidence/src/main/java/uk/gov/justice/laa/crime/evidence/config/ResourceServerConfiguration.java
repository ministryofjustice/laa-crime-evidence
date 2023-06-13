package uk.gov.justice.laa.crime.evidence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Order(1)
@Configuration
@EnableWebSecurity
public class ResourceServerConfiguration {

    public static final String CCP_STANDARD_SCOPE = "SCOPE_crime-rs/ccp:standard";

    @Bean
    protected BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint() {
        BearerTokenAuthenticationEntryPoint bearerTokenAuthenticationEntryPoint = new BearerTokenAuthenticationEntryPoint();
        bearerTokenAuthenticationEntryPoint.setRealmName("Crime evidence API");
        return bearerTokenAuthenticationEntryPoint;
    }

    @Bean
    public AccessDeniedHandler bearerTokenAccessDeniedHandler() {
        return new BearerTokenAccessDeniedHandler();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/open-api/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/api/**").hasAuthority(CCP_STANDARD_SCOPE)
                        .anyRequest().authenticated())
                .oauth2ResourceServer()
                .accessDeniedHandler(bearerTokenAccessDeniedHandler())
                .authenticationEntryPoint(bearerTokenAuthenticationEntryPoint()).jwt();
        return http.build();
    }
}
