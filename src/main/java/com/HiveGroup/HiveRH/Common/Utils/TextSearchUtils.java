package com.HiveGroup.HiveRH.Common.Utils;

import java.util.regex.Pattern;

public class TextSearchUtils {
    public static boolean matchesFullName(String name, String lastName, String fullNameFilter) {
        if (fullNameFilter == null || fullNameFilter.isBlank()) {
            return true;
        }

        String fullName = name + " " + lastName;
        String invertedFullName = lastName + " " + name;
        String regex = buildFullNameRegex(fullNameFilter);
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        return pattern.matcher(fullName).find() || pattern.matcher(invertedFullName).find();
    }

    private static String buildFullNameRegex(String fullNameFilter) {
        String[] terms = fullNameFilter.trim().split("\\s+");
        StringBuilder regex = new StringBuilder();

        for (String term : terms) {
            if (!regex.isEmpty()) {
                regex.append(".*");
            }
            regex.append(Pattern.quote(term));
        }

        return regex.toString();
    }
}
