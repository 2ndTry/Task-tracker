package com.amiroshnikov.task.tracker.api.controller;

import com.amiroshnikov.task.tracker.api.controller.helper.ControllerHelper;
import com.amiroshnikov.task.tracker.api.dto.TaskDto;
import com.amiroshnikov.task.tracker.api.factory.TaskDtoFactory;
import com.amiroshnikov.task.tracker.store.entity.TaskStateEntity;
import com.amiroshnikov.task.tracker.store.repository.TaskRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Transactional
@RestController
public class TaskController {

    TaskRepository taskRepository;

    TaskDtoFactory taskDtoFactory;

    ControllerHelper controllerHelper;

    public static final String GET_TASKS = "/api/projects/{project_id}/task-states/{task_state_id}/tasks";

    @GetMapping(GET_TASKS)
    public List<TaskDto> getTasks(
            @PathVariable(name = "project_id") Long projectId,
            @PathVariable(name = "task_state_id") Long taskStateId) {
        TaskStateEntity taskStateEntity = controllerHelper.getTaskStateOrThrowException(taskStateId);

        return taskStateEntity
                .getTasks()
                .stream()
                .map(taskDtoFactory::makeTaskDto)
                .collect(Collectors.toList());
    }
}
