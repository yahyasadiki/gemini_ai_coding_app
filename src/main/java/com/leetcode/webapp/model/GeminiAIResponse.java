package com.leetcode.webapp.model;

public class GeminiAIResponse {

    private String correctedCode;
    private String explanation;
    private int rating;
    private String feedback;

    public GeminiAIResponse() {
    }

    public GeminiAIResponse(String correctedCode, String explanation, int rating, String feedback) {
        this.correctedCode = correctedCode;
        this.explanation = explanation;
        this.rating = rating;
        this.feedback = feedback;
    }

    public String getCorrectedCode() {
        return correctedCode;
    }

    public void setCorrectedCode(String correctedCode) {
        this.correctedCode = correctedCode;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}