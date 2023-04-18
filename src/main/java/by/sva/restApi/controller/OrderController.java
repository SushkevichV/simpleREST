package by.sva.restApi.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import by.sva.restApi.entity.Order;
import by.sva.restApi.entity.Status;
import by.sva.restApi.exception.OrderNotFoundException;
import by.sva.restApi.repository.OrderRepository;
import by.sva.restApi.util.OrderModelAssembler;

//для реализации статического метода methodOn
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class OrderController {
	private final OrderRepository repository;
	private final OrderModelAssembler assembler;
	
	public OrderController(OrderRepository repository, OrderModelAssembler assembler) {
		this.repository = repository;
		this.assembler = assembler;
	}
	
	@GetMapping("/orders")
	public CollectionModel<EntityModel<Order>> getAll(){
		List<EntityModel<Order>> orders = repository.findAll().stream()
				.map(assembler::toModel)
				.collect(Collectors.toList());
		
		return CollectionModel.of(orders, linkTo(methodOn(OrderController.class).getAll()).withSelfRel());
	}

	@GetMapping("/orders/{id}")
	public EntityModel<Order> getOne(@PathVariable Long id) {
		Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		
		return assembler.toModel(order);
	}
	
	@PostMapping("/orders")
	public ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order){
		order.setStatus(Status.IN_PROGRSS);
		Order newOrder = repository.save(order);
		
		return ResponseEntity.created(linkTo(methodOn(OrderController.class).getOne(newOrder.getId()))
				.toUri()).body(assembler.toModel(newOrder));
	}

	@DeleteMapping("/orders/{id}/cancel")
	public ResponseEntity<?> cancel(@PathVariable Long id) {
		Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		
		if(order.getStatus() == Status.IN_PROGRSS) {
			order.setStatus(Status.CANCELLED);
			return ResponseEntity.ok(assembler.toModel(repository.save(order)));
		}
		
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
				.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
				.body(Problem.create()
							.withTitle("Method Not allowed")
							.withDetail("You can't cancel an order that is in the " + order.getStatus() + " status"));
	}

	@PutMapping("/orders/{id}/complete")
	public ResponseEntity<?> complete(@PathVariable Long id) {
		Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		
		if(order.getStatus() == Status.IN_PROGRSS) {
			order.setStatus(Status.COMPLETED);
			return ResponseEntity.ok(assembler.toModel(repository.save(order)));
		}
		
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
				.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
				.body(Problem.create()
						.withTitle("Method not allowed")
						.withDetail("You can't complete an order that is in " + order.getStatus() + " status"));
	}

}
