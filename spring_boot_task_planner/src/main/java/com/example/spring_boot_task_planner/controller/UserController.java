package com.example.spring_boot_task_planner.controller;

import com.example.spring_boot_task_planner.dto.LoginRequest;
import com.example.spring_boot_task_planner.dto.RegisterRequest;
import com.example.spring_boot_task_planner.dto.UserResponse;
import com.example.spring_boot_task_planner.entity.User;
import com.example.spring_boot_task_planner.enums.UserRole;
import com.example.spring_boot_task_planner.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {

  private final UserService userService;

  @Value("${admin.email}")
  private String adminEmail;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponse> registerUser(
      @Valid @RequestBody RegisterRequest model, BindingResult bindingResult) {

    List<String> errors = new ArrayList<>();

    if (bindingResult.hasErrors()) {
      bindingResult.getFieldErrors().forEach(err -> errors.add(err.getDefaultMessage()));
    }

    if (!model.getPassword().equals(model.getConfirmPassword())) {
      errors.add("Passwords do not match");
    }

    if (userService.findByUsername(model.getUsername()).isPresent()) {
      errors.add("Username already taken");
    }

    if (userService.findByEmail(model.getEmail()).isPresent()) {
      errors.add("Email already registered");
    }

    if (!errors.isEmpty()) {
      return ResponseEntity.badRequest().body(UserResponse.error(String.valueOf(errors)));
    }

    User user = new User();
    user.setUsername(model.getUsername());
    user.setEmail(model.getEmail());
    user.setPassword(model.getPassword());

    if (adminEmail.equalsIgnoreCase(model.getEmail())) {
      user.setRole(UserRole.ADMIN);
    }

    userService.register(user);
    return ResponseEntity.ok().body(UserResponse.user(user));
  }

  @PostMapping("/login")
  public ResponseEntity<UserResponse> login(@RequestBody LoginRequest model) {

    Optional<User> user = userService.findByEmail(model.getEmail());

    if (user.isEmpty()) {
      return ResponseEntity.badRequest().body(UserResponse.error("Wrong email"));
    }

    if (!userService.checkPassword(user.get(), model.getPassword())) {
      return ResponseEntity.badRequest().body(UserResponse.error("Wrong password"));
    }

    return ResponseEntity.ok().build();
  }

  @GetMapping("/myprofile")
  public ResponseEntity<UserResponse> showProfile(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {

    String email = currentUser.getUsername();
    User user = userService.findByEmail(email).orElse(null);

    if (user != null) {
      return ResponseEntity.ok().body(UserResponse.user(user));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(UserResponse.error("User " + currentUser.getUsername() + " not found"));
    }
  }

  @DeleteMapping("/deleteaccount")
  public ResponseEntity<UserResponse> deleteAccount(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {

    String email = currentUser.getUsername();
    User user = userService.findByEmail(email).orElse(null);

    if (user != null) {
      userService.deleteUser(user);

      return ResponseEntity.ok().body(UserResponse.user(user));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(UserResponse.error("User " + currentUser.getUsername() + " not found"));
    }
  }
}
