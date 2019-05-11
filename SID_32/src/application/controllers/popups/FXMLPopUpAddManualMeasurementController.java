package application.controllers.popups;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.connector.objects.Variavel;
import application.controllers.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLPopUpAddManualMeasurementController extends FXMLController implements Initializable {

	private Connector connector = null;
	private String cultura_id;

	private ObservableList<Variavel> variables_observablelist = FXCollections.observableArrayList();

	@FXML
	private ChoiceBox<Variavel> variable_choice_box;
	@FXML
	private TextField value_field;

	public FXMLPopUpAddManualMeasurementController(Connector connector, String cultura_id) {
		this.connector = connector;
		this.cultura_id = cultura_id;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			variables_observablelist.addAll(connector.getVariaveisCultura(Integer.parseInt(cultura_id)));
			variable_choice_box.setItems(variables_observablelist);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void addManualMeasurement(ActionEvent event) {
		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			System.out.println(timestamp.toString());
			String fields[];
			fields = new String[] { timestamp.toString(), value_field.getText(),
					variable_choice_box.getSelectionModel().getSelectedItem().getId_variavel() };
			connector.insertMedicao(fields);
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
