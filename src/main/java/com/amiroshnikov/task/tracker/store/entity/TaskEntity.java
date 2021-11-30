package com.amiroshnikov.task.tracker.store.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

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

    @ManyToOne
    @JoinColumn(name="task_state_id", nullable=false)
    private TaskStateEntity taskState;

}
