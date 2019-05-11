package application.controllers.popups;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.controllers.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLPopUpAddCultureController extends FXMLController implements Initializable {

	private Connector connector = null;
	private String investigador_id;

	@FXML
	private TextField name;
	@FXML
	private TextField description;

	public FXMLPopUpAddCultureController(Connector connector, String investigador_id) {
		this.connector = connector;
		this.investigador_id = investigador_id;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML
	public void addCulture(ActionEvent event) {
		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			System.out.println(timestamp.toString());
			String fields[];
			fields = new String[] { name.getText(), description.getText(), "1", investigador_id };
			connector.insertCultura(fields);
		} catch (SQLException e) {
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
