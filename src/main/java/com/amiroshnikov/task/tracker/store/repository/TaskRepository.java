package com.amiroshnikov.task.tracker.store.repository;

import com.amiroshnikov.task.tracker.store.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository <TaskEntity, Long> {

}
