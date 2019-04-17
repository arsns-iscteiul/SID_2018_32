package mongo;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
public class Paho {
	
	public static void main(String[] args) {
        String topic        = "iscte_sid_2016_S1";
        double temp =21.00;
        double hum =64.80;
        int cell =3138;
        String sensor ="wifi";
        String data = String.valueOf(LocalDate.now());
        String tim = String.valueOf(LocalDateTime.now());
        String content      = "{tmp:" + String.valueOf(temp) + ",hum:" + String.valueOf(hum) +",dat:" + data +",tim:"+ tim + 
        		",cell:" + String.valueOf(cell) + ",sens:" + sensor;
        int qos             = 0;
        String broker       = "wss://iot.eclipse.org:443/ws";
        String clientId     = "user";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");
            sampleClient.disconnect();
            System.out.println("Disconnected");
            System.exit(0);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }

}
