package com.example.simplesms.annotation;

import com.example.simplesms.config.TestJpaConfig;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@IntegrationTest
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({TestJpaConfig.class})
@Transactional
public @interface InMemoryDataJpaTest {
}
