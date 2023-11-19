package com.example.simplesms.service.user.query;

import com.example.simplesms.domain.user.User;
import com.example.simplesms.dto.user.UserDetailInfoResponse;
import com.example.simplesms.dto.user.UserInfoResponse;
import com.example.simplesms.repository.query.UserQueryRepository;
import com.example.simplesms.repository.user.UserRepository;
import com.example.simplesms.response.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;
    private final UserRepository userRepository;

    public UserInfoResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("회원을 (%d)을 찾을 수 없습니다.", userId)));

        return UserInfoResponse.fromEntity(user);
    }

    public UserDetailInfoResponse getUserProfileWithPost(Long userId) {
        return userQueryRepository.findUserDetailByUserId(userId);
    }


}
