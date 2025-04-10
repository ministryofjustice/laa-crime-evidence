package uk.gov.justice.laa.crime.evidence.config;

public class MockServicesConfiguration {

    public static ServicesConfiguration getConfiguration(int port) {

        String host = String.format("http://localhost:%s", port);

        ServicesConfiguration servicesConfiguration = new ServicesConfiguration();
        ServicesConfiguration.MaatApi maatApiConfiguration = new ServicesConfiguration.MaatApi();

        maatApiConfiguration.setBaseUrl(host);
        maatApiConfiguration.setRegistrationId("maat-api");

        servicesConfiguration.setOAuthEnabled(false);
        servicesConfiguration.setMaatApi(maatApiConfiguration);

        return servicesConfiguration;
    }
}
