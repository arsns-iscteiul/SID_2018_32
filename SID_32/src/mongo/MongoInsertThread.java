package mongo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;

public class MongoInsertThread extends Thread {

	private DB db;
	private DBCollection collection;
	private InsertToMongoTesteAB1 mqttClient;

	public MongoInsertThread(InsertToMongoTesteAB1 a) {
		mqttClient = a;
		MongoClient mongoClient = new MongoClient();

		MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
		while (dbsCursor.hasNext()) {
			System.out.println(dbsCursor.next());
		}

		db = mongoClient.getDB("sensores");
		collection = db.getCollection("sensor");
	}

	public void run() {
		while (true) {
			LinkedBlockingQueue<String> s = (LinkedBlockingQueue<String>) mqttClient.pollMessage();
			System.out.println(s);
			if (s == null) {
				System.out.println("queue is empty, imma gonna sleep");
				try {
					sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
//					while(!s.isEmpty()) {
//						DBObject dbObject = (DBObject) JSON.parse((String) s.poll());
//						collection.insert(dbObject);
//					}
					// or
					DBObject[] dbObjects = (DBObject[]) s.toArray()[s.size()];
					collection.insert(dbObjects);

					// Foi possivel inserir por isso vamos remover da outra lista
					 mqttClient.jobDone(s);
					 
				} catch (MongoException e) { // não for possivel inserir
					e.printStackTrace();
				}
			}

		}

	}
}
