package com.template.mapper;

import com.template.entity.User;
import com.template.model.request.UserRequest;
import com.template.model.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserResponse mapToResponse(User user) {
    return new UserResponse(user.getUserId(), user.getFirstName(), user.getLastName());
  }

  public User mapToEntity(UserRequest request) {
    return User.builder().firstName(request.firstName()).lastName(request.lastName()).build();
  }
}
