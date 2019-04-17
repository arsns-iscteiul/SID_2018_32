package mongo;


//Importações para Servidor
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;


public class InsertIntoMongo {
	
	public static void main(String[] args) {
		   try {
			   
			   MongoClient mongoClient = new MongoClient( );
			   mongoClient.getDatabaseNames().forEach(System.out::println);
			   DB db = mongoClient.getDB("sensores");
				DBCollection collection = db.getCollection("sensor");

	    MqttClient client;
	    MqttConnectOptions conn;

	    try {
	        client = new MqttClient("wss://iot.eclipse.org:443/ws", "user");
	        client.connect();
	        client.setCallback(new MqttCallback() {
	            public void connectionLost(Throwable cause) {}

	            public void messageArrived(String topic,
	                    MqttMessage message)
	                            throws Exception {
	                System.out.println(message.toString());
	                DBObject dbObject = (DBObject)JSON.parse(message.toString());
	    			
	            	collection.insert(dbObject);

	            }

	            public void deliveryComplete(IMqttDeliveryToken token) {}
	        });

	        client.subscribe("iscte_sid_2016_S1");

	    } catch (MqttException e) {
	        e.printStackTrace();
	    }
		  } catch (MongoException e) {
				e.printStackTrace();
		}
	    
	}

}
