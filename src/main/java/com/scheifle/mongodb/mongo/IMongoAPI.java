package com.scheifle.mongodb.mongo;

import org.bson.Document;

import java.util.ArrayList;

public interface IMongoAPI {

    Document writePosts(String[] posts);
    Document writeComments(String[] comments);

    void printArticlesOfOneUser(String user_id);
    void printNumberArticlesOfOneUser(String user_id);

    void printArticlesOfAllUsers();
    void printNumberArticlesOfAllUsers();

    ArrayList<Document> getUsersWithMostArticles();

    void removeAllUsersFromArticles();
}
