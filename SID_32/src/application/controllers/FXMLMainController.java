package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.connector.objects.Cultura;
import application.connector.objects.Medicao;
import application.connector.objects.Variavel;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class FXMLMainController extends FXMLController implements Initializable {

	private FXMLShellController fxmlShellController = null;
	private Connector connector = null;

	private ObservableList<Cultura> cultura_observablelist = FXCollections.observableArrayList();

	@FXML
	private ListView<Cultura> cultura_listview;
	@FXML
	private Label cultura_name_label;
	@FXML
	private BorderPane info_pane;
	@FXML
	private HBox monitorized_variables_hbox;
	@FXML
	private LineChart<String, String> line_chart;
	@FXML
	private TableView<Medicao> table_view;

	public FXMLMainController(FXMLShellController fxmlShellController, Connector connector) {
		this.fxmlShellController = fxmlShellController;
		this.connector = connector;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buildLeftPane();
		buildLineChart();
		buildTableView();
	}

	private void buildLeftPane() {
		cultura_listview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Cultura>() {
			public void changed(ObservableValue<? extends Cultura> ov, Cultura old_val, Cultura new_val) {
				refreshCentralPane(cultura_listview.getSelectionModel().getSelectedItem());
			}
		});
		try {
			cultura_observablelist.addAll(connector.getCulturas());
			cultura_listview.getItems().addAll(cultura_observablelist);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("NAO DEU :'(");
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
	}

	private void refreshCentralPane(Cultura cultura_selected) {
		refreshCulturaNameLabel(cultura_selected.getNome_cultura());
		refreshMonitorizedVariablesHBox(cultura_selected.getId_cultura());
		refreshLineChart(cultura_selected.getId_cultura(), cultura_selected.getNome_cultura());
		refreshTableView(cultura_selected.getId_cultura());
	}

	private void refreshCulturaNameLabel(String cultura_selected_name) {
		cultura_name_label.setText(cultura_selected_name);
	}

	private void refreshMonitorizedVariablesHBox(String cultura_selected_id) {
		monitorized_variables_hbox.getChildren().clear();
		try {
			for (Variavel variavel : connector.getVariaveisDaCultura(cultura_selected_id)) {
				Label variavel_label = new Label(variavel.getNome_variavel());
				variavel_label.setContentDisplay(ContentDisplay.TOP);
				variavel_label.setCursor(Cursor.HAND);
				variavel_label.setId(variavel.getNome_variavel());
				monitorized_variables_hbox.getChildren().add(variavel_label);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (monitorized_variables_hbox.getChildren().isEmpty()) {
			monitorized_variables_hbox.getChildren()
					.add(new Label("You have no variables being monitorized in this culture"));
		}

	}

	private void refreshTableView(String cultura_selected_id) {
		clearTableView();
		try {
			ObservableList<Medicao> medicoes = FXCollections.observableArrayList(connector.getMedicoesDaCultura(cultura_selected_id));
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
	private void refreshLineChart(String cultura_selected_id, String cultura_selected_name) {
		line_chart.getData().clear();
		try {
			XYChart.Series series = new XYChart.Series();
			for (Medicao medicao : connector.getMedicoesDaCultura(cultura_selected_id)) {
				series.getData().add(
						new XYChart.Data(medicao.getData_hora_medicao(), Integer.parseInt(medicao.getId_medicao())));
			}
			line_chart.getData().add(series);
		} catch (SQLException e) {
			e.printStackTrace();
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
}
