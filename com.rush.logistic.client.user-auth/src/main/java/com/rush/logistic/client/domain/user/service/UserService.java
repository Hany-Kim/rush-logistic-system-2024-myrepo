package com.rush.logistic.client.domain.user.service;

import com.rush.logistic.client.domain.global.exception.user.NoAuthorizationException;
import com.rush.logistic.client.domain.global.exception.user.NotFoundUserException;
import com.rush.logistic.client.domain.user.dto.UserInfoResponseDto;
import com.rush.logistic.client.domain.user.dto.UserUpdateRequestDto;
import com.rush.logistic.client.domain.user.entity.User;
import com.rush.logistic.client.domain.user.enums.UserRoleEnum;
import com.rush.logistic.client.domain.user.repository.UserRepository;
import com.rush.logistic.client.domain.user.repository.UserRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserRepositoryImpl userRepositoryImpl;

    public User findById(Long userId) {

        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));
    }

    public User findByUsername(String username) {

        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));
    }

    public Page<UserInfoResponseDto> getAllUsers(String role, Pageable pageable, Integer size) {

        if(!Objects.equals(role, UserRoleEnum.MASTER.name())){
            throw new NoAuthorizationException();
        }

        return userRepositoryImpl.findAll(pageable,size);
    }

    public UserInfoResponseDto getUserById(String role, String userId, String authenticatedUserId) {

        if(!authenticatedUserId.equals(userId)){
            if(!Objects.equals(role, UserRoleEnum.MASTER.name())) {
                throw new NoAuthorizationException();
            }
        }

        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(NotFoundUserException::new);

        return UserInfoResponseDto.from(user);
    }

    @Transactional(readOnly = false)
    public UserInfoResponseDto updateUser(String role, String userId, UserUpdateRequestDto updateRequestDto) {

        if(!Objects.equals(role, UserRoleEnum.MASTER.name())){
            throw new NoAuthorizationException();
        }

        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(NotFoundUserException::new);

        user.updateUser(updateRequestDto);
        userRepository.save(user);

        return UserInfoResponseDto.from(user);
    }

    @Transactional(readOnly = false)
    public UserInfoResponseDto deleteUser(String role, String userId) {

        if(!Objects.equals(role, UserRoleEnum.MASTER.name())){
            throw new NoAuthorizationException();
        }

        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(NotFoundUserException::new);

        user.setDelete(true);
        userRepository.save(user);

        return UserInfoResponseDto.from(user);
    }
}
