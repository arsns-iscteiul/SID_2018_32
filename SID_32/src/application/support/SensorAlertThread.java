package application.support;

import java.sql.SQLException;
import java.util.LinkedList;

import application.connector.Connector;
import application.connector.objects.AlertaSensor;
import application.controllers.FXMLMainController;

public class SensorAlertThread extends Thread {

	private FXMLMainController fxmlMainController;
	private Connector connector;
	private int latestAlertId;
	private int id_investigador;

	public SensorAlertThread(Connector connector, FXMLMainController fxmlMainController, int id_investigador) {
		this.fxmlMainController = fxmlMainController;
		this.connector = connector;
		this.id_investigador = id_investigador;
		try {
			if (connector.getSensorAlertTable(id_investigador).isEmpty()) {
				latestAlertId = -1;
			} else {
				latestAlertId = connector.getSensorAlertTable(id_investigador).getLast().getIdAlerta();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				try {
					LinkedList<AlertaSensor> alert_sensors = connector.getSensorAlertTable(id_investigador);
					if (alert_sensors.isEmpty()) {
						sleep(2000);
					} else {
						AlertaSensor latestAlert = connector.getSensorAlertTable(id_investigador).getLast();
						if (latestAlert.getIdAlerta() != latestAlertId) {
							fxmlMainController.showAlert(latestAlert);
							latestAlertId = latestAlert.getIdAlerta();
						}
						sleep(2000);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
