package com.acc.local.external.modules.google;

import com.acc.global.exception.google.GoogleErrorCode;
import com.acc.global.exception.google.GoogleException;
import com.acc.global.properties.GoogleProperties;
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

    private final GoogleProperties googleProperties;

    private GoogleCredentials getGoogleCredentials() {
        try{
            InputStream inputStream = new ClassPathResource(googleProperties.getCredentialsPath()).getInputStream();
            return GoogleCredentials.fromStream(inputStream)
                    .createScoped(List.of(SheetsScopes.SPREADSHEETS_READONLY));
        } catch (IOException e) {
            throw new GoogleException(GoogleErrorCode.CREDENTIALS_LOAD_FAILURE, e);
        }
    }

    public Sheets getSheetsService() {
        try {
            GoogleCredentials credentials = getGoogleCredentials();
            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName("AoldaCloudSheets").build();
        } catch (IOException | GeneralSecurityException e) {
            throw new GoogleException(GoogleErrorCode.SHEETS_SERVICE_INIT_FAILURE, e);
        }
    }
}
