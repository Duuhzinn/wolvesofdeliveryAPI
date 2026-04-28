package wolvesofdelivery.api.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EntityScan(basePackages = {"wolvesofdelivery.api.rest.model"})
//tudo que tiver dentro da pasta wolvesofdelivery vai ser configurado
@ComponentScan(basePackages = {"wolvesofdelivery.*"})
@EnableJpaRepositories(basePackages = {"wolvesofdelivery.api.rest.repository"})
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration

public class WolvesofdeliveryApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WolvesofdeliveryApiApplication.class, args);
	}

}
