package com.acc.server.local.service.modules.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoogleSheetReadModule {

    private final GoogleCredentialsModule credentialsLoader;

    public List<List<Object>> read(String id, String range) {
        try{
            Sheets service = credentialsLoader.getSheetsService();

            ValueRange response = service.spreadsheets().values()
                    .get(id, range)
                    .execute();

            return response.getValues() != null ? response.getValues() : List.of();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read Google Sheet data", e);
        }
    }
}
