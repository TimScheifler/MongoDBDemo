package com.scheifle.mongodb.mongo;

import com.mongodb.Block;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Accumulators.first;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.unset;

public class MongoAPI implements IMongoAPI {

    private final MongoCollection<Document> user_collection;
    private final MongoCollection<Document> article_collection;
    private final ArrayList<Document> usersWithMostArticles = new ArrayList<>();

    public MongoAPI(MongoDatabase mongoDatabase) {
        this.user_collection = mongoDatabase.getCollection("users");
        this.article_collection = mongoDatabase.getCollection("articles");
    }

    @Override
    public Document writePosts(String[] posts) {
        return new Document("ts", posts[0])
                .append("post_id", posts[1])
                .append("user_id", posts[2])
                .append("post", posts[3])
                .append("user", posts[4]);
    }

    @Override
    public Document writeComments(String[] comments) {
        Document document = new Document("ts", comments[0])
                .append("comment_id", comments[1])
                .append("user_id", comments[2])
                .append("comment", comments[3])
                .append("user", comments[4]);
        if (comments[5].isBlank()) {
            trimStringAtLength(comments[6], 255);
            document.append("post_commented", comments[6]);
        } else {
            trimStringAtLength(comments[5], 255);
            document.append("comment_replied", comments[5]);
        }

        return document;
    }

    @Override
    public void printArticlesOfOneUser(String user_id) {
        Document document = user_collection
                .find(eq("_id", user_id)).first()
                .append("articles", article_collection.find(eq("user_id", user_id)));
        System.out.println(document.toJson());
    }

    @Override
    public void printNumberArticlesOfOneUser(String user_id) {
        Document document = article_collection.aggregate(
                Arrays.asList(
                        match(eq("user_id", user_id)),
                        group("$user_id", sum("articles", 1)))
        ).first();
        assert document != null;
        System.out.println(document.toJson());
    }

    @Override
    public void printNumberArticlesOfAllUsers() {
        article_collection.aggregate(
                List.of(
                        group("$user_id", sum("articles", 1)))
        ).forEach((Block<? super Document>) doc -> System.out.println(doc.toJson()));
    }

    //sehr aufwändig. Hier ist die Menge der Daten das ausschlaggebende Problem. Das Speichern in einem String +
    //dessen Ausgabe kostet sehr viel Zeit (ohne Ausgabe: 4 Sekunden, mit mehrere Minuten)
    @Override
    public void printArticlesOfAllUsers() {
        int counter = 0;
        for (Document value : user_collection.find()) {
            counter++;
            System.out.println(counter);
            System.out.println(value.toJson() + " : " + value.get("_id"));
            printArticlesOfOneUser(value.getString("_id"));
        }
    }

    @Override
    public ArrayList<Document> getUsersWithMostArticles() {
        article_collection.aggregate(
                List.of(
                        group("$user_id", sum("articles", 1)))
        ).forEach((Block<? super Document>) this::getMostArticles
        );
        return usersWithMostArticles;
    }

    public void removeAllUsersFromArticles() {

        AggregateIterable<Document> documents = article_collection.aggregate(List.of(
                group("$user_id",
                        first("user", "$user"))
        ));


        Iterator<Document> iterator = documents.iterator();
        List<Document> documentList = new ArrayList<>();
        int counter = 0;

        while (iterator.hasNext()) {
            Document document = iterator.next();
            documentList.add(document);
            System.out.println(counter + " : " + document.toJson());
            counter++;
        }
        user_collection.insertMany(documentList);
        removeUsers();
    }

    private void removeUsers() {
        article_collection.updateMany(ne("_id", ""), unset("user"));
    }

    private int current_max = 0;
    private void getMostArticles(Document current_document) {
        int article_size = Integer.parseInt(current_document.get("articles").toString());
        if (article_size > current_max) {
            current_max = article_size;
            usersWithMostArticles.clear();
            usersWithMostArticles.add(current_document);
        } else if (article_size == current_max) {
            usersWithMostArticles.add(current_document);
        }
    }

    private void trimStringAtLength(String s, int maxLength) {
        s.substring(0, Math.min(s.length(), maxLength));
    }
}