package com.amiroshnikov.task.tracker.api.factory;

import com.amiroshnikov.task.tracker.api.dto.TaskDto;
import com.amiroshnikov.task.tracker.store.entity.TaskEntity;
import org.springframework.stereotype.Component;

@Component
public class TaskDtoFactory {

    public TaskDto makeTaskDto(TaskEntity entity) {

        return TaskDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createAt(entity.getCreateAt())
                .description(entity.getDescription())
                .leftTaskId(entity.getLeftTask().map(TaskEntity::getId).orElse(null))
                .rightTaskId(entity.getRightTask().map(TaskEntity::getId).orElse(null))
                .build();

    }
}
