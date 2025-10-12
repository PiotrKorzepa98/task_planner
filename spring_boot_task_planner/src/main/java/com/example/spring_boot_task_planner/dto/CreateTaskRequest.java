package com.example.spring_boot_task_planner.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateTaskRequest {

  @NotEmpty(message = "Title is required")
  private String title;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @NotNull(message = "Date is required")
  private LocalDate date;

  @NotNull(message = "Start time is required")
  private LocalTime startTime;

  @NotNull(message = "End time is required")
  private LocalTime endTime;

  private String description;
}
