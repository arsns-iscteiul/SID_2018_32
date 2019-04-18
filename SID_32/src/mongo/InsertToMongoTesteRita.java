package mongo;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;

public class InsertToMongoTesteRita implements MqttCallback{
	MqttClient client;
	
	public InsertToMongoTesteRita() {
		MongoClient mongoClient = new MongoClient();
		
		MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
		while(dbsCursor.hasNext()) {
		    System.out.println(dbsCursor.next());
		}
		
		DB db = mongoClient.getDB("sensores");
		DBCollection collection = db.getCollection("sensor");
	}
	
	
	public static void main(String[] args) {
		
		new InsertToMongoTesteRita().testeMqtt();
		
		
	}
	
	public void testeMqtt(){
		try {
			client = new MqttClient("wss://iot.eclipse.org:443/ws", "user321");
			
			client.connect();
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
		 System.out.println(message);  
		
	}
}
