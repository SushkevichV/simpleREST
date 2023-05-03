package by.sva.restApi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import by.sva.restApi.entity.Employee;
import by.sva.restApi.exception.EmployeeNotFoundException;
import by.sva.restApi.repository.EmployeeRepository;
import by.sva.restApi.util.EmployeeModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;

// для вызова статического метода methodOn
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/* Простой REST контроллер с обработкой ошибок
 * Обратить внимание на методы getAll и getOne. Они возвращают помимо данных еще и ссылки
 * https://spring.io/guides/tutorials/rest/
 * можно написать код для добавления ссылок прямо тут или вынести в отдельный класс EmployeeModelAssembler
 * 
 * Также описана возможность изменения структуры данных Entity с поддержкой обмена данными по прежней структуре
 * (написано путано, лучше почитать Supporting changes to the API) Здесь не реализовано.
 * 
 * Еще интересный раздел Building links into your REST API
 */

@RestController
//@Api(description = "Employee controller whith link generator")
@Tag(name = "Employee controller", description = "Контроллер запросов по персоналу")
public class EmployeeController {
	
	private final EmployeeModelAssembler assembler;
	private final EmployeeRepository repository;
	
	public EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
		this.repository = repository;
		this.assembler = assembler;
	}
	
	
	@GetMapping("/employees/get")
//	@ApiOperation("Get all elements")
	@Operation(summary = "Get list of employees", description = "Получить список сотрудников")
	public List<Employee> get(){
		return repository.findAll();
	}
	
	@GetMapping("/employees")
//	@ApiOperation("Get all elements whith links")
	@Operation(summary = "Get list of employees with links", description = "Получить список сотрудников со ссылками")
	public CollectionModel<EntityModel<Employee>> getAll() {
		List<EntityModel<Employee>> employees = repository.findAll().stream()
				.map(employee -> EntityModel.of(employee,
						linkTo(methodOn(EmployeeController.class).getOne(employee.getId())).withSelfRel(),
						linkTo(methodOn(EmployeeController.class).getAll()).withRel("employees")))
				.collect(Collectors.toList());

		return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).getAll()).withSelfRel());
	}
	
	@PostMapping("/employees")
//	@ApiOperation("Add new element")
	@Operation(summary = "Add new Employee", description = "Добавить нового сотрудника")
	public Employee newEmployee(@RequestBody Employee newEmployee) {
		return repository.save(newEmployee);
	}
	
	/*
	@GetMapping("/employees/{id}")
	public Employee getOne(@PathVariable Long id) {
		return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
	}
	*/
	@GetMapping("/employees/{id}")
//	@ApiOperation("Get element by id whith links")
	@Operation(summary = "Get an employee by Id with links", description = "Получить сотрудника по Id со ссылками")
	public EntityModel<Employee> getOne(@PathVariable @Parameter(description = "Идентификатор пользователя") Long id) {
		Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

		/*// это полный код для добавления ссылок
		return EntityModel.of(employee, linkTo(methodOn(EmployeeController.class).getOne(id)).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).getAll()).withRel("employees"));
		*/
		// это с использованием класса EmployeeModelAssembler 
		return assembler.toModel(employee);
	}
	
	@PutMapping("/employees/{id}")
//	@ApiOperation("Edit element by id")
	@Operation(summary = "Update an employee's data by Id", description = "Изменить данные сотрудника по Id")
	public Employee replaceEmployee(@RequestBody @Parameter(description = "Данные пользователя") Employee newEmployee
								, @PathVariable @Parameter(description = "Идентификатор пользователя") Long id) {
		return repository.findById(id).map(employee -> {employee.setName(newEmployee.getName());
														employee.setRole(newEmployee.getRole());
														return repository.save(employee);
		}).orElseGet(() -> {newEmployee.setId(id);
							return repository.save(newEmployee);
			
		});
	}
	
	@DeleteMapping("/employees/{id}")
//	@ApiOperation("Remove element by id")
	@Operation(summary = "Remove an employee by Id", description = "Удалить сотрудника по Id")
	public CollectionModel<EntityModel<Employee>> deleteEmployee(@PathVariable @Parameter(description = "Идентификатор пользователя") Long id) {
		Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
		repository.deleteById(id);
		return getAll();
	}
	
	// Отправляет запрос на указанный URL. Получает список объектов, берет второй из них и возвращает его поле name
	@GetMapping("/employees/gs")
	public String getSecond(){
		 Employee e = new RestTemplate().getForObject("http://localhost:8080" + "/employees/get", Employee[].class)[1];
		 return e.getName();
	}

}
