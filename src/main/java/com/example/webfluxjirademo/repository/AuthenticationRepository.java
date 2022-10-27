package com.example.webfluxjirademo.repository;

import com.example.webfluxjirademo.domain.Authentication;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthenticationRepository extends ReactiveMongoRepository<Authentication, Integer> {

    public Mono<Authentication> findByUsername(String username);
}
