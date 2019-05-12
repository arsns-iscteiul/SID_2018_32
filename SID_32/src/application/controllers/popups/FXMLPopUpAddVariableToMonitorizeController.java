package application.controllers.popups;

import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.connector.objects.Variavel;
import application.controllers.FXMLController;
import application.controllers.FXMLMainController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class FXMLPopUpAddVariableToMonitorizeController extends FXMLController implements Initializable {

	private FXMLMainController fxmlMainController = null;
	private Connector connector = null;
	private String cultura_id;
	private boolean add_or_remove;

	private ObservableList<Variavel> variables_not_monitorized_observablelist = FXCollections.observableArrayList();

	@FXML
	private ListView<Variavel> variables_not_monitorized_list_view;

	public FXMLPopUpAddVariableToMonitorizeController(FXMLMainController fxmlMainController, Connector connector,
			String cultura_id, boolean add_or_remove) {
		this.fxmlMainController = fxmlMainController;
		this.connector = connector;
		this.cultura_id = cultura_id;
		this.add_or_remove = add_or_remove;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		variables_not_monitorized_list_view.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Variavel>() {
					public void changed(ObservableValue<? extends Variavel> ov, Variavel old_val, Variavel new_val) {

					}
				});
		if (add_or_remove) {
			try {
				LinkedList<Variavel> variables_not_being_monitorized = new LinkedList<>();
				for (Variavel variable : connector.getVariavelTable()) {
					boolean is_being_monitorized = false;
					for (Variavel variables_being_monitorized : connector
							.getVariaveisCultura(Integer.parseInt(cultura_id))) {
						if (variables_being_monitorized.getId_variavel().equalsIgnoreCase(variable.getId_variavel())) {
							is_being_monitorized = true;
						}
					}
					if (!is_being_monitorized) {
						variables_not_being_monitorized.add(variable);
					}

				}
				variables_not_monitorized_observablelist.addAll(variables_not_being_monitorized);
				variables_not_monitorized_list_view.getItems().addAll(variables_not_monitorized_observablelist);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {
				LinkedList<Variavel> variables_being_monitorized = new LinkedList<>();
				for (Variavel variable : connector.getVariaveisCultura(Integer.parseInt(cultura_id))) {
					variables_being_monitorized.add(variable);
				}
				variables_not_monitorized_observablelist.addAll(variables_being_monitorized);
				variables_not_monitorized_list_view.getItems().addAll(variables_not_monitorized_observablelist);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	@FXML
	public void addVariableToMonitorize(ActionEvent event) {
		try {
			if (add_or_remove) {

				connector.insertVariavelMedida(Integer.parseInt(cultura_id), 10, 20, Integer.parseInt(
						variables_not_monitorized_list_view.getSelectionModel().getSelectedItem().getId_variavel()));
			} else {

				connector.deleteVariavelMedida(Integer.parseInt(
						variables_not_monitorized_list_view.getSelectionModel().getSelectedItem().getId_variavel()));
			}
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		} finally {
			close(event);
			fxmlMainController.refreshMonitorizedVariablesHBox(cultura_id);
		}

	}

	@FXML
	public void close(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

}
