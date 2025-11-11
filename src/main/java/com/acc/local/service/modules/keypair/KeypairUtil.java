package com.acc.local.service.modules.keypair;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class KeypairUtil {

    private static final String KEYPAIR_NAME_REGEX = "^[a-zA-Z0-9@._-]{1,255}$";
    private static final Pattern KEYPAIR_NAME_PATTERN = Pattern.compile(KEYPAIR_NAME_REGEX);

    public boolean validateKeypairName(String keypairName) {
        if (keypairName == null || keypairName.trim().isEmpty()) {
            return false;
        }
        return KEYPAIR_NAME_PATTERN.matcher(keypairName).matches();
    }
}
