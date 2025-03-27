package com.hackathon.inditex.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackathon.inditex.Entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
