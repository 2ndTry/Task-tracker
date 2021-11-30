package com.amiroshnikov.task.tracker.api.controller;

import com.amiroshnikov.task.tracker.api.dto.AckDto;
import com.amiroshnikov.task.tracker.api.dto.ProjectDto;
import com.amiroshnikov.task.tracker.api.exception.BadRequestException;
import com.amiroshnikov.task.tracker.api.exception.NotFoundException;
import com.amiroshnikov.task.tracker.api.factory.ProjectDtoFactory;
import com.amiroshnikov.task.tracker.api.controller.helper.ControllerHelper;
import com.amiroshnikov.task.tracker.store.entity.ProjectEntity;
import com.amiroshnikov.task.tracker.store.repository.ProjectRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
@RestController
public class ProjectController {

    ProjectRepository projectRepository;

    ProjectDtoFactory projectDtoFactory;

    ControllerHelper controllerHelper;

    public static final String FETCH_PROJECTS = "/api/projects";
    public static final String CREATE_PROJECT = "/api/projects";
    public static final String EDIT_PROJECT = "/api/projects/{project_id}";
    public static final String DELETE_PROJECT = "/api/projects/{project_id}";
    public static final String CREATE_OR_UPDATE_PROJECT = "/api/projects";

    @GetMapping(FETCH_PROJECTS)
    public List<ProjectDto> fetchProjects(
            @RequestParam(value = "prefix_name", required = false)
                    Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName.filter(
                prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartsWithIgnoreCase)
                .orElseGet(projectRepository::streamAllBy);

        if (optionalPrefixName.isPresent()) {
            projectStream = projectRepository.streamAllByNameStartsWithIgnoreCase(optionalPrefixName.get());
        } else {
            projectStream = projectRepository.streamAllBy();
        }

        return projectStream.map(projectDtoFactory::makeProjectDto)
                .collect(Collectors.toList());
    }

    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDto createOrUpdateProject(
            @RequestParam(value = "project_id", required = false) Optional<Long> optionalProjectId,
            @RequestParam(value = "project_name", required = false) Optional<String> optionalProjectName) {

        optionalProjectName = optionalProjectName.filter(projectName -> !projectName.trim().isEmpty());

        boolean isCreate = !optionalProjectId.isPresent();

        if (isCreate && !optionalProjectName.isPresent()) {
            throw new BadRequestException("Project name can't be empty.");
        }

        final ProjectEntity project = optionalProjectId
                .map(controllerHelper::getProjectOrThrowException)
                .orElseGet(() -> ProjectEntity.builder().build());

        optionalProjectName
                .ifPresent(projectName -> {

                    projectRepository
                            .findByName(projectName)
                            .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project.getId()))
                            .ifPresent(anotherProject -> {
                                throw new BadRequestException(
                                        String.format("Project \"%s\" already exists.", projectName)
                                );
                            });

                    project.setName(projectName);
                });

        final ProjectEntity savedProject = projectRepository.saveAndFlush(project);

        return projectDtoFactory.makeProjectDto(savedProject);
    }

    @PostMapping(CREATE_PROJECT)
    public ProjectDto createProject(
            @RequestParam("project_name") String projectName) {

        if (projectName.trim().isEmpty()) {
            throw new BadRequestException("Name can't be empty!");
        }

        projectRepository
                .findByName(projectName)
                .ifPresent(project -> {
                    throw new BadRequestException(
                            String.format("Project \"%s\" already exists!", projectName));
                });

        ProjectEntity project = projectRepository.saveAndFlush(
                ProjectEntity
                        .builder()
                        .name(projectName)
                        .build()
        );


        return projectDtoFactory.makeProjectDto(project);
    }

    @PatchMapping(EDIT_PROJECT)
    public ProjectDto editProject(
            @PathVariable("project_id") Long projectId,
            @RequestParam("project_name") String project_name) {

        if (project_name.trim().isEmpty()) {
            throw new BadRequestException("Name can't be empty!");
        }

        ProjectEntity project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Project with \"s\" not found!", projectId)
                ));

        projectRepository
                .findByName(project_name)
                .filter(anotherProject ->
                        !Objects.equals(anotherProject.getId(), projectId))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(
                            String.format("Project \"%s\" already exists!", project_name));
                });

        project.setName(project_name);

        project = projectRepository.saveAndFlush(project);

        return projectDtoFactory.makeProjectDto(project);
    }


    @DeleteMapping(DELETE_PROJECT)
    public AckDto deleteProject(
            @PathVariable("project_id") Long projectId) {

        projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Project with \"s\" not found!", projectId)
                ));

        projectRepository.deleteById(projectId);

        return AckDto.makeDefault(true);
    }

}
