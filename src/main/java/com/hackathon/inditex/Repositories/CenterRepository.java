package com.hackathon.inditex.Repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.hackathon.inditex.Entities.Center;

@Repository
public interface CenterRepository extends CrudRepository<Center, Long> {

}
