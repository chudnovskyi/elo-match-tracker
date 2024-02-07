package com.template.service;

import com.template.mapper.UserMapper;
import com.template.model.exception.UserNotFoundException;
import com.template.model.request.UserRequest;
import com.template.model.response.UserResponse;
import com.template.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public UserResponse getUserBpId(Long userId) {
    return userRepository
        .findById(userId)
        .map(userMapper::mapToResponse)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  public UserResponse createUser(UserRequest request) {
    return Optional.of(request)
        .map(userMapper::mapToEntity)
        .map(userRepository::save)
        .map(userMapper::mapToResponse)
        .orElseThrow();
  }
}
