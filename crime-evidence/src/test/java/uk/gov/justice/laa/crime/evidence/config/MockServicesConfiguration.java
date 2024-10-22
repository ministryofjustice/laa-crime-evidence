package uk.gov.justice.laa.crime.evidence.config;

public class MockServicesConfiguration {

    public static ServicesConfiguration getConfiguration(int port) {

        String host = String.format("http://localhost:%s", port);

        ServicesConfiguration servicesConfiguration = new ServicesConfiguration();
        ServicesConfiguration.MaatApi maatApiConfiguration = new ServicesConfiguration.MaatApi();
        ServicesConfiguration.CmaApi cmaApiConfiguration = new ServicesConfiguration.CmaApi();

        ServicesConfiguration.MaatApi.RepOrderEndpoints repOrderEndpoints =
                new ServicesConfiguration.MaatApi.RepOrderEndpoints(
                        "/rep-orders/capital/reporder/{repId}"
                );

        ServicesConfiguration.CmaApi.Endpoints cmaEndpoints =
            new ServicesConfiguration.CmaApi.Endpoints(
                "/assessment/means/{financialAssessmentId}",
                "/assessment/means");

        maatApiConfiguration.setBaseUrl(host);
        maatApiConfiguration.setRepOrderEndpoints(repOrderEndpoints);

        cmaApiConfiguration.setBaseUrl(host);
        cmaApiConfiguration.setEndpoints(cmaEndpoints);

        servicesConfiguration.setOAuthEnabled(false);
        servicesConfiguration.setMaatApi(maatApiConfiguration);
        servicesConfiguration.setCmaApi(cmaApiConfiguration);

        return servicesConfiguration;
    }
}
