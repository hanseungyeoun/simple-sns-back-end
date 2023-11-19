package com.example.simplesms.cache;

import com.example.simplesms.config.IntegrationContainerSupport;
import com.example.simplesms.dto.user.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

@DisplayName("캐시 테스트 - 회원")
class UserCacheRepositoryTest extends IntegrationContainerSupport {

    @Autowired
    private UserCacheRepository userCacheRepository;

    @DisplayName("저장 테스트")
    @Test
    void save_success(){
        //given
        long id = 1L;
        String email = "email";
        String pass = "";
        UserPrincipal userPrincipal =
                new UserPrincipal(id, email, pass, null);

        userCacheRepository.save(userPrincipal);

        //when
        UserPrincipal result = userCacheRepository.findByUsername(userPrincipal.getUsername()).get();

        //then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getEmail()).isEqualTo(email);
    }
}