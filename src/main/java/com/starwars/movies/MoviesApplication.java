package com.starwars.movies;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@OpenAPIDefinition(info = @Info(title = "Star Wars API Documentation",
        version = "v0.0.1",
        description = "Star ",
        license = @io.swagger.v3.oas.annotations.info.License(name = "Servicebook License",
                url = "https://swapi.dev/")))
@SpringBootApplication
public class MoviesApplication extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder)
    {
        return builder.sources(MoviesApplication.class);
    }
    
    public static void main(String[] args)
    {
        SpringApplication.run(MoviesApplication.class, args);
    }
    
    @Bean
    public RestTemplate getRestTemplate()
    {
        return new RestTemplate();
    }
}
