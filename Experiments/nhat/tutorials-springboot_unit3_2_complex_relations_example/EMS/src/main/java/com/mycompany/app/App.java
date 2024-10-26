package com.mycompany.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EntityScan(basePackages = {"com.mycompany.model"})
@EnableJpaRepositories(basePackages = {"com.mycompany.repository"})
@ComponentScan(basePackages = {"com.mycompany.service","com.mycompany.controller"})
public class App {
    public static void main( String[] args ){
        SpringApplication.run(App.class);
    }
}
