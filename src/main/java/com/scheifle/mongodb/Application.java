package com.scheifle.mongodb;

import ch.qos.logback.classic.Logger;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.scheifle.mongodb.mongo.IMongoAPI;
import com.scheifle.mongodb.mongo.MongoAPI;
import org.bson.Document;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@SpringBootApplication
public class Application {

    private static long startTime = -1;

    public static void main(String[] args) throws Exception {

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        rootLogger.setLevel(Level.OFF);

        MongoClient mongoClient = new MongoClient("localhost", 27017);

        MongoDatabase mongoDatabase = mongoClient.getDatabase("informationssysteme_aufgabe_4");

        IMongoAPI mongoAPI = new MongoAPI(mongoDatabase);
        FileProcessor fileProcessor = new FileProcessor(mongoDatabase, mongoAPI);

        //18 Sekunden
        startTimer();
        System.out.println("IMPORTING POSTS.DAT AND COMMENTS.DAT");
        fileProcessor.processFile("src/main/resources/comments.dat", FileType.COMMENT);
        fileProcessor.processFile("src/main/resources/posts.dat", FileType.POST);
        printTimerInSeconds();

        //28 Sekunden
        startTimer();
        mongoAPI.removeAllUsersFromArticles();
        printTimerInSeconds();

        //0-1 Sekunden
        startTimer();
        System.out.println("Articles of one user");
        mongoAPI.printArticlesOfOneUser("3981");
        printTimerInSeconds();

        //0-1 Sekunden
        startTimer();
        System.out.println("Number articles of one user");
        mongoAPI.printNumberArticlesOfOneUser("3981");
        printTimerInSeconds();

        //2981sec
        startTimer();
        System.out.println("All articles of all users");
        //mongoAPI.printArticlesOfAllUsers();
        printTimerInSeconds();

        //1-2 Sekunden
        startTimer();
        System.out.println("All numbers of Articles of all users");
        mongoAPI.printNumberArticlesOfAllUsers();
        printTimerInSeconds();

        //1 Sekunde
        //Ergebnis stimmt mit Vergleichswerten überein (6609)
        startTimer();
        System.out.println("All users with most articles");
        ArrayList<Document> documents = mongoAPI.getUsersWithMostArticles();
        for (Document document : documents) {
            System.out.println(document.toJson());
        }
        printTimerInSeconds();
    }

    private static void startTimer() {
        startTime = System.currentTimeMillis();
    }

    private static void printTimerInSeconds() {
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Done. Time: " + estimatedTime / 1000 + "sec.");
    }
}
