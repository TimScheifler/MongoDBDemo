package com.scheifle.mongodb.models;

public class Comment {

    private String timestamp;

    private long comment_id;

    private String comment;

    private String user_id;

    private long post_commented;

    private long comment_replied;

    public Comment(String timestamp, long comment_id, String comment, String user_id, String user, long post_commented, long comment_replied) {
        this.timestamp = timestamp;
        this.comment_id = comment_id;
        this.comment = comment;
        this.user_id = user_id;
        this.post_commented = post_commented;
        this.comment_replied = comment_replied;
    }
    @Override
    public String toString() {
        return "Comment{" +
                "timestamp='" + timestamp + '\'' +
                ", comment_id=" + comment_id +
                ", comment='" + comment + '\'' +
                ", user_id='" + user_id + '\'' +
                ", post_commented='" + post_commented + '\'' +
                ", comment_replied='" + comment_replied + '\'' +
                '}';
    }
}
