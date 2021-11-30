package com.amiroshnikov.task.tracker.store.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    @Builder.Default
    private Instant createAt = Instant.now();

    private String description;

    @OneToOne
    TaskEntity leftTask;

    @OneToOne
    TaskEntity rightTask;

    @ManyToOne
    @JoinColumn(name="task_state_id", nullable=false)
    private TaskStateEntity taskState;

    public Optional<TaskEntity> getLeftTask() {
        return Optional.ofNullable(leftTask);
    }

    public Optional<TaskEntity> getRightTask() {
        return Optional.ofNullable(rightTask);
    }
}
