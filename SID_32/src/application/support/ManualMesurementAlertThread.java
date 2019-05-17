package application.support;

import java.sql.SQLException;
import java.util.LinkedList;

import application.connector.Connector;
import application.connector.objects.AlertaVariavel;
import application.controllers.FXMLMainController;

public class ManualMesurementAlertThread extends Thread {

	private FXMLMainController fxmlMainController;
	private Connector connector;
	private int latestAlertId;
	private int id_investigador;

	public ManualMesurementAlertThread(Connector connector, FXMLMainController fxmlMainController,
			int id_investigador) {
		this.fxmlMainController = fxmlMainController;
		this.connector = connector;
		this.id_investigador = id_investigador;
		try {
			if (connector.getVariableAlertTable(id_investigador).isEmpty()) {
				latestAlertId = -1;
			} else {
				latestAlertId = connector.getVariableAlertTable(id_investigador).getLast().getIdAlertaVariavel();
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
					LinkedList<AlertaVariavel> alert_sensors = connector.getVariableAlertTable(id_investigador);
					if (alert_sensors.isEmpty()) {
						sleep(2000);
					} else {
						AlertaVariavel latestAlert = connector.getVariableAlertTable(id_investigador).getLast();
						if (latestAlert.getIdAlertaVariavel() != latestAlertId) {
							fxmlMainController.showAlert(latestAlert);
							latestAlertId = latestAlert.getIdAlertaVariavel();
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
