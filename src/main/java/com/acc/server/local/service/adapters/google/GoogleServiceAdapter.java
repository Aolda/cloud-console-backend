package com.acc.server.local.service.adapters.google;

import com.acc.server.global.properties.GoogleSheetProperties;
import com.acc.server.local.dto.google.GoogleFormRequest;
import com.acc.server.local.service.modules.google.GoogleSheetReadModule;
import com.acc.server.local.service.ports.GoogleServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GoogleServiceAdapter implements GoogleServicePort {

    private final GoogleSheetProperties sheetProperties;
    private final GoogleSheetReadModule googleSheetReadModule;

    @Override
    public List<List<Object>> readSheetData() {
        return googleSheetReadModule.read(sheetProperties.getId(), sheetProperties.getRange());
    }

    @Override
    public void receiveFormSubmission(GoogleFormRequest request) {
        //TODO: Discord Webhook 으로 전송
    }
}
