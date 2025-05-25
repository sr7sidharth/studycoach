package com.jobprep.randomserver.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    @Id
    private String videoId;

    private String title;
    private String topic;
    private String url;

    private int duration;

    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
