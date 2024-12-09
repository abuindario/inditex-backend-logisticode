package com.hackathon.inditex.Repositories;

import org.springframework.data.repository.CrudRepository;

import com.hackathon.inditex.Entities.Order;

public interface OrderRepository extends CrudRepository<Order, Long> {

}
