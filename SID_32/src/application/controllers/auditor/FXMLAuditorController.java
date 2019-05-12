package application.controllers.auditor;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.connector.objects.Log;
import application.controllers.FXMLController;
import application.controllers.FXMLLoginController;
import application.controllers.FXMLShellController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FXMLAuditorController extends FXMLController implements Initializable {

	private FXMLShellController fxmlShellController = null;
	private Connector connector = null;
	private Connector auditorConnector = null;

	LinkedList<ListView<String>> list_views = new LinkedList<>();
	private ObservableList<String> tables_observable_list = FXCollections.observableArrayList();

	@FXML
	private ChoiceBox<String> tables_choice_box;
	@SuppressWarnings("rawtypes")
	@FXML
	private TableView table_view;

	public FXMLAuditorController(FXMLShellController fxmlShellController, Connector connector,
			Connector auditorConnector) {
		this.fxmlShellController = fxmlShellController;
		this.connector = connector;
		this.auditorConnector = auditorConnector;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			tables_observable_list.addAll(auditorConnector.getAllTables());
			tables_choice_box.setItems(tables_observable_list);
			tables_choice_box.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
				public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
					buildTableView();
				}
			});
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void buildTableView() {
		table_view.getItems().clear();
		switch (tables_choice_box.getSelectionModel().getSelectedItem()) {
		case "categoria_profissional":
			buildCategoriaProfissionalTableView();
			break;
		case "cultura":
			buildCulturaTableView();
			break;
		case "investigador":
			buildInvestigadorTableView();
			break;
		case "medicao":
			buildMedicaoTableView();
			break;
		case "medicao_temperatura":
			buildMedicaoTemperaturaTableView();
			break;
		case "medicao_luminosidade":
			buildMedicaoLuminosidadeTableView();
			break;
		case "variavel":
			buildVariavelTableView();
			break;
		case "variavel_medida":
			buildVariavelMedidaTableView();
			break;
		}
	}

	private void buildVariavelMedidaTableView() {
		buildLogTableView();

	}

	private void buildVariavelTableView() {
		buildLogTableView();

	}

	private void buildMedicaoLuminosidadeTableView() {
		buildLogTableView();

	}

	private void buildMedicaoTemperaturaTableView() {
		buildLogTableView();

	}

	private void buildMedicaoTableView() {
		buildLogTableView();

	}

	private void buildCategoriaProfissionalTableView() {
		buildLogTableView();

	}

	private void buildCulturaTableView() {
		buildLogTableView();
	}

	private void buildInvestigadorTableView() {
		buildLogTableView();

	}

	@SuppressWarnings("unchecked")
	private void buildLogTableView() {
		try {
			TableColumn<Log, String> logIdCol = new TableColumn<Log, String>("Id");
			TableColumn<Log, String> logUtilizadorCol = new TableColumn<Log, String>("Utilizador");
			TableColumn<Log, String> logDataHoraCol = new TableColumn<Log, String>("Data e Hora");
			TableColumn<Log, String> logOperacaoCol = new TableColumn<Log, String>("Operação");

			logIdCol.setCellValueFactory(new PropertyValueFactory<>("idlog"));
			logUtilizadorCol.setCellValueFactory(new PropertyValueFactory<>("utilizador"));
			logDataHoraCol.setCellValueFactory(new PropertyValueFactory<>("dataLog"));
			logOperacaoCol.setCellValueFactory(new PropertyValueFactory<>("operacao"));

			table_view.getColumns().addAll(logIdCol, logUtilizadorCol, logDataHoraCol, logOperacaoCol);

			ObservableList<Log> logs = FXCollections.observableArrayList(
					auditorConnector.getLogColumns(tables_choice_box.getSelectionModel().getSelectedItem()));
			table_view.setItems(logs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void logout(ActionEvent event) throws IOException {
		FXMLLoader login_loader = new FXMLLoader(getClass().getResource("/application/views/FXMLLogin.fxml"));
		FXMLLoginController login_controller = new FXMLLoginController(fxmlShellController, connector);
		fxmlShellController.setDisplay("Login", login_loader, login_controller, true);
	}

	public FXMLShellController getFXMLShellController() {
		return fxmlShellController;
	}

	public Connector getConnector() {
		return connector;
	}

	public Connector getAuditorConnector() {
		return auditorConnector;
	}

}
