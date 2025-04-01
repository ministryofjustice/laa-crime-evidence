package uk.gov.justice.laa.crime.evidence.config;

public class MockServicesConfiguration {

    public static ServicesConfiguration getConfiguration(int port) {

        String host = String.format("http://localhost:%s", port);

        ServicesConfiguration servicesConfiguration = new ServicesConfiguration();
        ServicesConfiguration.MaatApi maatApiConfiguration = new ServicesConfiguration.MaatApi();

        ServicesConfiguration.MaatApi.RepOrderEndpoints repOrderEndpoints =
                new ServicesConfiguration.MaatApi.RepOrderEndpoints(
                        "/rep-orders/{repId}/capital-assets/count"
                );

        maatApiConfiguration.setBaseUrl(host);
        maatApiConfiguration.setRepOrderEndpoints(repOrderEndpoints);

        servicesConfiguration.setOAuthEnabled(false);
        servicesConfiguration.setMaatApi(maatApiConfiguration);

        return servicesConfiguration;
    }
}
