package com.example.webfluxjirademo.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.util.Collections;

@Configuration
@PropertySource("classpath:application.properties")
public class MongoConfiguration extends AbstractReactiveMongoConfiguration {

    @Value("${spring.data.mongodb.host}")
    private String host;
    @Value("${spring.data.mongodb.database}")
    private String database;
    @Value("${spring.data.mongodb.port}")
    private Integer port;
    @Value("${spring.data.mongodb.username}")
    private String username;
    @Value("${spring.data.mongodb.password}")
    private String password;

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient(), this.database);
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder
                .credential(MongoCredential.createCredential(
                        this.username, this.database, this.password.toCharArray()))
                .applyToClusterSettings(settings -> settings.hosts(
                        Collections.singletonList(new ServerAddress(this.host, this.port))));
    }

    @Override
    protected String getDatabaseName() {
        return this.database;
    }
}
