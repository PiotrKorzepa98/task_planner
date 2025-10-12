package com.example.spring_boot_task_planner.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

  @NotEmpty(message = "Username is required")
  @Size(min = 3, message = "Username must be at least 3 characters long")
  private String username;

  @Email
  @NotEmpty(message = "Email is required")
  private String email;

  @NotEmpty(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

  @NotEmpty(message = "Confirm your password")
  private String confirmPassword;

  @AssertTrue(message = "You have to agree to the terms")
  private Boolean terms;
}
