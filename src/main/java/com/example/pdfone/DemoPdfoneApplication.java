package com.example.pdfone;

import com.example.pdfone.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class DemoPdfoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoPdfoneApplication.class, args);
    }

}
