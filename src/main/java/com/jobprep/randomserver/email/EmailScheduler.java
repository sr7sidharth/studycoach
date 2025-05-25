package com.jobprep.randomserver.email;

import com.jobprep.randomserver.model.Video;
import com.jobprep.randomserver.repository.VideoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailScheduler {
    private final EmailService emailService;
    private final VideoRepository videoRepository;

    public EmailScheduler(EmailService emailService, VideoRepository videoRepository) {
        this.emailService = emailService;
        this.videoRepository = videoRepository;
    }

    //@Scheduled(cron = "0 0 10-21/3 * * *")
    @Scheduled(cron = "0 * * * * *")
    public void sendReminder(){
        List<Video> pendingVideos = videoRepository.findByCompletedFalse();

        StringBuilder body = new StringBuilder("Here are your unwatched videos:\n\n");
        for (Video v : pendingVideos){
            body.append("- ").append(v.getTitle())
                    .append(" (").append(v.getUrl()).append(")\n");
        }

        emailService.sendEmail(
                "Reminder Email",
                body.toString(),
                "sr7sidharth@gmail.com"
        );
    }
}
