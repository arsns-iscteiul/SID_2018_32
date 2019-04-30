package mongo;

//Importa��es para Servidor
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

public class Old_InsertIntoMongo {

	public static void main(String[] args) {
		try {

			MongoClient mongoClient = new MongoClient();
			mongoClient.getDatabaseNames().forEach(System.out::println);
			DB db = mongoClient.getDB("sensores");
			DBCollection collection = db.getCollection("sensor");
			
		
			
			MqttClient client;

			try {
				client = new MqttClient("wss://iot.eclipse.org:443/ws", "user123");
				MqttConnectOptions options = new MqttConnectOptions();
				options.setAutomaticReconnect(true);
				options.setCleanSession(true);
				options.setConnectionTimeout(10);
				client.connect(options);
				client.subscribe("iscte_sid_2016_S1", (topic, msg) ->{
					System.out.println("Payload Triggered:\n");
					byte[] payload = msg.getPayload();
					System.out.println(msg.toString());
					//falta garantir que a msg.toString vem no formato abaixo
					String json = "{'tmp' : 21.0, 'hum' : 64.8, 'dat' : '2019-04-17' , 'tim' : '19:40:02',"
							+ "'cell' : 3138, 'sens' : 'wifi', 'foiExportado' : 0}";

					String json2 = "{\"tmp\" : 31.0, \"hum\" : 74.8, \"dat\" : \"2019-04-19\" , \"tim\" : \"19:40:02\","
							+ "\"cell\" : 3138, \"sens\" : \"wifi\", \"exported\" : 0}";
					String tmp = "{\"tmp\":21.0,\"hum\":64.8,\"dat\":\"2019-04-01\",\"tim\":\"10:40:01\",\"cell\":3138,\"sens\":\"wifi\",\"exported\":0}";
					
					DBObject dbObject = (DBObject)JSON.parse(json2);
					collection.insert(dbObject);
					DBCursor cursorDocJSON = collection.find();
					while (cursorDocJSON.hasNext()) {
						System.out.println(cursorDocJSON.next());
					}

						
		
				});
				
				client.setCallback(new MqttCallback() {
					public void connectionLost(Throwable cause) {}

					public void messageArrived(String topic, MqttMessage message) throws Exception {
						System.out.println("Message triggered:\n");
						System.out.println(message.toString());
						

					}

					public void deliveryComplete(IMqttDeliveryToken token) {
					}
				});

			} catch (MqttException e) {
				e.printStackTrace();
			}
		} catch (MongoException e) {
			e.printStackTrace();
		}

	}

}
