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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

//для реализации статического метода methodOn
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@Tag(name = "Order controller", description = "Контроллер запросов по заказам")
public class OrderController {
	private final OrderRepository repository;
	private final OrderModelAssembler assembler;
	
	public OrderController(OrderRepository repository, OrderModelAssembler assembler) {
		this.repository = repository;
		this.assembler = assembler;
	}
	
	@GetMapping("/orders")
	@Operation(summary = "Get list of orders", description = "Получить список заказов")
	public CollectionModel<EntityModel<Order>> getAll(){
		List<EntityModel<Order>> orders = repository.findAll().stream()
				.map(assembler::toModel)
				.collect(Collectors.toList());
		
		return CollectionModel.of(orders, linkTo(methodOn(OrderController.class).getAll()).withSelfRel());
	}

	@GetMapping("/orders/{id}")
	@Operation(summary = "Get order by Id", description = "Получить данные заказа по Id")
	public EntityModel<Order> getOne(@PathVariable @Parameter(description = "Идентификатор заказа") Long id) {
		Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		
		return assembler.toModel(order);
	}
	
	@PostMapping("/orders")
	@Operation(summary = "New order", description = "Создать новый заказ")
	public ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order){
		order.setStatus(Status.IN_PROGRESS);
		Order newOrder = repository.save(order);
		
		return ResponseEntity.created(linkTo(methodOn(OrderController.class).getOne(newOrder.getId()))
				.toUri()).body(assembler.toModel(newOrder));
	}

	@DeleteMapping("/orders/{id}/cancel")
	@Operation(summary = "Cancel order by Id", description = "Изменить статус заказа на Cancelled")
	public ResponseEntity<?> cancel(@PathVariable @Parameter(description = "Идентификатор заказа") Long id) {
		Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		
		if(order.getStatus() == Status.IN_PROGRESS) {
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
	@Operation(summary = "Complete order", description = "Изменить статус заказа на Completed")
	public ResponseEntity<?> complete(@PathVariable @Parameter(description = "Идентификатор заказа") Long id) {
		Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
		
		if(order.getStatus() == Status.IN_PROGRESS) {
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
