package application.controllers.popups;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.connector.objects.Variavel;
import application.controllers.FXMLController;
import application.controllers.FXMLMainController;
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

	private FXMLMainController fxmlMainController = null;
	private Connector connector = null;
	private String cultura_id;
	private String cultura_nome;
	private String id_investigador;

	private ObservableList<Variavel> variables_observablelist = FXCollections.observableArrayList();

	@FXML
	private ChoiceBox<Variavel> variable_choice_box;
	@FXML
	private TextField value_field;

	public FXMLPopUpAddManualMeasurementController(FXMLMainController fxmlMainController, Connector connector,
			String cultura_id, String cultura_nome, String id_investigador) {
		this.fxmlMainController = fxmlMainController;
		this.connector = connector;
		this.cultura_id = cultura_id;
		this.cultura_nome = cultura_nome;
		this.id_investigador = id_investigador;
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
			connector.insertMedicao(Integer.parseInt(value_field.getText()),
					Integer.parseInt(variable_choice_box.getSelectionModel().getSelectedItem().getId_variavel()),
					Integer.parseInt(id_investigador));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(event);
			fxmlMainController.refreshLineChart(cultura_id, cultura_nome);
			fxmlMainController.refreshTableView(cultura_id);
		}
	}

	@FXML
	public void close(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

}
