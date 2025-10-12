package com.example.spring_boot_task_planner.controller;

import com.example.spring_boot_task_planner.dto.CreateTaskRequest;
import com.example.spring_boot_task_planner.dto.LoginRequest;
import com.example.spring_boot_task_planner.dto.TaskResponse;
import com.example.spring_boot_task_planner.dto.UserResponse;
import com.example.spring_boot_task_planner.entity.Task;
import com.example.spring_boot_task_planner.entity.User;
import com.example.spring_boot_task_planner.enums.UserRole;
import com.example.spring_boot_task_planner.service.TaskService;
import com.example.spring_boot_task_planner.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

  private final UserService userService;
  private final TaskService taskService;

  public AdminController(UserService userService, TaskService taskService) {
    this.taskService = taskService;
    this.userService = userService;
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

  @GetMapping("/dashboard")
  public ResponseEntity<UserResponse> showDashboard() {
    List<User> users = userService.findAllUsers();

    return ResponseEntity.ok().body(UserResponse.userList(users));
  }

  @GetMapping("/users/view/{id}")
  public ResponseEntity<TaskResponse> showUserView(@PathVariable Long id) {
    User user = userService.findById(id).orElse(null);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(TaskResponse.error("User " + id + " not found"));
    }

    List<Task> tasks = taskService.findTasksByUser(user);
    return ResponseEntity.ok().body(TaskResponse.taskList(tasks));
  }

  @GetMapping("/users/advanced/{id}")
  public ResponseEntity<UserResponse> showUserAdvanced(@PathVariable Long id) {
    User user = userService.findById(id).orElse(null);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(UserResponse.error("User " + id + " not found"));
    }

    return ResponseEntity.ok().body(UserResponse.user(user));
  }

  @PutMapping("/users/advanced/{id}")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable Long id,
      @RequestParam(value = "role") UserRole role,
      @RequestParam(defaultValue = "false") Boolean ban,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {
    User user = userService.findById(id).orElse(null);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(UserResponse.error("User " + id + " not found"));
    }

    if (user.getEmail().equals(currentUser.getUsername())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(UserResponse.error("Cannot change own settings"));
    }

    user.setRole(role);
    user.setBanned(ban);
    userService.updateUser(user);

    return ResponseEntity.ok().body(UserResponse.user(user));
  }

  @DeleteMapping("/users/delete/{id}")
  public ResponseEntity<UserResponse> deleteUser(
      @PathVariable Long id,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {
    User user = userService.findById(id).orElse(null);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(UserResponse.error("User " + id + " not found"));
    }

    if (user.getEmail().equals(currentUser.getUsername())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(UserResponse.error("Cannot delete own account"));
    }

    userService.deleteUser(user);

    return ResponseEntity.ok().body(UserResponse.user(user));
  }

  @GetMapping("/users/{userId}/tasks/edit/{taskId}")
  public ResponseEntity<TaskResponse> showUserTaskEdit(
      @PathVariable Long userId, @PathVariable Long taskId) {
    User user = userService.findById(userId).orElse(null);

    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(TaskResponse.error("User " + userId + " not found"));
    }

    Task task = taskService.findById(taskId).orElse(null);

    if (task == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(TaskResponse.error("Task " + taskId + " not found"));
    }

    if (!task.getUser().getId().equals(userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(TaskResponse.error("User " + userId + " is not task owner"));
    }

    return ResponseEntity.ok().body(TaskResponse.task(task));
  }

  @PutMapping("/users/{userId}/tasks/edit/{taskId}")
  public ResponseEntity<TaskResponse> editUserTask(
      @PathVariable Long userId,
      @PathVariable Long taskId,
      @Valid @RequestBody CreateTaskRequest model,
      BindingResult bindingResult) {

    Task exsistingTask = taskService.findById(taskId).orElse(null);

    if (exsistingTask == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(TaskResponse.error("Task " + taskId + " not found"));
    }

    if (!exsistingTask.getUser().getId().equals(userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(TaskResponse.error("User " + userId + " is not task owner"));
    }

    List<String> errors = new ArrayList<>();

    if (bindingResult.hasErrors()) {
      bindingResult.getFieldErrors().forEach(err -> errors.add(err.getDefaultMessage()));
    }

    if (model.getDate().isBefore(LocalDate.now())) {
      errors.add("The date must be today or later");
    }

    if (model.getDate().equals(LocalDate.now()) && model.getStartTime().isBefore(LocalTime.now())) {
      errors.add("The start time cannot be earlier than now");
    }

    if (model.getEndTime().isBefore(model.getStartTime())
        || model.getEndTime().equals(model.getStartTime())) {
      errors.add("The end time must be after the start time");
    }

    if (!errors.isEmpty()) {
      return ResponseEntity.badRequest().body(TaskResponse.error(String.valueOf(errors)));
    }

    exsistingTask.setTitle(model.getTitle());
    exsistingTask.setDate(model.getDate());
    exsistingTask.setStartTime(model.getStartTime());
    exsistingTask.setEndTime(model.getEndTime());
    exsistingTask.setDescription(model.getDescription());

    taskService.updateTask(exsistingTask);

    return ResponseEntity.ok().body(TaskResponse.task(exsistingTask));
  }

  @DeleteMapping("/users/{userId}/tasks/{taskId}")
  public ResponseEntity<TaskResponse> deleteUserTask(
      @PathVariable Long userId, @PathVariable Long taskId) {
    Task task = taskService.findById(taskId).orElse(null);

    if (task == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(TaskResponse.error("Task " + taskId + " not found"));
    }

    if (!task.getUser().getId().equals(userId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(TaskResponse.error("User " + userId + " is not task owner"));
    }

    taskService.deleteTask(task);

    return ResponseEntity.ok().body(TaskResponse.task(task));
  }
}
