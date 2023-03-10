package uk.gov.justice.laa.crime.evidence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableAspectJAutoProxy(proxyTargetClass=true)
@Slf4j
public class CrimeEvidenceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrimeEvidenceApplication.class, args);
    }
}
