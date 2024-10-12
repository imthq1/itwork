package com.example.demo.repository;

import com.example.demo.domain.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {
    Subscriber save(Subscriber subscriber);
    Boolean existsByEmail(String email);
    Subscriber findById(long id);
    List<Subscriber> findAll();
    Subscriber findByEmail(String email);
}

