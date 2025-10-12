package com.example.spring_boot_task_planner.dto;

import com.example.spring_boot_task_planner.entity.User;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class UserResponse {
  Optional<UserDTO> user = Optional.empty();
  Optional<List<UserDTO>> userList = Optional.empty();
  Optional<String> errors = Optional.empty();

  public static UserResponse error(String errorMessage) {
    var result = new UserResponse();
    result.errors = Optional.of(errorMessage);
    return result;
  }

  public static UserResponse user(User user) {
    var result = new UserResponse();

    if (user != null) {
      result.user = Optional.of(new UserDTO(user));
    }
    return result;
  }

  public static UserResponse userList(List<User> userList) {
    var result = new UserResponse();
    result.userList = Optional.of(userList.stream().map(UserDTO::new).collect(Collectors.toList()));
    return result;
  }
}
