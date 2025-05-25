package com.jobprep.randomserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jobprep.randomserver.model.Video;
import com.jobprep.randomserver.repository.VideoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class VideoDataLoader {
    private final VideoRepository videoRepository;
    private final ObjectMapper objectMapper;

    public VideoDataLoader(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() throws IOException {
        InputStream input = getClass().getResourceAsStream("/initialize_videos.json");
        List<Video> videos = Arrays.asList(
                objectMapper.readValue(input, Video[].class)
        );
        videoRepository.saveAll(videos);
    }
}
