package uk.gov.justice.laa.crime.evidence.config;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "services")
public class ServicesConfiguration {

    @NotNull
    private CmaApi cmaApi;

    @NotNull
    private MaatApi maatApi;

    @NotNull
    private boolean oAuthEnabled;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CmaApi {
        @NotNull
        private String baseUrl;

        @NotNull
        private Endpoints endpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Endpoints {
            @NotNull
            private String findUrl;

            @NotNull
            private String updateUrl;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaatApi {

        @NotNull
        private String baseUrl;

        @NotNull
        private RepOrderEndpoints repOrderEndpoints;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class RepOrderEndpoints {

            @NotNull
            private String repOrderCapitalUrl;
        }
    }
}
