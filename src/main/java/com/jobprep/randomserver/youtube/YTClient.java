package com.jobprep.randomserver.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class YTClient {
    private static final String CHANNEL_ID = "UCN5xN3dgAk3_PHmmQO2eRow";
    private static final String LC_PLAYLIST_ID = "PLmeMsiT-_DHMUzCdUpFsaNs-bmfwYiVQv";
    private final YouTube youtube;

    public YTClient(YTAuthService authService) throws Exception {
        Credential credential = authService.loadCredential();
        this.youtube = new YouTube.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("studycoach").build();
    }

    public List<Playlist> getPlaylists() throws IOException {
        YouTube.Playlists.List request = youtube.playlists()
                .list(List.of("snippet,contentDetails"));
        PlaylistListResponse response = request.setChannelId(CHANNEL_ID)
                .setMaxResults(25L)
                .execute();
        return response.getItems();
    }

    public List<PlaylistItem> fetchFromSourcePlaylist(String playlistId) throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems()
                .list(List.of("snippet,contentDetails"))
                .setPlaylistId(playlistId)
                .setMaxResults(50L);

        return request.execute().getItems();
    }


    public void addVideoToLCPlaylist(String videoId) throws IOException {
        ResourceId resourceId = new ResourceId();
        resourceId.setKind("youtube#video");
        resourceId.setVideoId(videoId);

        PlaylistItemSnippet snippet = new PlaylistItemSnippet();
        snippet.setPlaylistId(LC_PLAYLIST_ID);
        snippet.setResourceId(resourceId);

        PlaylistItem item = new PlaylistItem();
        item.setSnippet(snippet);

        youtube.playlistItems().insert(List.of("snippet"), item).execute();
    }
}
