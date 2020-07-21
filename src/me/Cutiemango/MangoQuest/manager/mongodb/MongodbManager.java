package me.Cutiemango.MangoQuest.manager.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import me.Cutiemango.MangoQuest.ConfigSettings;
import org.bson.Document;

import java.util.stream.StreamSupport;

public class MongodbManager
{
	private static MongoCollection<Document> collection;

	public static MongoCollection<Document> getCollection()
	{
		if(collection != null)
			return collection;

		MongoClient mongoClient = new MongoClient(
				new MongoClientURI(String.format("mongodb://%s:%s@%s:%d/",
												 ConfigSettings.DATABASE_USER, ConfigSettings.DATABASE_PASSWORD,
												 ConfigSettings.DATABASE_ADDRESS, ConfigSettings.DATABASE_PORT)));
		MongoDatabase db = mongoClient.getDatabase(ConfigSettings.DATABASE_NAME);
		boolean hasCollection = StreamSupport.stream(db.listCollectionNames().spliterator(), false).anyMatch(name -> name.equals("MangoQuest.PlayerData"));

		if(!hasCollection)
		{
			db.createCollection("MangoQuest.PlayerData");
			collection = db.getCollection("MangoQuest.PlayerData");

			IndexOptions options = new IndexOptions();
			options.unique(true);

			collection.createIndex(new Document("UUID", 1), options);
		}
		else
		{
			collection = db.getCollection("MangoQuest.PlayerData");
		}

		return collection;
	}

}
