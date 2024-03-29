package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.connector.objects.AlertaSensor;
import application.connector.objects.AlertaVariavel;
import application.connector.objects.Cultura;
import application.connector.objects.Medicao;
import application.connector.objects.MedicaoLuminosidade;
import application.connector.objects.MedicaoTemperatura;
import application.connector.objects.Variavel;
import application.connector.objects.VariavelMedida;
import application.controllers.popups.FXMLPopUpAddCultureController;
import application.controllers.popups.FXMLPopUpAddManualMeasurementController;
import application.controllers.popups.FXMLPopUpAddVariableToMonitorizeController;
import application.controllers.popups.FXMLPopUpEditVariableMesuredController;
import application.controllers.popups.FXMLPopUpShellController;
import application.controllers.popups.FXMLPopUpShowAlertController;
import application.controllers.popups.FXMLPopUpShowUserProfileController;
import application.support.ManualMesurementAlertThread;
import application.support.SensorAlertThread;
import application.support.StageResizeHelper;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXMLMainController extends FXMLController implements Initializable {

	private FXMLShellController fxmlShellController = null;
	private Connector connector = null;
	private String id_investigador = null;
	private String email_investigador = null;

	private ObservableList<Cultura> cultura_observablelist = FXCollections.observableArrayList();

	private SensorAlertThread sensor_alert_thread;
	private ManualMesurementAlertThread manual_mesurement_alert_thread;

	@FXML
	private ListView<Cultura> cultura_listview;
	@FXML
	private AnchorPane init_display;
	@FXML
	private AnchorPane center_display;
	@FXML
	private Label user_email_label;
	@FXML
	private Label cultura_name_label;
	@FXML
	private TextArea cultura_description_text_area;
	@FXML
	private Label temperature_label;
	@FXML
	private Label luminosity_label;
	@FXML
	private HBox monitorized_variables_hbox;
	@FXML
	private LineChart<String, String> line_chart;
	@FXML
	private TableView<Medicao> table_view;

	public FXMLMainController(FXMLShellController fxmlShellController, Connector connector, String id_investigador,
			String email_investigador) {
		this.fxmlShellController = fxmlShellController;
		this.connector = connector;
		this.id_investigador = id_investigador;
		this.email_investigador = email_investigador;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buildLeftPane();
		buildWelcomingPane();
		buildLineChart();
		buildTableView();
		startThreads();
	}

	private void startThreads() {
		sensor_alert_thread = new SensorAlertThread(connector, this, Integer.parseInt(id_investigador));
		sensor_alert_thread.start();
		manual_mesurement_alert_thread = new ManualMesurementAlertThread(connector, this,
				Integer.parseInt(id_investigador));
		manual_mesurement_alert_thread.start();
	}

	private void buildLeftPane() {
		user_email_label.setText(email_investigador);
		cultura_listview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Cultura>() {
			public void changed(ObservableValue<? extends Cultura> ov, Cultura old_val, Cultura new_val) {
				if (!cultura_listview.getSelectionModel().isEmpty()) {
					refreshCentralPane(cultura_listview.getSelectionModel().getSelectedItem());
				}
			}
		});
		refreshLeftPane();
	}

	public void refreshLeftPane() {
		try {
			cultura_observablelist.clear();
			cultura_observablelist.addAll(connector.getCulturasInvestigador(Integer.parseInt(id_investigador)));
			cultura_listview.getItems().clear();
			cultura_listview.getItems().addAll(cultura_observablelist);
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}

	}

	private void buildWelcomingPane() {
		init_display.setManaged(true);
		init_display.setVisible(true);
		center_display.setManaged(false);
		center_display.setVisible(false);
		try {
			FXMLLoader welcoming_loader = new FXMLLoader(
					getClass().getResource("/application/views/FXMLWelcoming.fxml"));
			Parent welcoming_pane = welcoming_loader.load();
			init_display.getChildren().add(welcoming_pane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buildLineChart() {
		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Time");
		yAxis.setLabel("Measurement");
	}

	@SuppressWarnings("unchecked")
	private void buildTableView() {
		TableColumn<Medicao, String> culturaIdCol = new TableColumn<Medicao, String>("Id");
		TableColumn<Medicao, String> culturaNomeCol = new TableColumn<Medicao, String>("Data e Hora");
		TableColumn<Medicao, String> culturaDescricaoCol = new TableColumn<Medicao, String>("Valor");
		TableColumn<Medicao, String> culturaTipoCol = new TableColumn<Medicao, String>("Variavel");

		culturaIdCol.setCellValueFactory(new PropertyValueFactory<>("id_medicao"));
		culturaNomeCol.setCellValueFactory(new PropertyValueFactory<>("data_hora_medicao"));
		culturaDescricaoCol.setCellValueFactory(new PropertyValueFactory<>("valor_medicao"));
		culturaTipoCol.setCellValueFactory(new PropertyValueFactory<>("variavel_medida_fk"));

		table_view.getColumns().addAll(culturaIdCol, culturaNomeCol, culturaDescricaoCol);
	}

	@FXML
	public void logout(ActionEvent event) throws IOException {
		FXMLLoader login_loader = new FXMLLoader(getClass().getResource("/application/views/FXMLLogin.fxml"));
		FXMLLoginController login_controller = new FXMLLoginController(fxmlShellController, connector);
		fxmlShellController.setDisplay("Login", login_loader, login_controller, true);
		sensor_alert_thread.interrupt();
	}

	private void refreshCentralPane(Cultura cultura_selected) {
		init_display.setManaged(false);
		init_display.setVisible(false);
		center_display.setManaged(true);
		center_display.setVisible(true);
		refreshCulturaNameLabel(cultura_selected.getNome_cultura(), cultura_selected.getDescricao_cultura());
		refreshSensorsHBox();
		refreshMonitorizedVariablesHBox(cultura_selected.getId_cultura());
		refreshLineChart(cultura_selected.getId_cultura(), cultura_selected.getNome_cultura());
		refreshTableView(cultura_selected.getId_cultura());
	}

	private void refreshCulturaNameLabel(String cultura_selected_name, String cultura_selected_description) {
		cultura_name_label.setText(cultura_selected_name);
		cultura_description_text_area.setText(cultura_selected_description);
	}

	private void refreshSensorsHBox() {
		try {
			LinkedList<MedicaoTemperatura> medicoes_temperatura = connector.getMedicoesTemperatura();
			LinkedList<MedicaoLuminosidade> medicoes_luminosidade = connector.getMedicoesLuminosidade();
			if (!medicoes_temperatura.isEmpty()) {
				temperature_label.setText(medicoes_temperatura.getLast().getValor_medicao_temperatura());
				if (Double.parseDouble(medicoes_temperatura.get(medicoes_temperatura.size() - 1)
						.getValor_medicao_temperatura()) <= Double
								.parseDouble(medicoes_temperatura.getLast().getValor_medicao_temperatura())) {
					temperature_label.setId("up");
				} else {
					temperature_label.setId("down");
				}
			} else {
				System.out.println("N�o existem medi��es nos sensores de temperatura");
			}
			if (!medicoes_luminosidade.isEmpty()) {
				luminosity_label.setText(medicoes_luminosidade.getLast().getValor_medicao_luminosidade());
				if (Double.parseDouble(medicoes_luminosidade.get(medicoes_luminosidade.size() - 1)
						.getValor_medicao_luminosidade()) <= Double
								.parseDouble(medicoes_luminosidade.getLast().getValor_medicao_luminosidade())) {
					luminosity_label.setId("up");
				} else {
					luminosity_label.setId("down");
				}
			} else {
				System.out.println("N�o existem medi��es nos sensores de luminosidade");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void refreshMonitorizedVariablesHBox(String cultura_selected_id) {
		monitorized_variables_hbox.getChildren().clear();
		try {
			for (Variavel variavel : connector.getVariaveisCultura(Integer.parseInt(cultura_selected_id))) {
				for (VariavelMedida variavel_medida : connector.getVariavelMedidaTable()) {
					if (variavel_medida.getVariavel_fk().equalsIgnoreCase(variavel.getId_variavel())) {
						Button variavel_label = new Button(variavel.getNome_variavel());
						variavel_label.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent e) {
								showMesuredVariableInfo(variavel_medida.getVariavel_medida_id());
							}
						});
						variavel_label.setContentDisplay(ContentDisplay.TOP);
						variavel_label.setCursor(Cursor.HAND);
						variavel_label.setId(variavel.getNome_variavel());
						monitorized_variables_hbox.getChildren().add(variavel_label);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (monitorized_variables_hbox.getChildren().isEmpty()) {
			monitorized_variables_hbox.getChildren()
					.add(new Label("You have no variables being monitorized in this culture"));
		}

	}

	public void refreshTableView(String cultura_selected_id) {
		clearTableView();
		try {
			ObservableList<Medicao> medicoes = FXCollections
					.observableArrayList(connector.getMedicoesDaCultura(cultura_selected_id));
			table_view.setItems(medicoes);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void clearTableView() {
		for (int i = 0; i < table_view.getItems().size(); i++) {
			table_view.getItems().clear();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void refreshLineChart(String cultura_selected_id, String cultura_selected_name) {
		line_chart.getData().clear();
		try {
			for (LinkedList<Medicao> list_medicoes : connector.getMedicoesDaCulturaByVariable(cultura_selected_id)) {
				if (list_medicoes.isEmpty()) {
					break;
				}
				XYChart.Series series = new XYChart.Series();
				series.setName(list_medicoes.getFirst().getMore_info());
				for (Medicao medicao : list_medicoes) {

					series.getData().add(new XYChart.Data(medicao.getData_hora_medicao(),
							Double.parseDouble(medicao.getValor_medicao())));

				}
				line_chart.getData().add(series);
				Node line = series.getNode().lookup(".chart-series-line");
				line.setId(list_medicoes.getFirst().getMore_info() + "_stroke_color");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error about mesurements");
		}
	}

	@FXML
	public void setOnMouseEntered_info_pane(MouseEvent event) {
		playScaleTransition((Node) event.getSource(), 0.1, Duration.millis(120));
	}

	@FXML
	public void setOnMouseExited_info_pane(MouseEvent event) {
		playScaleTransition((Node) event.getSource(), -0.1, Duration.millis(120));
	}

	private void playScaleTransition(Node node, double scale, Duration duration) {
		ScaleTransition scaleTransition = new ScaleTransition();
		scaleTransition.setDuration(duration);
		scaleTransition.setNode(node);
		scaleTransition.setByY(scale);
		scaleTransition.setByX(scale);
		scaleTransition.play();
	}

	@FXML
	private void addCulture() {
		FXMLLoader popup_add_culture_loader = new FXMLLoader(
				getClass().getResource("/application/views/popups/FXMLPopUpAddCulture.fxml"));
		FXMLPopUpAddCultureController popup_add_culture_controller = new FXMLPopUpAddCultureController(this, connector,
				id_investigador);
		buildPopPup("Add culture", "PopUpAddCulture", popup_add_culture_loader, popup_add_culture_controller);
	}

	@FXML
	private void removeCulture() {
		try {
			connector.deleteCultura(
					Integer.parseInt(cultura_listview.getSelectionModel().getSelectedItem().getId_cultura()));
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		} finally {
			cultura_listview.getSelectionModel().clearSelection();
			buildWelcomingPane();
			refreshLeftPane();
		}
	}

	@FXML
	private void addVariableToBeMonitorized() {
		FXMLLoader popup_add_variable_to_monitorize_loader = new FXMLLoader(
				getClass().getResource("/application/views/popups/FXMLPopUpAddVariableToMonitorize.fxml"));
		FXMLPopUpAddVariableToMonitorizeController popup_add_variable_to_monitorize_controller = new FXMLPopUpAddVariableToMonitorizeController(
				this, connector, cultura_listview.getSelectionModel().getSelectedItem().getId_cultura(),
				id_investigador, true);
		buildPopPup("Add variable to monitorize", "PopUpAddVariableToMonitorize",
				popup_add_variable_to_monitorize_loader, popup_add_variable_to_monitorize_controller);
	}

	@FXML
	private void removeVariableToBeMonitorized() {
		FXMLLoader popup_add_variable_to_monitorize_loader = new FXMLLoader(
				getClass().getResource("/application/views/popups/FXMLPopUpAddVariableToMonitorize.fxml"));
		FXMLPopUpAddVariableToMonitorizeController popup_add_variable_to_monitorize_controller = new FXMLPopUpAddVariableToMonitorizeController(
				this, connector, cultura_listview.getSelectionModel().getSelectedItem().getId_cultura(),
				id_investigador, false);
		buildPopPup("Remove variable being monitorize", "PopUpAddVariableToMonitorize",
				popup_add_variable_to_monitorize_loader, popup_add_variable_to_monitorize_controller);
	}

	@FXML
	private void addManualMeasurement() {
		FXMLLoader popup_add_manual_measurement_loader = new FXMLLoader(
				getClass().getResource("/application/views/popups/FXMLPopUpAddManualMeasurement.fxml"));
		FXMLPopUpAddManualMeasurementController popup_add_manual_measurement_controller = new FXMLPopUpAddManualMeasurementController(
				this, connector, cultura_listview.getSelectionModel().getSelectedItem().getId_cultura(),
				cultura_listview.getSelectionModel().getSelectedItem().getNome_cultura(), id_investigador);
		buildPopPup("Add manual mesurement", "PopUpAddManualMeasurement", popup_add_manual_measurement_loader,
				popup_add_manual_measurement_controller);
	}

	private void buildPopPup(String title_name, String fxml_name, FXMLLoader fxml_loader,
			FXMLController fxml_controller) {
		try {
			FXMLLoader popup_shell_loader = new FXMLLoader(
					getClass().getResource("/application/views/popups/FXMLPopUpShell.fxml"));
			FXMLPopUpShellController popup_shell_controller = new FXMLPopUpShellController(connector, title_name);
			popup_shell_loader.setController(popup_shell_controller);

			Parent popup_shell = popup_shell_loader.load();
			popup_shell.getStylesheets()
					.add(getClass().getResource("/application/styles/popups/FXMLPopUpShell.css").toExternalForm());

			Stage popup_stage = new Stage();
			popup_stage.initModality(Modality.APPLICATION_MODAL);
			popup_stage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
			Scene scene = new Scene(popup_shell);
			scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
			popup_stage.setScene(scene);
			StageResizeHelper.addResizeListener(popup_stage);
			popup_stage.show();

			popup_shell_controller.setDisplay(fxml_name, fxml_loader, fxml_controller);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void showUserInfo() {
		FXMLLoader popup_show_user_profile_loader = new FXMLLoader(
				getClass().getResource("/application/views/popups/FXMLPopUpShowUserProfile.fxml"));
		FXMLPopUpShowUserProfileController popup_show_user_profile_controller = new FXMLPopUpShowUserProfileController(
				this, connector, id_investigador);
		buildPopPup("User profile info", "PopUpShowUserProfile", popup_show_user_profile_loader,
				popup_show_user_profile_controller);
	}

	public void showAlert(AlertaSensor sensor_alert) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				FXMLLoader popup_show_alert_loader = new FXMLLoader(
						getClass().getResource("/application/views/popups/FXMLPopUpShowAlert.fxml"));
				FXMLPopUpShowAlertController popup_show_alert_controller = new FXMLPopUpShowAlertController(
						sensor_alert);
				buildPopPup("Alert", "PopUpShowAlert", popup_show_alert_loader, popup_show_alert_controller);

			}
		});
	}

	public void showAlert(AlertaVariavel manual_mesurement_alert) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				FXMLLoader popup_show_alert_loader = new FXMLLoader(
						getClass().getResource("/application/views/popups/FXMLPopUpShowAlert.fxml"));
				FXMLPopUpShowAlertController popup_show_alert_controller = new FXMLPopUpShowAlertController(
						manual_mesurement_alert);
				buildPopPup("Alert", "PopUpShowAlert", popup_show_alert_loader, popup_show_alert_controller);

			}
		});
	}

	public void showMesuredVariableInfo(String mesured_variable_id) {
		FXMLLoader popup_show_mesured_variable_edit_loader = new FXMLLoader(
				getClass().getResource("/application/views/popups/FXMLPopUpEditVariableMesured.fxml"));
		FXMLPopUpEditVariableMesuredController popup_show_mesured_variable_edit_controller = new FXMLPopUpEditVariableMesuredController(
				connector, mesured_variable_id);
		buildPopPup("Mesured Variable info", "PopUpEditVariableMesured", popup_show_mesured_variable_edit_loader,
				popup_show_mesured_variable_edit_controller);
	}
}
