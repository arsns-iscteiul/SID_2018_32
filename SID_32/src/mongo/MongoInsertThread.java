package mongo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;

public class MongoInsertThread extends Thread{
	
	private DB db;
	private DBCollection collection;
	private SensorReader mqttClient;
	Conf cfg;
	
	public MongoInsertThread (SensorReader a){
		mqttClient = a;
		cfg = a.getCfg();
		MongoClient mongoClient = new MongoClient(
				new MongoClientURI("mongodb+srv://"+cfg.getProperty("user_mongo")+":"+cfg.getProperty("pass_mongo")+cfg.getProperty("cluster_mongo")));

		db = mongoClient.getDB("sensores");
		collection = db.getCollection("sensor");
	}
	
	public void run (){	
		while (true){
			String s = mqttClient.pollMessage();
			System.out.println(" vou guardar no mongo ");
			if(s == null || s.isEmpty() || !verificarFormatoJSON(s))
				continue;
				
			DBObject dbObject = (DBObject)JSON.parse(s);
			collection.insert(dbObject);
		}
	}
	
	public boolean verificarFormatoJSON(String s) {
		try {
			DBObject dbObject = (DBObject)JSON.parse(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;

	}
}


//new MongoClientURI("mongodb://localhost:27017,localhost:25017,localhost:23017/?replicaSet=replicas"));

//db1= mongoClient.getDatabase("sensores");
//collection = db1.getCollection("sensor");