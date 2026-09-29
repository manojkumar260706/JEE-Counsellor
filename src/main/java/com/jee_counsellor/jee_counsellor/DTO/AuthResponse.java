package com.jee_counsellor.jee_counsellor.dto;

public class AuthResponse {

    private boolean success;
    private String message;
    private CandidateResponse candidate;

    public AuthResponse() {
    }

    public AuthResponse(boolean success, String message, CandidateResponse candidate) {
        this.success = success;
        this.message = message;
        this.candidate = candidate;
    }

    public static AuthResponse success(String message, CandidateResponse candidate) {
        return new AuthResponse(true, message, candidate);
    }

    public static AuthResponse failure(String message) {
        return new AuthResponse(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CandidateResponse getCandidate() {
        return candidate;
    }

    public void setCandidate(CandidateResponse candidate) {
        this.candidate = candidate;
    }
}
