package com.amiroshnikov.task.tracker.api.factory;

import com.amiroshnikov.task.tracker.api.dto.ProjectDto;
import com.amiroshnikov.task.tracker.store.entity.ProjectEntity;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
public class ProjectDtoFactory {

    public ProjectDto makeProjectDto(ProjectEntity entity) {

        return ProjectDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createAt(entity.getCreateAt())
                .updateAt(entity.getUpdateAt())
                .build();

    }
}
