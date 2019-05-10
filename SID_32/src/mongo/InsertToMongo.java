package mongo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

public class InsertToMongo implements MqttCallback{
	private MqttClient client;
//	private DB db;
//	private DBCollection collection;
	
	//ArrayList<SensorInfo> sens =new ArrayList<>();
	private ArrayList<String> sens =new ArrayList<>();
	private BlockingQueue<String> msgs;
	
//	BasicDBList dbList;
	public InsertToMongo() {
		msgs = new LinkedBlockingQueue<String>();
		MongoInsertThread t = new MongoInsertThread(this);
		t.start();
	}
	
	
	
	public void testeMqtt(){
		try {
			client = new MqttClient("wss://iot.eclipse.org:443/ws", "user321");
			//client = new MqttClient("tcp://broker.mqtt-dashboard.com:1883","/sid_lab_2019");
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);
			client.connect(options);
			
		    client.setCallback(this);
		    client.subscribe("/sid_lab_2019");
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
			String s = message.toString();
			System.out.println(s+" -mensagem recebida2");
			System.out.println(checkMsgFormat(s));
			if (checkMsgFormat(s)){
					s=s.replace("}", ",\"exported\":0}");
					s=s.replace("\"sens\"", ",\"sens\"");
				//	s.replaceAll("\\}", ",\"exported\":0}");		
					System.out.println(s + " formato checkado, esta e a nova msg");
					saveMessage(s);
				}		
	}
	
	
	public synchronized void saveMessage (String msg){
		msgs.offer(msg);
		notifyAll();
			//nova		
		System.out.println( "   - mensagem guardada na queue");
//		BsonArray parse = BsonArray.parse(msg.toString());
//		System.out.println( "       estou aqui");
//		BasicDBList dbList =new BasicDBList();
//		dbList.addAll(parse);
//		DBObject dbObject = dbList;
//		System.out.println("cheguei aqui?");
		
		
		//antigo
		
	//DBObject dbObject = (DBObject)JSON.parse(msg.toString());
	//	collection.insert(dbObject);
//		DBCursor cursorDocJSON = collection.find();
//		while (cursorDocJSON.hasNext()) {
//			System.out.println(cursorDocJSON.next());
//		}
//	String json = "{'tmp' : 21.0, 'hum' : 64.8, 'dat' : '2019-04-18' , 'tim' : '19:40:02',"
//			+ "'cell' : 3138, 'sens' : 'wifi', 'foiExportado' : 0}";

	}
	
	public synchronized String pollMessage() {
		if (msgs.isEmpty()){
			try {
				System.out.println("não há mensagens, esperando");
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return msgs.poll();
	}
	
	public boolean checkMsgFormat (String msg) {
		return msg.matches("\\{(\"tmp\"(\\s+)?:(\\s+)?\"\\d+.\\d+\",(\\s+)?)"
				+ "\"hum\"(\\s+)?:(\\s+)?\"\\d+.\\d+\",(\\s+)?"
				+ "\"dat\"(\\s+)?:(\\s+)?\"(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/\\d+\",(\\s+)?"
				+ "\"tim\"(\\s+)?:(\\s+)?\"(0?[0-9]|1[0-9]|2[0-3]):([0-9]|[1-5][0-9]):([0-9]|[1-5][0-9])\"(\\s+)?"
				+ "(,\"cell\"(\\s+)?:(\\s+)?\"\\d+\"(\\s+)?)?"
				+ "\"sens\"(\\s+)?:(\\s+)?\"\\w+\"(\\s+)?\\}");
			//	+ "\"exported\"(\\s+)?:(\\s+)?[01](\\s+)?\\}");	
				
	}
	

	public static void main(String[] args) {	
		new InsertToMongo().testeMqtt();
	}
	
}
