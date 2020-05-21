package xyz.mugai.redditfetcher.database;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import xyz.mugai.redditfetcher.configuration.Config;

public class Database {

    private static MongoCollection cats;

    public Database() {
        MongoClient mongoClient = MongoClients.create(Config.get("mongo_url"));
        MongoDatabase database = mongoClient.getDatabase("mugaiapi");

        cats = database.getCollection(Config.get("MONGO_TABLE"));

    }

    public static boolean insertCat(String imgurl, String title, String author, String posturl){
        if(exists(posturl)) return false;
        Document doc = new Document();
        doc.append("PostURL", posturl);
        doc.append("Title", title);
        doc.append("Author", author);
        doc.append("ImgURL", imgurl);
        return cats.insertOne(doc).wasAcknowledged();
    }

    private static boolean exists(String posturl){
        MongoCursor cursor = cats.find(Filters.eq("PostURL", posturl)).cursor();
        return cursor.hasNext();
    }
}
