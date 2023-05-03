 package by.sva.restApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/* Простой REST контроллер с обработкой ошибок
 * Обратить внимание на методы getAll и getOne класса EmployeeController. 
 * Они возвращают помимо данных еще и ссылки https://spring.io/guides/tutorials/rest/
 * 
 * Еще интересный раздел Building links into your REST API. Реализован в OrderController
 * 
 * Описание API: http://localhost:8080/swagger-ui/index.html
 * 			OAS: http://localhost:8080/v3/api-docs
 * Есть подборка в postman
 */

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

}
