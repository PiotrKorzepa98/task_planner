package com.example.spring_boot_task_planner.dto;

import com.example.spring_boot_task_planner.entity.User;
import com.example.spring_boot_task_planner.enums.UserRole;
import lombok.Data;

@Data
public class UserDTO {
  private String username;
  private String email;
  private UserRole role;
  private boolean banned;

  private String = "123";

  public UserDTO(User user) {
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.role = user.getRole();
    this.banned = user.isBanned();
  }
}
