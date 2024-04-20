package ru.liga.cargodistributor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.liga.cargodistributor.config.ConfigProperties;

@SpringBootApplication
@EnableConfigurationProperties(ConfigProperties.class)
public class CargoDistributorBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(CargoDistributorBotApplication.class, args);
    }
}
