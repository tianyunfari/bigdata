package com.java;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        MongoClient mongoClient = new MongoClient("bigdata3", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("test");

        MongoCollection<Document> collection = mongoDatabase.getCollection("student");

        Document document = new Document();

        FindIterable<Document> list = collection.find();

        for (Document doc : list) {
            System.out.println(doc);
        }

        System.out.println( "Hello World!" );
    }
}
