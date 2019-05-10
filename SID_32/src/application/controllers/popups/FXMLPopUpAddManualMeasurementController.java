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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

public class FXMLPopUpAddManualMeasurementController extends FXMLController implements Initializable {

	private Connector connector = null;
	private String cultura_id;
	
	private ObservableList<Variavel> variables_observablelist = FXCollections.observableArrayList();
	
	@FXML
	private ChoiceBox<Variavel> variable_choice_box;

	public FXMLPopUpAddManualMeasurementController(Connector connector, String cultura_id) {
		this.connector = connector;
		this.cultura_id = cultura_id;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			variables_observablelist.addAll(connector.getVariaveisDaCultura(cultura_id));
			variable_choice_box.setItems(variables_observablelist);
			
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			System.out.println(timestamp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}