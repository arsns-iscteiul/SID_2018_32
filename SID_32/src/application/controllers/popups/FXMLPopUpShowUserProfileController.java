package application.controllers.popups;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.controllers.FXMLController;
import application.controllers.FXMLMainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLPopUpShowUserProfileController extends FXMLController implements Initializable {

	private FXMLMainController fxmlMainController = null;
	private Connector connector = null;
	private String investigador_id;

	@FXML
	private TextField time_field;
	@FXML
	private TextField temperature_red_up_field;
	@FXML
	private TextField temperature_orange_up_field;
	@FXML
	private TextField temperature_orange_down_field;
	@FXML
	private TextField temperature_red_down_field;
	@FXML
	private TextField luminosity_red_up_field;
	@FXML
	private TextField luminosity_orange_up_field;
	@FXML
	private TextField luminosity_orange_down_field;
	@FXML
	private TextField luminosity_red_down_field;

	public FXMLPopUpShowUserProfileController(FXMLMainController fxmlMainController, Connector connector,
			String investigador_id) {
		this.fxmlMainController = fxmlMainController;
		this.connector = connector;
		this.investigador_id = investigador_id;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	public void updateUserInfo(ActionEvent event) {
		try {
			connector.updateAlerta(Integer.parseInt(time_field.getText()),
					Float.parseFloat(temperature_red_up_field.getText()),
					Float.parseFloat(temperature_red_down_field.getText()),
					Float.parseFloat(temperature_orange_up_field.getText()),
					Float.parseFloat(temperature_orange_down_field.getText()),
					Float.parseFloat(luminosity_red_up_field.getText()),
					Float.parseFloat(luminosity_red_down_field.getText()),
					Float.parseFloat(luminosity_orange_up_field.getText()),
					Float.parseFloat(luminosity_orange_down_field.getText()), Integer.parseInt(investigador_id));
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		} finally {
			close(event);
		}
	}

	@FXML
	public void close(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

}
