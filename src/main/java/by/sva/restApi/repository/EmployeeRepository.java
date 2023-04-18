package by.sva.restApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import by.sva.restApi.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	
}
