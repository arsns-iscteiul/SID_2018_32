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

public class FXMLPopUpAddCultureController extends FXMLController implements Initializable {

	private FXMLMainController fxmlMainController = null;
	private Connector connector = null;
	private String investigador_id;

	@FXML
	private TextField name;
	@FXML
	private TextField description;

	public FXMLPopUpAddCultureController(FXMLMainController fxmlMainController, Connector connector,
			String investigador_id) {
		this.fxmlMainController = fxmlMainController;
		this.connector = connector;
		this.investigador_id = investigador_id;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	public void addCulture(ActionEvent event) {
		try {
			connector.insertCultura(name.getText(), description.getText(), 1, Integer.parseInt(investigador_id));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(event);
			fxmlMainController.refreshLeftPane();
		}
	}

	@FXML
	public void close(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

}
