package mongo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class SensorReader implements MqttCallback{
	private MqttClient client;
//	private DB db;
//	private DBCollection collection;
	
	//ArrayList<SensorInfo> sens =new ArrayList<>();
	private ArrayList<String> sens =new ArrayList<>();
	private BlockingQueue<String> msgs;
	
//	BasicDBList dbList;
	public SensorReader() {
		msgs = new LinkedBlockingQueue<String>();
		MongoInsertThread t = new MongoInsertThread(this);
		t.start();
	}
	
	
	
	public void mqttReader(){
		try {
		//	client = new MqttClient("wss://iot.eclipse.org:443/ws", "user321");
			client = new MqttClient("tcp://broker.mqtt-dashboard.com:1883","/sid_lab_2019_2");
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);
			client.connect(options);
			
		    client.setCallback(this);
		    client.subscribe("/sid_lab_2019_2");
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
			s=s.replace("\"sens\"", ",\"sens\"");
			String [] arr = s.split(",");
			String msg = "";
			
		
			boolean valida = false;
			for(String a : arr) {
				if (a.contains("tmp")) {
					msg+=a;
					msg+=",";
					valida = true;
				}		
				if(a.contains("dat")) {
					SimpleDateFormat print = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date dataAtual = new Date();
					
					String dataString = print.format(dataAtual);
					System.out.println("a data atual é" + dataString);
					msg += "\"dateTime\":" + "\"" +dataString +"\",";
				}
//				if(a.contains("dat")) {
//					msg+=a;
//					msg+=",";
//				}
//				if(a.contains("tim")) {
//					msg+=a;
//					msg+=",";
//				}
				if(a.contains("cell")) {
					msg+=a;
					msg+=",";
					valida = true;
				}
				if(a.contains("sens")) {
					msg+=a;
				}
				
			}
			System.out.println(msg +"   -----    mensagem recebida");
			//if (checkMsgFormat(msg)){
					msg=msg.replace("}", ",\"exported\":0}");
				//	s.replaceAll("\\}", ",\"exported\":0}");		
					System.out.println(msg + " formato checkado, esta e a nova msg");
					saveMessage(msg);
			//	}		
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
				System.out.println("no messages");
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return msgs.poll();
	}
	
	public boolean checkMsgFormat (String msg) {
		return msg.matches("\\{(\"tmp\"(\\s+)?:(\\s+)?\"\\d+.\\d+\",(\\s+)?)?"
			//	+ "\"hum\"(\\s+)?:(\\s+)?\"\\d+.\\d+\",(\\s+)?"
				+"\"dateTime\"(\\s+)?:(\\s+)?\"(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/\\d+(\\s+)?"
				+ "(0?[0-9]|1[0-9]|2[0-3]):([0-9]|[1-5][0-9]):([0-9]|[1-5][0-9])\"(\\s+)?"
				+ "(,\"cell\"(\\s+)?:(\\s+)?\"\\d+\"(\\s+)?)?"
				+ ",\"sens\"(\\s+)?:(\\s+)?\"\\w+\"(\\s+)?\\}");
			//	+ "\"exported\"(\\s+)?:(\\s+)?[01](\\s+)?\\}");	
				
	}
	


	
}