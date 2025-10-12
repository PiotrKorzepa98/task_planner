package com.example.spring_boot_task_planner.service;

import com.example.spring_boot_task_planner.entity.Task;
import com.example.spring_boot_task_planner.entity.User;
import com.example.spring_boot_task_planner.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

  private final TaskRepository taskRepository;

  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public void createTask(Task task, User user) {
    task.setUser(user);
    taskRepository.save(task);
  }

  public List<Task> findTasksByUser(User user) {
    return taskRepository.findByUser(user);
  }

  public Optional<Task> findById(Long id) {
    return taskRepository.findById(id);
  }

  public Optional<Task> findByTitle(String title) {
    return taskRepository.findByTitle(title);
  }

  public void updateTask(Task task) {
    taskRepository.save(task);
  }

  public void deleteTask(Task task) {
    taskRepository.delete(task);
  }
}
