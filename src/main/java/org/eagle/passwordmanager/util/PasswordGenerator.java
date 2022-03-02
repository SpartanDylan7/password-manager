package org.eagle.passwordmanager.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public final class PasswordGenerator {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String PUNCTUATION = "!@#$%&*()_+-=[]|,./?><";
    private static final PasswordGenerator INSTANCE = new PasswordGenerator();
//    public boolean useDigits;
//    public boolean usePunctuation;

    private PasswordGenerator() {

    }

    public static PasswordGenerator getInstance(){
        return INSTANCE;
    }



    public String generate(int length, boolean usePunctuation, boolean useDigits) {
        if (length < 4) {
            length = 4;
        } else {
            if (length > 30) {
                length = 30;
            }
        }

        StringBuilder password = new StringBuilder(length);
        SecureRandom secureRandom = new SecureRandom();
        List<String> charCategories = new ArrayList<>(4);

            charCategories.add(LOWER);
            charCategories.add(UPPER);

        if (useDigits) {
            charCategories.add(DIGITS);
        }
        if (usePunctuation) {
            charCategories.add(PUNCTUATION);
        }
        for (int i = 0; i < length; i++) {
            String charCategory = charCategories.get(secureRandom.nextInt(charCategories.size()));
            int position = secureRandom.nextInt(charCategory.length());
            password.append(charCategory.charAt(position));
        }
        return new String(password);
    }
}
