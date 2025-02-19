package com.leetcode.webapp.model;

import com.opencsv.bean.CsvBindByName;

public class LeetCodeQuestion {

    @CsvBindByName
    private Long id;

    @CsvBindByName
    private String title;

    @CsvBindByName
    private String description;

    @CsvBindByName
    private int is_premium;

    @CsvBindByName
    private String difficulty;

    @CsvBindByName(column = "solution_link")
    private String solutionLink; // Use solutionLink instead of url

    @CsvBindByName(column = "acceptance_rate")
    private String acceptanceRate;

    @CsvBindByName
    private String frequency;

    @CsvBindByName
    private String url;

    @CsvBindByName(column = "discuss_count")
    private String discussCount;

    @CsvBindByName
    private String accepted;

    @CsvBindByName
    private String submissions;

    @CsvBindByName
    private String companies;

    @CsvBindByName(column = "related_topics")
    private String topics; // Use topics instead of relatedTopics

    @CsvBindByName
    private String likes;

    @CsvBindByName
    private String dislikes;

    @CsvBindByName
    private String rating;

    @CsvBindByName(column = "asked_by_faang")
    private String askedByFaang;

    @CsvBindByName(column = "similar_questions")
    private String similarQuestions;

    // Getters and setters for all fields

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIs_premium() {
        return is_premium;
    }

    public void setIs_premium(int is_premium) {
        this.is_premium = is_premium;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getSolutionLink() {
        return solutionLink;
    }

    public void setSolutionLink(String solutionLink) {
        this.solutionLink = solutionLink;
    }

    public String getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(String acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDiscussCount() {
        return discussCount;
    }

    public void setDiscussCount(String discussCount) {
        this.discussCount = discussCount;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getSubmissions() {
        return submissions;
    }

    public void setSubmissions(String submissions) {
        this.submissions = submissions;
    }

    public String getCompanies() {
        return companies;
    }

    public void setCompanies(String companies) {
        this.companies = companies;
    }

    public String getTopics() {
        return topics;
    }

    public void setTopics(String topics) {
        this.topics = topics;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getDislikes() {
        return dislikes;
    }

    public void setDislikes(String dislikes) {
        this.dislikes = dislikes;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAskedByFaang() {
        return askedByFaang;
    }

    public void setAskedByFaang(String askedByFaang) {
        this.askedByFaang = askedByFaang;
    }

    public String getSimilarQuestions() {
        return similarQuestions;
    }

    public void setSimilarQuestions(String similarQuestions) {
        this.similarQuestions = similarQuestions;
    }
}