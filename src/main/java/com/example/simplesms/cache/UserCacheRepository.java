package com.example.simplesms.cache;

import com.example.simplesms.dto.user.UserPrincipal;
import com.example.simplesms.response.exception.IllegalStatusException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisConnectionException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private static final Duration USER_CACHE_TTL = Duration.ofDays(3);
    private final RedisTemplate<String, String> userRedisTemplate;
    private final ObjectMapper objectMapper;

    public void save(UserPrincipal user) {
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {}({})", key, user);

        try {
            userRedisTemplate.opsForValue().set(key, serializeUserPrincipal(user), USER_CACHE_TTL);
            log.info("[UserCacheRepository save success] id: {}", user.getId());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (RedisConnectionFailureException e) {
            log.error("[UserCacheRepository save error]: {}", e.getMessage());
        } catch (RedisConnectionException e) {
            log.error("[UserCacheRepository save error]: {}", e.getMessage());
        }
    }

    public Optional<UserPrincipal> findByUsername(String userName) {
        try {
            String value = userRedisTemplate.opsForValue().get(getKey(userName));
            if(Objects.isNull(value)){
                return Optional.empty();
            }

            UserPrincipal userPrincipal = deserializeUserPrincipal(value);
            log.info("Get User from Redis {}", userPrincipal);
            return Optional.of(userPrincipal);

        } catch (JsonProcessingException e) {
            log.error("[UserCacheRepository findByUsername error]: {}", e.getMessage());
        } catch (RedisConnectionFailureException e) {
            log.error("[UserCacheRepository findByUsername error]: {}", e.getMessage());
        } catch (RedisConnectionException e) {
            log.error("[UserCacheRepository findByUsername error]: {}", e.getMessage());
        }

        return Optional.empty();
    }

    private String getKey(String userName) {
        return "UID:" + userName;
    }

    private String serializeUserPrincipal(UserPrincipal pharmacyDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(pharmacyDto);
    }

    private UserPrincipal deserializeUserPrincipal(String value) throws JsonProcessingException {
        return objectMapper.readValue(value, UserPrincipal.class);
    }
}