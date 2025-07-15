package com.acc.local.service.modules.google;

import com.acc.global.properties.GoogleCredentialsProperties;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleCredentialsModule {

    private final GoogleCredentialsProperties credentialsProperties;

    private GoogleCredentials getGoogleCredentials() throws IOException {
        InputStream inputStream = new ClassPathResource(credentialsProperties.getCredentialsPath()).getInputStream();

        return GoogleCredentials.fromStream(inputStream) // 추후 에러처리 IOException
                .createScoped(List.of(SheetsScopes.SPREADSHEETS_READONLY));
    }

    public Sheets getSheetsService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = getGoogleCredentials();

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName("AoldaCloudSheets").build();
    }
}
