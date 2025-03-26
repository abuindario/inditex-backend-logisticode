package com.hackathon.inditex.Repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.inditex.Entities.Order;

@Repository
@Transactional
public interface OrderRepository extends CrudRepository<Order, Long> {

}
