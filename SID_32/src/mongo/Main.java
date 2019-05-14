package mongo;

public class Main {

	public static void main(String[] args) {
//		InsertToMongo insert = new InsertToMongo();
//		insert.testeMqtt();
		//new InsertToMongo().testeMqtt();               <<<<<<<<<<<< utilizar este 
		ExportingThread export = new ExportingThread();
		export.start();  
	}   
	  
}