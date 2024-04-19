package ru.liga.cargodistributor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableConfigurationProperties(ConfigProperties.class)
public class CargoDistributorBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(CargoDistributorBotApplication.class, args);
    }
}
