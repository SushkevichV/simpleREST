package by.sva.restApi.util;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

// для вызова статического метода methodOn
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import by.sva.restApi.controller.EmployeeController;
import by.sva.restApi.entity.Employee;

@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {

	@Override
	public EntityModel<Employee> toModel(Employee employee) {
		return EntityModel.of(employee,
				linkTo(methodOn(EmployeeController.class).getOne(employee.getId())).withSelfRel(),
				linkTo(methodOn(EmployeeController.class).getAll()).withRel("employees")
		);
	}

}
