package uk.gov.justice.laa.crime.evidence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@SpringBootApplication(exclude = ZipkinAutoConfiguration.class)
@ConfigurationPropertiesScan
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CrimeEvidenceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrimeEvidenceApplication.class, args);
    }
}
