package com.hackathon.inditex.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hackathon.inditex.Entities.Center;

@Repository
public interface CenterRepository extends JpaRepository<Center, Long> {

}
