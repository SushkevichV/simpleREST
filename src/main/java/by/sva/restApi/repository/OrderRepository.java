package by.sva.restApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import by.sva.restApi.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
