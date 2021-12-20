package com.scheifle.mongodb.models;

public class Post {

    private String ts;
    private long post_id;
    private long user_id;
    private String post;

    public Post(String ts, long post_id, long user_id, String post, String user) {
        this.ts = ts;
        this.post_id = post_id;
        this.user_id = user_id;
        this.post = post;
    }

    @Override
    public String toString() {
        return "Post{" +
                "ts='" + ts + '\'' +
                ", post_id=" + post_id +
                ", user_id=" + user_id +
                ", post='" + post + '\'' +
                '}';
    }
}
