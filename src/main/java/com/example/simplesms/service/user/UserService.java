package com.example.simplesms.service.user;

import com.example.simplesms.cache.UserCacheRepository;
import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.user.LoginResponse;
import com.example.simplesms.dto.user.UserInfoResponse;
import com.example.simplesms.dto.user.UserJoinRequest;
import com.example.simplesms.dto.user.UserProfileUpdateRequest;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.response.exception.BaseException;
import com.example.simplesms.response.exception.EntityNotFoundException;
import com.example.simplesms.response.exception.ErrorCode;
import com.example.simplesms.response.exception.IllegalStatusException;
import com.example.simplesms.security.TokenProvider;
import com.example.simplesms.service.upload.FileUploder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserCacheRepository userCacheRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder encoder;
    private final FileUploder fileUploder;


    @Transactional
    public Long join(UserJoinRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(userAccount -> {
            throw new BaseException(ErrorCode.DUPLICATED_USER_NAME);
        });

        User user = User.BySingUpBuilder()
                .email(request.email())
                .password(encoder.encode(request.password()))
                .nickName(request.nickName())
                .build();

        return userRepository.save(user).getId();
    }

    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(String.format("회원을 (%s)을 찾을 수 없습니다.", email)));

        if (!encoder.matches(password, user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        return new LoginResponse(
                user.getId(),
                tokenProvider.createToken(email));
    }

    @Transactional
    public UserInfoResponse updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("회원을 (%d)을 찾을 수 없습니다.", userId)));

        String profileImage = null;

        try {
            profileImage = fileUploder.storeFile(request.file());
        } catch (IOException e) {
            throw new IllegalStatusException(String.format("프로필 파일 저장 도중 오류가 발생 하였습니다. (%s)", request.file()));
        }

        user.changeProfile(request.nickName(), profileImage, request.description());
        return UserInfoResponse.fromEntity(user);
    }
}
