package com.jobprep.randomserver.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.ResourceId;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class YTClient {
    private final YouTube youtube;

    public YTClient(YTAuthService authService) throws Exception {
        Credential credential = authService.authorize();
        this.youtube = new YouTube.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("studycoach").build();
    }

    public List<PlaylistItem> fetchFromSourcePlaylist(String playlistId) throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems()
                .list(List.of("snippet,contentDetails"))
                .setPlaylistId(playlistId)
                .setMaxResults(50L);

        return request.execute().getItems();
    }

    public void addToPersonalPlaylist(String videoId, String playlistId) throws IOException {
        ResourceId resourceId = new ResourceId();
        resourceId.setKind("youtube#video");
        resourceId.setVideoId(videoId);

        PlaylistItemSnippet snippet = new PlaylistItemSnippet();
        snippet.setPlaylistId(playlistId);
        snippet.setResourceId(resourceId);

        PlaylistItem item = new PlaylistItem();
        item.setSnippet(snippet);

        youtube.playlistItems().insert(List.of("snippet"), item).execute();
    }
}
