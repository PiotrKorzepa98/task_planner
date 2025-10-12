package com.example.spring_boot_task_planner.dto;

import lombok.Data;

@Data
public class LoginRequest {

  private String email;
  private String password;
}
