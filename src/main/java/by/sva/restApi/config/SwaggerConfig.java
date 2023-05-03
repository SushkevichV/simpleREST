package by.sva.restApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.spi.DocumentationType;
//
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;

// Необязательный в данном случае класс

//@Configuration
//@EnableSwagger2
@OpenAPIDefinition(
		info = @Info(
				title = "simple REST app", // название приложения
                description = "Пример простого REST приложения с автоматической генерацией ссылок и описанием API", //описание
                version = "1.0.2", // версия
                contact = @Contact( // контакты
                        name = "Vladislav S",
                        email = "raxmaniana@gmail.com",
                        url = "https://"
                        )
				)
		)
public class SwaggerConfig {
	/*
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				//.apis(RequestHandlerSelectors.basePackage("by.sva.restApi.controller"))
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}
	*/

}
