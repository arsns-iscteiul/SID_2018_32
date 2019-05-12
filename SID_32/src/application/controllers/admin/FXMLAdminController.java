package application.controllers.admin;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.connector.objects.Investigador;
import application.connector.objects.Variavel;
import application.controllers.FXMLController;
import application.controllers.FXMLLoginController;
import application.controllers.FXMLShellController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class FXMLAdminController extends FXMLController implements Initializable {

	private FXMLShellController fxmlShellController = null;
	private Connector connector = null;

	@FXML
	private TableView<Investigador> investigadores_table_view;
	@FXML
	private TableView<Variavel> variaveis_table_view;
	@FXML
	private Button add_investigador;
	@FXML
	private Button add_variavel;
	@FXML
	private VBox add_researcher_hbox;
	@FXML
	private VBox add_variable_hbox;
	@FXML
	private TextField new_nome_investigador;
	@FXML
	private TextField new_email_investigador;
	@FXML
	private PasswordField new_password_investigador;
	@FXML
	private TextField new_catprof_investigador;
	@FXML
	private TextField new_nome_variavel;

	public FXMLAdminController(FXMLShellController fxmlShellController, Connector connector) {
		this.fxmlShellController = fxmlShellController;
		this.connector = connector;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		add_researcher_hbox.setVisible(false);
		add_researcher_hbox.setManaged(false);
		add_variable_hbox.setVisible(false);
		add_variable_hbox.setManaged(false);
		buildInvestigadoresTableView();
		buildVariaveisTableView();
	}

	@SuppressWarnings("unchecked")
	private void buildInvestigadoresTableView() {
		try {
			TableColumn<Investigador, String> investigadorIdCol = new TableColumn<Investigador, String>("Id");
			TableColumn<Investigador, String> investigadorNomeCol = new TableColumn<Investigador, String>("Nome");
			TableColumn<Investigador, String> investigadorEmailCol = new TableColumn<Investigador, String>("Email");
			TableColumn<Investigador, String> investigadorPasswordCol = new TableColumn<Investigador, String>(
					"Passwprd");
			TableColumn<Investigador, String> investigadorCatProCol = new TableColumn<Investigador, String>(
					"Cat. Prof.");

			investigadorIdCol.setCellValueFactory(new PropertyValueFactory<>("id_investigador"));
			investigadorNomeCol.setCellValueFactory(new PropertyValueFactory<>("nome_investigador"));
			investigadorEmailCol.setCellValueFactory(new PropertyValueFactory<>("email_investigador"));
			investigadorPasswordCol.setCellValueFactory(new PropertyValueFactory<>("pwd"));
			investigadorCatProCol.setCellValueFactory(new PropertyValueFactory<>("categoria_profissional"));

			investigadores_table_view.getColumns().addAll(investigadorIdCol, investigadorNomeCol, investigadorEmailCol,
					investigadorPasswordCol, investigadorCatProCol);

			ObservableList<Investigador> investigadores = FXCollections
					.observableArrayList(connector.getInvestigadorTable());
			investigadores_table_view.setItems(investigadores);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void refreshInvestigadoresTableView() {
		clearTableView(investigadores_table_view);
		try {
			ObservableList<Investigador> investigadores = FXCollections
					.observableArrayList(connector.getInvestigadorTable());
			investigadores_table_view.setItems(investigadores);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void buildVariaveisTableView() {
		try {

			TableColumn<Variavel, String> variavelIdCol = new TableColumn<Variavel, String>("Id");
			TableColumn<Variavel, String> variavelNomeCol = new TableColumn<Variavel, String>("Nome");

			variavelIdCol.setCellValueFactory(new PropertyValueFactory<>("id_variavel"));
			variavelNomeCol.setCellValueFactory(new PropertyValueFactory<>("nome_variavel"));

			variaveis_table_view.getColumns().addAll(variavelIdCol, variavelNomeCol);

			ObservableList<Variavel> variaveis = FXCollections.observableArrayList(connector.getVariavelTable());
			variaveis_table_view.setItems(variaveis);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void refreshVariaveisTableView() {
		clearTableView(variaveis_table_view);
		try {
			ObservableList<Variavel> variaveis = FXCollections.observableArrayList(connector.getVariavelTable());
			variaveis_table_view.setItems(variaveis);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@FXML
	private void addResearcher() {
		add_researcher_hbox.setVisible(true);
		add_researcher_hbox.setManaged(true);
	}

	@FXML
	private void addResearcherInsert() {
		try {
			connector.insertInvestigador(new_nome_investigador.getText(), new_email_investigador.getText(),
					new_catprof_investigador.getText(), new_password_investigador.getText());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			add_researcher_hbox.setVisible(false);
			add_researcher_hbox.setManaged(false);
			buildInvestigadoresTableView();
		}
	}

	@FXML
	private void addVariable() {
		add_variable_hbox.setVisible(true);
		add_variable_hbox.setManaged(true);
	}

	@FXML
	private void addVariableInsert() {
		try {
			connector.insertVariavel(new_nome_variavel.getText());
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			add_variable_hbox.setVisible(false);
			add_variable_hbox.setManaged(false);
			buildVariaveisTableView();
		}
	}

	@SuppressWarnings("rawtypes")
	public void clearTableView(TableView table_view) {
		for (int i = 0; i < table_view.getItems().size(); i++) {
			table_view.getItems().clear();
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

}
