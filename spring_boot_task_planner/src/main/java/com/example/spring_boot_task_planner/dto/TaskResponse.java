package com.example.spring_boot_task_planner.dto;

import com.example.spring_boot_task_planner.entity.Task;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class TaskResponse {
  Optional<TaskDTO> task = Optional.empty();
  private Optional<List<TaskDTO>> taskList = Optional.empty();
  Optional<String> errors = Optional.empty();

  public static TaskResponse error(String errorMessage) {
    var result = new TaskResponse();
    result.errors = Optional.of(errorMessage);
    return result;
  }

  public static TaskResponse task(Task task) {
    var result = new TaskResponse();
    result.task = Optional.of(new TaskDTO(task));
    return result;
  }

  public static TaskResponse taskList(List<Task> taskList) {
    var result = new TaskResponse();
    result.taskList = Optional.of(taskList.stream().map(TaskDTO::new).collect(Collectors.toList()));
    return result;
  }
}
