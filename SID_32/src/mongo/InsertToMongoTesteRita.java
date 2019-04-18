package mongo;

import org.bson.BsonArray;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;

public class InsertToMongoTesteRita implements MqttCallback{
	MqttClient client;
	DB db;
	DBCollection collection;
//	BasicDBList dbList;
	public InsertToMongoTesteRita() {
		MongoClient mongoClient = new MongoClient();
		
		MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
		while(dbsCursor.hasNext()) {
		    System.out.println(dbsCursor.next());
		}
		
		db = mongoClient.getDB("sensores");
		collection = db.getCollection("sensor");
	//	dbList = new BasicDBList();
	}
	
	
	public static void main(String[] args) {
		
		new InsertToMongoTesteRita().testeMqtt();
		
		
	}
	
	public void testeMqtt(){
		try {
			client = new MqttClient("wss://iot.eclipse.org:443/ws", "user321");
			
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);
			client.connect(options);
			
		    client.setCallback(this);
		    client.subscribe("iscte_sid_2016_S1");
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void connectionLost(Throwable caus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
				 saveMessage(message);
				 System.out.println(message); 

		 
		
	}
	
	public void saveMessage (MqttMessage msg){
		//nova tentativa	
		
		System.out.println(msg.toString() + "   oi");
//		BsonArray parse = BsonArray.parse(msg.toString());
//		System.out.println( "       estou aqui");
//		BasicDBList dbList =new BasicDBList();
//		dbList.addAll(parse);
//		DBObject dbObject = dbList;
//		System.out.println("cheguei aqui?");
		
		
		//antigo
		
	//DBObject dbObject = (DBObject)JSON.parse(msg.toString());
	System.out.println("cheguei aquiiiiiiiiiiiiiiiiiii");
	//	collection.insert(dbObject);
//		DBCursor cursorDocJSON = collection.find();
//		while (cursorDocJSON.hasNext()) {
//			System.out.println(cursorDocJSON.next());
//		}

	}
	public boolean checkMsgFormat (String msg) {

		return msg.matches("\\{(\"tmp\"(\\s+)?:(\\s+)?\\d+.\\d+,(\\s+)?)"
				+ "\"hum\"(\\s+)?:(\\s+)?\\d+.\\d+,(\\s+)?"
				+ "\"dat\"(\\s+)?:(\\s+)?\"\\d+-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])\",(\\s+)?"
				+ "\"tim\"(\\s+)?:(\\s+)?\"(0?[0-9]|1[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])\",(\\s+)?"
				+ "\"cell\"(\\s+)?:(\\s+)?\\d+,(\\s+)?"
				+ "\"sens\"(\\s+)?:(\\s+)?\"\\w+\",(\\s+)?"
				+ "\"exported\"(\\s+)?:(\\s+)?[01](\\s+)?\\}");
			
				
	}
}
