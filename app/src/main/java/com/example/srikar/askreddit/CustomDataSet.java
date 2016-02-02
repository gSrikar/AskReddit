package com.example.srikar.askreddit;


public class CustomDataSet {

    public String threadTitle;
    public String threadAuthor;
    public String threadCreated;
    public int threadCommentsNumber;
    public String threadUrl;
    public int threadUps;

    public String getThreadUrl() {
        return threadUrl;
    }

    public void setThreadUrl(String threadUrl) {
        this.threadUrl = threadUrl;
    }

    public String getThreadTitle() {
        return threadTitle;
    }

    public void setThreadTitle(String threadTitle) {
        this.threadTitle = threadTitle;
    }

    public String getThreadAuthor() {
        return threadAuthor;
    }

    public void setThreadAuthor(String threadAuthor) {
        this.threadAuthor = threadAuthor;
    }

    public String getThreadCreated() {
        return threadCreated;
    }

    public void setThreadCreated(String threadCreated) {
        this.threadCreated = threadCreated;
    }

    public int getThreadCommentsNumber() {
        return threadCommentsNumber;
    }

    public void setThreadCommentsNumber(int threadCommentsNumber) {
        this.threadCommentsNumber = threadCommentsNumber;
    }

    public int getThreadUps() {
        return threadUps;
    }

    public void setThreadUps(int threadUps) {
        this.threadUps = threadUps;
    }
}