package uk.gov.justice.laa.crime.evidence.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <code>MaatApiOAuth2Client.java</code>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

    private final ServicesConfiguration configuration;
    private final RetryConfiguration retryConfiguration;

    public static Consumer<Map<String, Object>> getExchangeFilterWith(String provider) {
        return ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId(provider);
    }

    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("custom")
                .maxConnections(500)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .evictInBackground(Duration.ofSeconds(120))
                .build();
    }

    @Primary
    @Bean(name = "maatAPIOAuth2WebClient")
    public WebClient maatApiWebClient(WebClient.Builder builder) {
        return builder.baseUrl(configuration.getMaatApi().getBaseUrl()).build();
    }

}
