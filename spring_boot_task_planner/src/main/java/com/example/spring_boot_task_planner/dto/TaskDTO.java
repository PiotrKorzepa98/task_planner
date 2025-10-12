package com.example.spring_boot_task_planner.dto;

import com.example.spring_boot_task_planner.entity.Task;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class TaskDTO {
  private String title;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime ednTime;
  private String description;
  private UserDTO user;

  public TaskDTO(Task task) {
    this.title = task.getTitle();
    this.date = task.getDate();
    this.startTime = task.getStartTime();
    this.ednTime = task.getEndTime();
    this.description = task.getDescription();
    this.user = new UserDTO(task.getUser());
  }
}
