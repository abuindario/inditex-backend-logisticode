package com.hackathon.inditex.Repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hackathon.inditex.Entities.Order;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

}
