package com.example.spring_boot_task_planner.controller;

import com.example.spring_boot_task_planner.dto.CreateTaskRequest;
import com.example.spring_boot_task_planner.dto.TaskResponse;
import com.example.spring_boot_task_planner.entity.Task;
import com.example.spring_boot_task_planner.entity.User;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mytasks")
public class TaskController {

  private final UserService userService;
  private final TaskService taskService;

  public TaskController(UserService userService, TaskService taskService) {
    this.taskService = taskService;
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<TaskResponse> showMyTasks(
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {

    String email = currentUser.getUsername();

    User user = userService.findByEmail(email).orElse(null);

    if (user != null) {
      List<Task> tasks = taskService.findTasksByUser(user);
      return ResponseEntity.ok().body(TaskResponse.taskList(tasks));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(TaskResponse.error("User " + currentUser.getUsername() + " not found"));
    }
  }

  @PostMapping("/create")
  public ResponseEntity<TaskResponse> createTask(
      @Valid @RequestBody CreateTaskRequest model,
      BindingResult bindingResult,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {

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

    String email = currentUser.getUsername();
    User user = userService.findByEmail(email).orElse(null);

    if (user != null) {
      Task task = new Task();
      task.setTitle(model.getTitle());
      task.setDate(model.getDate());
      task.setStartTime(model.getStartTime());
      task.setEndTime(model.getEndTime());
      task.setDescription(model.getDescription());

      taskService.createTask(task, user);

      return ResponseEntity.ok().body(TaskResponse.task(task));
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(TaskResponse.error("User " + currentUser.getUsername() + " not found"));
    }
  }

  @PutMapping("/edit/{id}")
  public ResponseEntity<TaskResponse> editTask(
      @PathVariable Long id,
      @Valid @RequestBody CreateTaskRequest model,
      BindingResult bindingResult,
      @AuthenticationPrincipal User currentUser) {

    Task exsistingTask = taskService.findById(id).orElse(null);

    if (exsistingTask == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(TaskResponse.error("Task " + id + " not found"));
    }

    if (!exsistingTask.getUser().getEmail().equals(currentUser.getUsername())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(TaskResponse.error("User " + currentUser.getEmail() + " is not the task owner"));
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

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<TaskResponse> deleteTask(
      @PathVariable Long id,
      @AuthenticationPrincipal org.springframework.security.core.userdetails.User currentUser) {
    Task task = taskService.findById(id).orElse(null);

    if (task == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(TaskResponse.error("Task " + id + " not found"));
    }

    if (task.getUser().getEmail().equals(currentUser.getUsername())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(TaskResponse.error("User " + currentUser.getUsername() + " is not the task owner"));
    }

    taskService.deleteTask(task);
    return ResponseEntity.ok().body(TaskResponse.task(task));
  }
}
