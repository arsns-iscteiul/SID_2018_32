package application.controllers.popups;

import java.net.URL;
import java.util.ResourceBundle;

import application.connector.objects.AlertaSensor;
import application.connector.objects.AlertaVariavel;
import application.controllers.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FXMLPopUpShowAlertController extends FXMLController implements Initializable {
	
	private AlertaSensor sensor_alert = null;
	private AlertaVariavel variable_alert = null;

	@FXML
	private Label alert_message_label;
	@FXML
	private Label type_image_view_label;
	@FXML
	private Label date_hour_alert;

	public FXMLPopUpShowAlertController(AlertaSensor sensor_alert) {
		this.sensor_alert = sensor_alert;
	}

	public FXMLPopUpShowAlertController(AlertaVariavel variable_alert) {
		this.variable_alert = variable_alert;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setDesignProperties();
	}

	private void setDesignProperties() {
		if (sensor_alert != null) {
			alert_message_label.setId(sensor_alert.getIntensidade());
			alert_message_label.setText(sensor_alert.getDescricao());
			date_hour_alert.setText(sensor_alert.getDatahoraalerta());
			type_image_view_label.setId(sensor_alert.getTipo());
		} else if (variable_alert != null) {
			alert_message_label.setId(variable_alert.getIntensidade());
			alert_message_label.setText(variable_alert.getDescricao());
			date_hour_alert.setText(variable_alert.getDataHora());
		}
	}

	@FXML
	public void close(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

}
