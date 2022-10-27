package com.example.webfluxjirademo.repository;

import com.example.webfluxjirademo.domain.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
class AuthenticationRepositoryTests {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    @Autowired
    private AuthenticationRepository repository;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        authentication = new Authentication(2, "test_user", "test_pwd");
    }

    @Test
    void shouldFindAuthentication() {
        Mono<Authentication> returnResult = mongoTemplate.save(authentication, "authentications");
        assertThat(returnResult.block()).isEqualTo(authentication);

        Mono<Authentication> findResult = repository.findById(authentication.getId());
        assertThat(findResult.block()).isEqualTo(authentication);
    }

    @Test
    void shouldSaveAuthentication() {
        Mono<Authentication> returnResult = repository.save(authentication);
        assertThat(returnResult.block()).isEqualTo(authentication);

        Mono<Authentication> saveResult = mongoTemplate.findById(authentication.getId(), Authentication.class);
        assertThat(saveResult.block()).isEqualTo(authentication);
    }

    @Test
    void shouldDeleteAuthentication() {
        Query query = new Query();
        query.addCriteria(Criteria.where(authentication.getId().toString()));
        Mono<Authentication> returnResult = mongoTemplate.save(authentication, "authentications");
        assertThat(returnResult.block()).isEqualTo(authentication);

        repository.deleteById(authentication.getId());

        Mono<Boolean> deleteResult = mongoTemplate.exists(query, Authentication.class, "authentications");
        assertThat(deleteResult.block()).isFalse();
    }

    @Test
    void shouldFindAuthenticationByUsername() {
        Mono<Authentication> returnResult = mongoTemplate.save(authentication, "authentications");
        assertThat(returnResult.block()).isEqualTo(authentication);

        Mono<Authentication> findResult = repository.findByUsername(authentication.getUsername());
        assertThat(findResult.block()).isEqualTo(authentication);
    }
}
