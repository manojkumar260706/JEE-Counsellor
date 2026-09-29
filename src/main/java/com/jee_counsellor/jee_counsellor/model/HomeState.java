package com.jee_counsellor.jee_counsellor.model;

import java.util.Arrays;
import java.util.List;

public class HomeState {
    public static final List<String> VALID_STATES = List.of(
        "Uttarakhand",
        "Himachal Pradesh",
        "Assam",
        "Andhra Pradesh",
        "Madhya Pradesh",
        "Sikkim",
        "Jammu and Kashmir",
        "Goa",
        "Meghalaya",
        "Dadra and Nagar Haveli and Daman and Diu",
        "Maharashtra",
        "Puducherry",
        "Mizoram",
        "Haryana",
        "Tamil Nadu",
        "Uttar Pradesh",
        "Manipur",
        "Jharkhand",
        "Rajasthan",
        "Gujarat",
        "Karnataka",
        "Kerala",
        "Delhi",
        "West Bengal",
        "Tripura",
        "Chhattisgarh",
        "Odisha",
        "Arunachal Pradesh",
        "Bihar",
        "Punjab",
        "Nagaland",
        "Telangana",
        "Chandigarh"
    );

    public static String normalizeAndValidate(String stateInput) {
        if (stateInput == null || stateInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Home state is required");
        }
        String trimmed = stateInput.trim();
        for (String validState : VALID_STATES) {
            if (validState.equalsIgnoreCase(trimmed)) {
                return validState;
            }
        }
        throw new IllegalArgumentException("Invalid home state: '" + stateInput + "'. Must be one of the 33 designated states/UTs.");
    }
}
