package by.sva.restApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/* Простой REST контроллер с обработкой ошибок
 * Обратить внимание на методы getAll и getOne класса EmployeeController. 
 * Они возвращают помимо данных еще и ссылки
 * https://spring.io/guides/tutorials/rest/
 * можно написать код для добавления ссылок прямо тут или вынести в отдельный класс EmployeeModelAssembler
 * 
 * Также описана возможность изменения структуры данных Entity с поддержкой обмена данными по прежней структуре
 * (написано путано, лучше почитать Supporting changes to the API) Здесь не реализовано.
 * 
 * Еще интересный раздел Building links into your REST API. Реализован в OrderController
 * 
 * Есть подборка в postman
 */

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

}
