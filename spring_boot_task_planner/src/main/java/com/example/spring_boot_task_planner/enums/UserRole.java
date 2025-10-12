package com.example.spring_boot_task_planner.enums;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
  ADMIN,
  BASIC;

  @Override
  public String getAuthority() {
    return name();
  }
}
