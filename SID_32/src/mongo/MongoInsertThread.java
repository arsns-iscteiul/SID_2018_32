package mongo;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;

public class MongoInsertThread extends Thread{
	
	private DB db;
	private DBCollection collection;
	private InsertToMongoTesteRita mqttClient;
	
	public MongoInsertThread (InsertToMongoTesteRita a){
		mqttClient = a;
		MongoClient mongoClient = new MongoClient();
		
		MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
		while(dbsCursor.hasNext()) {
		    System.out.println(dbsCursor.next());
		}
		
		db = mongoClient.getDB("sensores");
		collection = db.getCollection("sensor");
	}
	
	public void run (){	
		try {
			while (true){
				System.out.println("oi sou uma thread");
				String s = mqttClient.pollMessage();
				System.out.println(s);
				System.out.println(s.equals(null) + "------------------------------------- " + s);
				if (s.equals(null)){
					System.out.println("queue is empty, imma gonna sleep");
					sleep(10000);
				}else{
					DBObject dbObject = (DBObject)JSON.parse(s);
					collection.insert(dbObject);
				}
		
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
}
