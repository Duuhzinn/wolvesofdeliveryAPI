package wolvesofdelivery.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = { "wolvesofdelivery.api.rest.model" })
@ComponentScan(basePackages = { "wolvesofdelivery.*" })
@EnableJpaRepositories(basePackages = { "wolvesofdelivery.api.rest.repository" })
@EnableTransactionManagement
@RestController
@EnableAutoConfiguration
public class WolvesofdeliveryApiApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(WolvesofdeliveryApiApplication.class, args);
        System.out.println(new BCryptPasswordEncoder().encode("123"));
    }

    // ✅ libera o CORS para requisições externas (Postman, frontend, mobile)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}