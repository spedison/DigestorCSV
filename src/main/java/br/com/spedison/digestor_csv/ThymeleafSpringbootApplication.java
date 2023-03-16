package br.com.spedison.digestor_csv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ThymeleafSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThymeleafSpringbootApplication.class, args);
    }

}
