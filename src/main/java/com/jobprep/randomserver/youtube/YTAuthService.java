package com.jobprep.randomserver.youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTubeScopes;
import org.springframework.stereotype.Service;

@Service
public class YTAuthService {
    public Credential loadCredential() throws Exception{
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(),
                new InputStreamReader(getClass().getResourceAsStream("/credentials.json"))
        );

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                clientSecrets,
                List.of(YouTubeScopes.YOUTUBE_FORCE_SSL))
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens"))).build();

        return flow.loadCredential("user");
    }


    /**
     * @return Credential
     * @throws Exception
     * Used *once* to create a tokens StoredCred
     */
    public Credential localAuthorize() throws Exception {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(),
                new InputStreamReader(getClass().getResourceAsStream("/credentials.json"))
        );

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                clientSecrets,
                List.of(YouTubeScopes.YOUTUBE_FORCE_SSL))
                .setAccessType("offline")
                .setApprovalPrompt("force")
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens"))).build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
