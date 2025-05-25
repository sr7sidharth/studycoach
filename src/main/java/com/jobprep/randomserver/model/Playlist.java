package com.jobprep.randomserver.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {
    private String url;

    @Id
    private String playlistId;

    private ArrayList<Video> videoList;
}
