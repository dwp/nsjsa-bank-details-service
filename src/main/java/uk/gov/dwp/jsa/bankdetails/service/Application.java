package uk.gov.dwp.jsa.bankdetails.service;

import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import uk.gov.dwp.health.crypto.CryptoConfig;
import uk.gov.dwp.health.crypto.CryptoDataManager;
import uk.gov.dwp.health.crypto.exception.CryptoException;
import uk.gov.dwp.jsa.bankdetails.service.config.BankDetailsServiceObjectMapperProvider;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan(value = "uk.gov.dwp.jsa")
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        final BankDetailsServiceObjectMapperProvider objectMapperProvider =
                new BankDetailsServiceObjectMapperProvider();
        return objectMapperProvider.get();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CryptoDataManager cryptoDataManager(
            final @Value("${app.security.kms.datakey}") String datakey,
            final @Value("${app.security.kms.overrideurl:}") String urlOverride
    ) throws CryptoException {
        CryptoConfig config = new CryptoConfig(datakey);
        if (!StringUtils.isNullOrEmpty(urlOverride)) {
            config.setKmsEndpointOverride(urlOverride);
        }
        return new CryptoDataManager(config);
    }
}
