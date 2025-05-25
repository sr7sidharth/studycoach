package com.jobprep.randomserver.youtube;

import com.google.api.client.auth.oauth2.Credential;
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

    public List<SearchResult> searchForVideos(String query, long maxResults) throws IOException {
        YouTube.Search.List searchRequest = youtube.search()
                .list(List.of("snippet"))
                .setQ(query)
                .setMaxResults(maxResults)
                .setType(List.of("video"))
                .setOrder("relevance")
                .setFields("items(id/videoId,snippet/title,snippet/channelTitle,snippet/publishedAt)");
        SearchListResponse response = searchRequest.execute();
        return response.getItems();
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

    public void clearLCPlaylist() throws IOException {
        YouTube.PlaylistItems.List request = youtube.playlistItems()
                .list(List.of("id"))
                .setPlaylistId(LC_PLAYLIST_ID)
                .setMaxResults(20L);
        List<PlaylistItem> items = request.execute().getItems();

        for (PlaylistItem item: items){
            youtube.playlistItems()
                    .delete(item.getId()).execute();
        }
    }
}
