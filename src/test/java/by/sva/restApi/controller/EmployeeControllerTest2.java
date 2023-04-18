package by.sva.restApi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.MediaType;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import by.sva.restApi.entity.Employee;
import by.sva.restApi.repository.EmployeeRepository;
import by.sva.restApi.util.EmployeeModelAssembler;

import org.mockito.junit.jupiter.MockitoExtension;

/* Тестирование без базы данных
 * Тесты идут не в порядке написания, поэтому результат работы одного теста 
 * зависит от работы предыдущих тестов. Это неправильный подход.
 */

//@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class EmployeeControllerTest2 {

	EmployeeRepository mockRepository = Mockito.mock(EmployeeRepository.class);
	
	@Value(value = "${local.server.port}")
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
//	@Autowired
//	EmployeeModelAssembler assembler;
//	
//	private EmployeeController controller = new EmployeeController(mockRepository, assembler);
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	private MockMvc mockMvc;
	
	// для преобразования объекта в JSON
	private HttpMessageConverter mappingJackson2HttpMessageConverter;
	
	// для преобразования объекта в JSON
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
	
	// для преобразования объекта в JSON
	@Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();
    }

	@BeforeEach
	public void init() {
		mockMvc  = webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
	public void testGetAll() {
//		assertNotNull(mockRepository);
//		Mockito.when(mockRepository.findAll()).thenReturn(null);
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/employees/", String.class)).contains("Baggins");
	}
	
	@Disabled
	@Test
	public void testGetAllisOk() throws Exception {
		//MockMvc mockMvc  = webAppContextSetup(webApplicationContext).build();
		mockMvc.perform(get("http://localhost:" + port + "/employees/"))
				.andExpect(status().isOk());
	}
	/*
	// Добавляет новый объект и проверяет его наличие. т.е. сам метод добавления не тестирует.
	@Disabled
	@Test
	public void testNewEmployee() {
		Employee employee = new Employee("Ligalas", "elf");
		controller.newEmployee(employee);
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/employees/get", Employee[].class)[2]).isEqualTo(employee);
	}
	
	// Тестирует сам метод добавления нового объекта
	@Disabled
	@Test
	public void testNewEmployeeFullTest() throws IOException, Exception {
		Employee employee = new Employee("Gimli", "dwarf");
		
		mockMvc.perform(post("/employees/")
				// можно создать объект, преобразовать его в JSON и отправить в content
                .content(json(employee))
                // а можно прямо в content написать строку в формате JSON
				//.content("{\"name\": \"Gimli\",\"role\": \"dwarf\"}")
                
                // в следующей строке contentType можно заменить на MediaType.APPLICATION_JSON
                // тогда можно не создавать глобальную переменную MediaType contentType
                .contentType(contentType)) // задать тип содержимого MediaType.APPLICATION_JSON
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Gimli")));
	}
	
	// Тоже тестирует сам метод добавления нового объекта
	// Добавляет функционал предыдущему тесту
	@Disabled
	@Test
	public void testNewEmployeeFullTest2() throws IOException, Exception {
		Employee employee = new Employee("Gimli", "dwarf");
		
		// в отличии от предыдущего теста получает результат запроса
		MvcResult result = mockMvc.perform(post("/employees/")
				// можно создать объект, преобразовать его в JSON и отправить в content
                .content(json(employee))
                // а можно прямо в content написать строку в формате JSON
				//.content("{\"name\": \"Gimli\",\"role\": \"dwarf\"}")
                
                // в следующей строке contentType можно заменить на MediaType.APPLICATION_JSON
                // тогда можно не создавать глобальную переменную MediaType contentType
                .contentType(contentType))
				.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Gimli")))
                .andReturn(); // возвращает результат запроса
		
		// преобразует запрос в текст
		String content = result.getResponse().getContentAsString();
		System.out.println(content);
		Assert.hasText(employee.getName(), content);
		Assert.hasText(employee.getRole(), content);
	}

	// тест на получение объекта по id
	@Disabled
	@Test
	public void testGetOne() throws Exception {
		Employee employee = this.restTemplate.getForObject("http://localhost:" + port + "/employees/get", Employee[].class)[0];
		mockMvc.perform(get("http://localhost:" + port + "/employees/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(employee.getId().intValue())));
	}

	// тест на изменение бъекта
	@Disabled
	@Test
	public void testReplaceEmployee() throws IOException, Exception {
		assertThat(controller).isNotNull();
		Employee employee = new Employee("Elront", "lord");
		Long id = 1L;
		employee.setId(id);
		
		mockMvc.perform(put("/employees/" + id)
                .content(json(employee))
                // в следующей строке contentType можно заменить на MediaType.APPLICATION_JSON
                // тогда можно не создавать глобальную переменную MediaType contentType
                .contentType(contentType))
                .andExpect(status().isOk());
	}

	// тест на удаление объекта
	@Disabled
	@Test
	public void testDeleteEmployee() throws Exception {
		assertThat(controller).isNotNull();
		mockMvc.perform(delete("http://localhost:" + port + "/employees/2"))
				.andExpect(status().isOk());
		mockMvc.perform(get("http://localhost:" + port + "/employees/2"))
				.andExpect(status().isNotFound());
	}
	*/
	// тест на получение списка объектов
	@Disabled
	@Test
	public void testGet() {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/employees/get", Employee[].class)).hasSize(1);
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/employees/get", Employee[].class)[0].getName()).isEqualTo("Bilbo Baggins");
	}
	
	// тест на вызов ошибки, если объект не найден
	@Disabled
	@Test
	public void testEmployeeNotFound() throws Exception {
        mockMvc.perform(get("http://localhost:" + port + "/employees/10"))
        		.andExpect(status().isNotFound());
    }
	
	// Конвертирует в формат JSON для POST и PUT методов
	private String json(Object object) throws IOException {
		System.out.println(object.toString());
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(
                object, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
