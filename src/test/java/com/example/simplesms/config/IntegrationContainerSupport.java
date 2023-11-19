package com.example.simplesms.config;

import com.example.simplesms.annotation.IntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@IntegrationTest
@SpringBootTest
@Testcontainers
public abstract class IntegrationContainerSupport {
    @Container
    static final GenericContainer MY_REDIS_CONTAINER;

    static {
        MY_REDIS_CONTAINER = new GenericContainer<>("redis:6")
                .withExposedPorts(6379);
        MY_REDIS_CONTAINER.start();
    }

    // 동적 설정값 매핑
    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry){
        // redis
        registry.add("spring.redis.host", MY_REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", () -> "" + MY_REDIS_CONTAINER.getMappedPort(6379));
    }

}
