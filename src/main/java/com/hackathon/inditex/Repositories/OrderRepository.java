package com.hackathon.inditex.Repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hackathon.inditex.Entities.Order;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface OrderRepository extends CrudRepository<Order, Long> {

}
