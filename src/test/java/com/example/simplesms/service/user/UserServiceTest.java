package com.example.simplesms.service.user;

import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.user.LoginResponse;
import com.example.simplesms.dto.user.UserInfoResponse;
import com.example.simplesms.dto.user.UserJoinRequest;
import com.example.simplesms.dto.user.UserProfileUpdateRequest;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.response.exception.BaseException;
import com.example.simplesms.response.exception.EntityNotFoundException;
import com.example.simplesms.response.exception.ErrorCode;
import com.example.simplesms.security.TokenProvider;
import com.example.simplesms.service.upload.FileUploder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Optional;

import static com.example.simplesms.fixture.UserFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@DisplayName("비즈니스 로직 - 회원")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService sut;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private FileUploder fileUploder;

    @DisplayName("회원 가입 시, 존재하는 이메일이면 예외를 발생 시킨다.")
    @Test
    void givenExistUser_whenSaving_thenThrowsException() {
        // Given
        UserJoinRequest request = createUserJoinRequest();
        User existUser = createUser();
        given(userRepository.findByEmail(request.email())).willReturn(Optional.of(existUser));

        // When
        Assertions.assertThatCode(() -> sut.join(request))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.DUPLICATED_USER_NAME.getErrorMsg());

        // Then
        then(userRepository).should().findByEmail(request.email());
        then(encoder).should(never()).encode(request.password());
        then(userRepository).should(never()).save(any(User.class));
    }

    @DisplayName("회원 정보를 입력하면, 새로운 회원 정보를 저장하여 가입시키고 해당 회원 아이디를 리턴한다.")
    @Test
    void givenUserParams_whenSaving_thenSavesUserAccount() {
        // Given
        UserJoinRequest request = createUserJoinRequest();
        User savedUserAccount = createUser();
        given(userRepository.findByEmail(request.email())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(savedUserAccount);
        given(encoder.encode(request.password())).willReturn("encodedPassword");

        // When
        Long result = sut.join(request);

        // Then
        assertThat(result).isEqualTo(savedUserAccount.getId());

        then(encoder).should().encode(request.password());
        then(userRepository).should().findByEmail(request.email());
        then(userRepository).should().save(any(User.class));
    }

    @DisplayName("로그인 시 이메일이 존재하지 않을 시, EntityNotFoundException 이 발생한다. ")
    @Test
    void givenNotExistLoginInfo_whenLogin_thenThrowsEntityNotFoundException() {
        // Given
        String email = "email";
        String pass = "pass";

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // When
        Assertions.assertThatCode(() -> sut.login(email, pass))
                .isInstanceOf(EntityNotFoundException.class);
        // Then

        then(encoder).should(never()).matches(any(), eq(pass));
    }


    @DisplayName("로그인 시 패스워드가 일치하지 않을 시, InvalidPasswordException 이 발생한다.")
    @Test
    void givenNotMatchPassword_whenLogin_thenThrowsInvalidPasswordException() {
        // Given
        String email = "email";
        String pass = "pass";


        User savedUserAccount = createUser();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(savedUserAccount));
        given(encoder.matches(pass, savedUserAccount.getPassword())).willReturn(false);
        // When
        Assertions.assertThatCode(() -> sut.login(email, pass))
                .isInstanceOf(BaseException.class)
                .hasMessage(ErrorCode.INVALID_PASSWORD.getErrorMsg());
        // Then

        then(userRepository).should().findByEmail(email);
        then(encoder).should().matches(pass, savedUserAccount.getPassword());
    }

    @DisplayName("로그인 요청하면, 토큰 키를 반환한다.")
    @Test
    void givenLogin_whenLogin_thenReturnUserIdAndToken() {
        // Given
        String email = "email";
        String pass = "pass";
        String token = "token";


        User savedUserAccount = createUser();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(savedUserAccount));
        given(encoder.matches(pass, savedUserAccount.getPassword())).willReturn(true);
        given(tokenProvider.createToken(email)).willReturn(token);
        // When
        LoginResponse result = sut.login(email, pass);

        // Then
        then(userRepository).should().findByEmail(email);
        then(encoder).should().matches(pass, savedUserAccount.getPassword());

        assertThat(result.id()).isEqualTo(savedUserAccount.getId());
        assertThat(result.accessToken()).isEqualTo(token);
    }

    @DisplayName("존재 하지 않는 회원 정보 업데이트 요청 시 예외를 발생 시킨다.")
    @Test
    void givenNotExistUserId_whenUpdatingUserInfo_thenThrowsException() {
        // Given
        UserProfileUpdateRequest request = createUserProfileUpdateRequest();
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // When
        Assertions.assertThatCode(() -> sut.updateUserProfile(userId, request))
                .isInstanceOf(EntityNotFoundException.class);
        // Then

        then(userRepository).should().findById(userId);
    }

    @DisplayName("회원의 수정 정보를 입력하면, 회원 정보를 수정한다. ")
    @Test
    void givenUpdateUserInfo_whenUpdatingUserInfo_thenUpdateUserInfo() throws IOException {
        // Given
        UserProfileUpdateRequest request = createUserProfileUpdateRequest();
        Long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.of(createUser()));
        given(fileUploder.storeFile(request.file())).willReturn("image.jpg");

        // When
        UserInfoResponse result = sut.updateUserProfile(userId, request);

        // Then
        assertThat(result.nickName()).isEqualTo(request.nickName());
        assertThat(result.description()).isEqualTo(request.description());

        then(userRepository).should().findById(userId);
    }

}