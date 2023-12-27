package com.dastan.spring.reactive;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataMongoTest
@Slf4j
@Testcontainers
public class RepositoryIT<T> {

    @Container
    protected static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4");

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;


    @DynamicPropertySource
    static void registerMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongoDBContainer.getReplicaSetUrl());
    }

    @BeforeAll
    public static void before() {
        mongoDBContainer.start();
        log.info("started mongoDB container " + mongoDBContainer.getConnectionString());
    }

    @AfterAll
    public static void tearDown() {
        mongoDBContainer.stop();
    }

    protected void cleanUp(Class<T> type) throws InterruptedException {
        var latch = new CountDownLatch(1);

        this.reactiveMongoTemplate.remove(type)
                                  .all()
                                  .doOnTerminate(latch::countDown)
                                  .subscribe(r -> log.debug("delete all posts: " + r), e -> log.debug("error: " + e), () -> log.debug("done"));
        latch.await(5000, TimeUnit.MILLISECONDS);
    }

}
