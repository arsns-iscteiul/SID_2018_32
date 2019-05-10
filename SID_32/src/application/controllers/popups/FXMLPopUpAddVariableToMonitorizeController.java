package application.controllers.popups;

import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.connector.objects.Variavel;
import application.controllers.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

public class FXMLPopUpAddVariableToMonitorizeController extends FXMLController implements Initializable {

	private Connector connector = null;
	private String cultura_id;

	private ObservableList<Variavel> variables_not_monitorized_observablelist = FXCollections.observableArrayList();

	@FXML
	private ListView<Variavel> variables_not_monitorized_list_view;

	public FXMLPopUpAddVariableToMonitorizeController(Connector connector, String cultura_id) {
		this.connector = connector;
		this.cultura_id = cultura_id;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		variables_not_monitorized_list_view.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<Variavel>() {
					public void changed(ObservableValue<? extends Variavel> ov, Variavel old_val, Variavel new_val) {

					}
				});
		try {
			LinkedList<Variavel> variables_not_being_monitorized = new LinkedList<>();
			for (Variavel variable : connector.getVariaveis()) {
				boolean is_being_monitorized = false;
				for (Variavel variables_being_monitorized : connector.getVariaveisDaCultura(cultura_id)) {
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
			System.out.println("NAO DEU :'(");
		}
	}

	@FXML
	public void add_variable_to_be_monitorized() {
		System.out.println(variables_not_monitorized_list_view.getSelectionModel().getSelectedItem());
	}

}
