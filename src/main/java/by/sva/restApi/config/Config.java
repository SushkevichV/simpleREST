package by.sva.restApi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import by.sva.restApi.entity.Employee;
import by.sva.restApi.entity.Order;
import by.sva.restApi.entity.Status;
import by.sva.restApi.repository.EmployeeRepository;
import by.sva.restApi.repository.OrderRepository;

@Configuration
public class Config {
	private static final Logger log = LoggerFactory.getLogger(Config.class);
	
	@Bean
	CommandLineRunner initDataBase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {
		return args -> {
			log.info("Preloading " + employeeRepository.save(new Employee("Bilbo Baggins", "burglar")));
			log.info("Preloading " + employeeRepository.save(new Employee("Frodo Baggins", "theif")));
			
			orderRepository.save(new Order("MacBook Pro", Status.COMPLETED));
			orderRepository.save(new Order("iPhone", Status.IN_PROGRSS));
			orderRepository.findAll().forEach(order -> {
				log.info("Preloaded" + order);
			});
		};
		
	}

}
