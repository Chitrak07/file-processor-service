package com.docutools.fileprocessorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

// Add this annotation to explicitly define the packages to scan
@ComponentScan(basePackages = "com.docutools.fileprocessorservice")
@SpringBootApplication
public class FileProcessorServiceApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(FileProcessorServiceApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(FileProcessorServiceApplication.class, args);
    }
}
