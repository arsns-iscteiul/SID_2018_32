package mongo;

import java.util.Queue;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoInsertThread extends Thread {

	private MongoDatabase db;
	private MongoCollection<Document> collection;
	private InsertToMongoTesteAB1 mqttClient;

	public MongoInsertThread(InsertToMongoTesteAB1 a) {
		mqttClient = a;
		MongoClient mongoClient = new MongoClient();

		MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
		while (dbsCursor.hasNext()) {
			System.out.println(dbsCursor.next());
		}

		db = mongoClient.getDatabase("sensores");
		collection = db.getCollection("sensor");
	}

	public void run() {
		while (true) {
			Queue<String> s = mqttClient.pollMessage();
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
					Object[] arrayOfS = (Object[]) s.toArray();
					for (int i = 0; i != arrayOfS.length; i++) {
						collection.insertOne(new Document("Mensagem" + collection.countDocuments(), arrayOfS[i])); // Discutir**
						System.out.println(collection.countDocuments());

					}

					// Foi possivel inserir por isso vamos remover da outra lista
					mqttClient.jobDone(s);

				} catch (MongoException e) { // não for possivel inserir
					e.printStackTrace();
				}
			}

		}

	}
}
