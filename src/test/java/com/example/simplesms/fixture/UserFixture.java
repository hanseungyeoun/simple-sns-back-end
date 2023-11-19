package com.example.simplesms.fixture;

import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.user.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static com.example.simplesms.fixture.MultipartFileFixture.createMockMultipartFile;

public class UserFixture {

    public static final String userEmailFixture = "test@eamil.com";

    public static User createUser() {
        return createUser(1L, userEmailFixture, "password", "nick");
    }

    public static User createUser(Long userId, String email, String password, String nickName) {
        User user = User.BySingUpBuilder()
                .email(email)
                .password(password)
                .nickName(nickName)
                .build();

        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }

    public static UserJoinRequest createUserJoinRequest() {
        return new UserJoinRequest(userEmailFixture, "password", "nickName");
    }

    public static UserJoinRequest createUserJoinRequest(String email, String password, String nickName) {
        return new UserJoinRequest(email, password, nickName);
    }

    public static UserLoginRequest createUserLoginRequest() {
        return new UserLoginRequest(userEmailFixture, "pass");
    }

    public static UserInfoResponse createUserInfoResponse(Long userId, String email, String nickName, String profileImage, String description) {
        return new UserInfoResponse(userId, email, nickName, profileImage, description);
    }

    public static UserInfoResponse createUserInfoResponse(String email) {
        return createUserInfoResponse(1L, email, "nickName", "profileImage", "description");
    }

    public static UserDetailInfoResponse createUserDetailInfoResponse() {
        Long userId = 1L;
        String profileImage = "profileImage";
        String nickName = "nickName";
        String description = "description";
        Integer feedCount = 0;
        Integer passionIndex = 0;
        Integer hashtagCount = 0;

        return new UserDetailInfoResponse(
                userId,
                userEmailFixture,
                nickName,
                profileImage,
                description,
                feedCount,
                passionIndex,
                hashtagCount,
                List.of(),
                List.of()
        );
    }

    public static UserProfileUpdateRequest createUserProfileUpdateRequest() {
        String nickName = "nickName";
        String description = "description";
        return new UserProfileUpdateRequest(
                nickName,
                description,
                createMockMultipartFile("file")
        );
    }

    public static LoginResponse createLoginResponse() {
        return new LoginResponse(1L, "token");
    }

}
