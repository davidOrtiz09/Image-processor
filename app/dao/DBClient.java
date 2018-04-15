package dao;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DBClient {

    private static MongoDatabase singleton = new MongoClient().getDatabase("local");

    public static MongoDatabase getConnection( ) {
        return singleton;
    }
}
