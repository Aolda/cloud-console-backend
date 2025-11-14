package com.acc.local.external.modules.google;

import com.acc.global.exception.google.GoogleErrorCode;
import com.acc.global.exception.google.GoogleException;
import com.acc.global.properties.GoogleProperties;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleCredentialsModule {

    private final GoogleProperties googleProperties;
    private final OAuth2AuthorizedClientService authorizedClientService;

    private GoogleCredentials getGoogleRobotCredentials() {
        try{
            InputStream inputStream = new ClassPathResource(googleProperties.getCredentialsPath()).getInputStream();
            return GoogleCredentials.fromStream(inputStream)
                    .createScoped(List.of(SheetsScopes.SPREADSHEETS_READONLY));
        } catch (IOException e) {
            throw new GoogleException(GoogleErrorCode.CREDENTIALS_LOAD_FAILURE, e);
        }
    }

    private GoogleCredentials getGoogleUserCredentials(OAuth2User oAuth2User) {
        String principalName = oAuth2User.getName();
        OAuth2AuthorizedClient authClient = getOAuth2AuthorizedClient(principalName);

        String tokenValue = authClient.getAccessToken().getTokenValue();
        Instant expiresAt = authClient.getAccessToken().getExpiresAt();
        Date expirationTime = Optional.ofNullable(expiresAt).map(Date::from).orElse(null);

        AccessToken accessToken = new AccessToken(tokenValue, expirationTime);
        return GoogleCredentials.create(accessToken);
    }

    private OAuth2AuthorizedClient getOAuth2AuthorizedClient(String principalName) {
        OAuth2AuthorizedClient authClient = authorizedClientService.loadAuthorizedClient("google", principalName);
        if (authClient == null) {
            throw new GoogleException(GoogleErrorCode.CREDENTIALS_LOAD_FAILURE);
        }

        return authClient;
    }

    public Sheets getSheetsService() {
        try {
            GoogleCredentials credentials = getGoogleRobotCredentials();
            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName(googleProperties.getServiceName()).build();
        } catch (IOException | GeneralSecurityException e) {
            throw new GoogleException(GoogleErrorCode.SHEETS_SERVICE_INIT_FAILURE, e);
        }
    }

    protected PeopleService getPeopleService(OAuth2User oAuth2User) {
        try {
            GoogleCredentials credentials = getGoogleUserCredentials(oAuth2User);
            return new PeopleService.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
            ).setApplicationName(googleProperties.getServiceName()).build();
        } catch (Exception e) {
            throw new GoogleException(GoogleErrorCode.SHEETS_SERVICE_INIT_FAILURE, e);
        }
    }
}
