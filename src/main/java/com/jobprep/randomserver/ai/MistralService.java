package com.jobprep.randomserver.ai;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class MistralService {
    private final OllamaChatModel chatClient;

    public MistralService(OllamaChatModel chatClient) {
        this.chatClient = chatClient;
    }

    public boolean isViableVideo(String title, String description) {
        String prompt = """
                You are a Leetcode and system design expert.
                You are given the following YouTube video title and description.
                If these attributes indicate either a solution to a Leetcode problem, or a discussion of a system design
                interview/walkthrough, then reply with "True".
                Your answer must be either "True" or "False".

                Title: "%s"
                
                Description: "%s"

                Answer:
                """.formatted(title, description);
        String result = chatClient.call(prompt);
        System.out.println(result);
        return result.contains("True");
    }
}
