package com.example.spring_boot_task_planner.entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull private String title;

  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  @NotNull private LocalTime startTime;

  @NotNull private LocalTime endTime;

  @Lob private String description;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
