package com.example.spring_boot_task_planner.repository;

import com.example.spring_boot_task_planner.entity.Task;
import com.example.spring_boot_task_planner.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
  Optional<Task> findByTitle(String title);

  List<Task> findByUser(User user);
}
