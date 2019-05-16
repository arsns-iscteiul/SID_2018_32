package mongo;

public class Main {

	public static void main(String[] args) {
		new SensorReader().mqttReader();  
		ExportingThread export = new ExportingThread();
		export.start();  
	}   
	  
}