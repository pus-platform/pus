package com.ez.pus;

import com.ez.pus.service.GoogleOAuth2Properties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.ez.pus.UploadFiles.storage.StorageProperties;
import com.ez.pus.UploadFiles.storage.StorageService;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({StorageProperties.class, GoogleOAuth2Properties.class})
@EnableJpaRepositories
public class PUsApplication {
    public static void main(String[] args) {
        SpringApplication.run(PUsApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.init();
        };
    }
}
