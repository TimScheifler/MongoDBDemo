package com.scheifle.mongodb;

import com.mongodb.client.*;
import com.scheifle.mongodb.mongo.IMongoAPI;

import org.bson.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {

    private final IMongoAPI mongoAPI;
    private final MongoCollection<org.bson.Document> article_collection;

    public FileProcessor(final MongoDatabase mongoDatabase, final IMongoAPI mongoAPI){
        this.mongoAPI = mongoAPI;

        this.article_collection = mongoDatabase.getCollection("articles");
    }

    public void processFile(final String path, final FileType fileType) throws Exception {
        if(fileType.equals(FileType.POST))
            processPost(path);
        else if(fileType.equals(FileType.COMMENT))
            processComment(path);
        else
            throw new Exception("Unknown FileType");
    }

    private void processPost(final String path) throws IOException {
        File file = new File(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);

        List<Document> postDocuments = new ArrayList<Document>();
        String line;

        int count = 0;
        final int max = 50000;
        while((line = br.readLine())!=null){
            count++;
            String[] splitLine = splitLine(line);
            Document postDocument = mongoAPI.writePosts(splitLine);
            postDocuments.add(postDocument);
            if(count > max){
                count = 0;
                article_collection.insertMany(postDocuments);
                postDocuments.clear();
            }
        }

        fileReader.close();
        article_collection.insertMany(postDocuments);
    }

    private void processComment(final String path) throws IOException {
        File file = new File(path);
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);

        List<Document> commentDocuments = new ArrayList<>();

        String line;

        int count = 0;
        final int max = 50000;
        while((line = br.readLine())!=null){
            count++;
            String[] splitLine = splitLine(line);
            Document commentDocument = mongoAPI.writeComments(splitLine);
            commentDocuments.add(commentDocument);
            if(count > max){
                count = 0;
                article_collection.insertMany(commentDocuments);
                commentDocuments.clear();
            }
        }
        fileReader.close();
        article_collection.insertMany(commentDocuments);
    }

    private String[] splitLine(final String line) {
        return line.split("\\|");
    }

}