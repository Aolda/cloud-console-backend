package com.acc.local.external.modules.google;

import com.acc.global.exception.google.GoogleErrorCode;
import com.acc.global.exception.google.GoogleException;
import com.acc.global.properties.GoogleProperties;
import com.acc.local.external.dto.google.GoogleFormRequest;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GoogleSheetReadModule {

    private final GoogleProperties googleProperties;
    private final GoogleCredentialsModule googleCredentialsModule;

    public List<GoogleFormRequest> read() {
        try{
            ValueRange response = getGoogleSheetValue(googleProperties.getSpreadsheet().getId(), googleProperties.getSpreadsheet().getRange());
            List<List<Object>> values = response.getValues();
            if (values == null || values.isEmpty()) {
                return List.of();
            }

            return values.stream()
                    .map(GoogleFormRequest::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            if (e instanceof GoogleException) {
                throw (GoogleException) e;
            }
            throw new GoogleException(GoogleErrorCode.DATA_PARSING_FAILURE, e);
        }
    }

    private ValueRange getGoogleSheetValue(String spreadsheetId, String range) {
        try {
            Sheets sheetsService = googleCredentialsModule.getSheetsService();
            return sheetsService.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
        } catch (IOException e) {
            throw new GoogleException(GoogleErrorCode.API_READ_FAILURE, e);
        }
    }
}
