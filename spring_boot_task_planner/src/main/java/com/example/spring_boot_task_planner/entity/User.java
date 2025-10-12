package com.example.spring_boot_task_planner.entity;

import com.example.spring_boot_task_planner.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "app_user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 64, nullable = false, unique = true)
  private String username;

  @Column(length = 64, nullable = false, unique = true)
  private String email;

  @Column(length = 64, nullable = false)
  private String password;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Task> tasks;

  @NotNull private UserRole role = UserRole.BASIC;

  @NotNull private boolean banned = false;
}
