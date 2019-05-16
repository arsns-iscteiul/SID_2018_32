package application.controllers.popups;

import java.net.URL;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.controllers.FXMLController;
import application.controllers.FXMLMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FXMLPopUpShowAlertController extends FXMLController implements Initializable {

	private FXMLMainController fxmlMainController = null;
	private Connector connector = null;
	private String investigador_id;
	private String color;

	@FXML
	private Label alert_message_label;

	public FXMLPopUpShowAlertController(FXMLMainController fxmlMainController, Connector connector,
			String investigador_id, String color) {
		this.fxmlMainController = fxmlMainController;
		this.connector = connector;
		this.investigador_id = investigador_id;
		this.color = color;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		alert_message_label.setId(color);
	}

	@FXML
	public void close(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

}
