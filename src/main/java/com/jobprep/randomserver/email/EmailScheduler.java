package com.jobprep.randomserver.email;

import com.jobprep.randomserver.ai.MistralService;
import com.jobprep.randomserver.config.InitialParamsConfig;
import com.jobprep.randomserver.model.Video;
import com.jobprep.randomserver.repository.VideoRepository;
import com.jobprep.randomserver.youtube.YTClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.google.api.services.youtube.model.SearchResult;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class EmailScheduler {
    private final EmailService emailService;
    private final VideoRepository videoRepository;
    private final YTClient ytClient;
    private final MistralService mistralService;
    private final InitialParamsConfig initialParamsConfig;

    public EmailScheduler(EmailService emailService, VideoRepository videoRepository, YTClient ytClient, MistralService mistralService, InitialParamsConfig initialParamsConfig) {
        this.emailService = emailService;
        this.videoRepository = videoRepository;
        this.ytClient = ytClient;
        this.mistralService = mistralService;
        this.initialParamsConfig = initialParamsConfig;
    }

    @Scheduled(cron = "0 0 9 * * 1-5")
    public void addProblems() throws Exception{
        Random random = new Random();
        List<String> uploadedVideos = new ArrayList<>();
        // get 2 LC videos
        for (int iteration = 0; iteration < 2; iteration++){
            // get random category
            List<String> topics = initialParamsConfig.getTopics();
            String topic = topics.get(random.nextInt(topics.size()));

            // search YT for matches
            String query = "leetcode " + topic;
            List<SearchResult> searchResults = ytClient.searchForVideos(query, 25L);

            // find first video that was NOT already added to playlist
            findAndUploadVideo(searchResults, topic, uploadedVideos);
        }

        String query = "system design walkthrough";
        List<SearchResult> searchResults = ytClient.searchForVideos(query, 25L);

        findAndUploadVideo(searchResults, "system design", uploadedVideos);


        // send email
        List<Video> pendingVideos = videoRepository.findAllById(uploadedVideos);

        StringBuilder body = new StringBuilder("Here are your new videos:\n\n");
        for (Video v : pendingVideos){
            body.append("- ").append(v.getTitle())
                    .append(" (").append(v.getUrl()).append(")\n");
        }
        body.append("Playlist: ").append("https://youtube.com/playlist?list=")
                .append("PLmeMsiT-_DHMUzCdUpFsaNs-bmfwYiVQv");
        body.append("\n Number of videos watched: ").append(videoRepository.findAll().size());

        emailService.sendEmail(
                "Interview Practice Email",
                body.toString(),
                "sr7sidharth@gmail.com"
        );

        System.out.println(String.format("Sent email at %s", LocalDateTime.now()));
    }

    @Scheduled(cron = "0 0 22 * * 1-5")
    public void clearPlaylist() throws Exception{
        ytClient.clearLCPlaylist();
        System.out.println(String.format("Cleared LP playlist at %s", LocalDateTime.now()));
    }

    private void findAndUploadVideo(List<SearchResult> searchResults,
                                    String topic, List<String> uploadedVideos) throws IOException {
        for (SearchResult searchResult : searchResults){
            String videoId = searchResult.getId().getVideoId();

            if (videoRepository.existsById(videoId)) continue;

            String channel = searchResult.getSnippet().getChannelTitle();
            if (!initialParamsConfig.getTrustedSources().contains(channel)) continue;

            // AI verification
            if (!mistralService.isViableVideo(
                    searchResult.getSnippet().getTitle(),
                    searchResult.getSnippet().getDescription()
            )) continue;

            StringBuilder msg = new StringBuilder();
            msg.append(searchResult.getSnippet().getTitle()).append(" - ")
                    .append(searchResult.getSnippet().getChannelTitle());
            System.out.println(msg);

            Video video = Video.builder()
                    .videoId(videoId)
                    .title(searchResult.getSnippet().getTitle())
                    .topic(topic)
                    .url("https://youtube.com/watch?v=" + videoId)
                    .createdAt(LocalDateTime.now())
                    .creator(searchResult.getSnippet().getChannelTitle())
                    .build();

            // add to playlist
            ytClient.addVideoToLCPlaylist(videoId);

            // add to db
            videoRepository.save(video);

            uploadedVideos.add(videoId);
            break;
        }
    }
}
