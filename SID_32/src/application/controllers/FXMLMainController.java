package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import main.BD_GUI_Connector;

public class FXMLMainController extends FXMLController implements Initializable {

	private FXMLShellController fxmlShellController = null;
	private BD_GUI_Connector bd_gui_connector = null;

	private ObservableList<String> cultura_observablelist = FXCollections.observableArrayList();

	@FXML
	private ListView<String> cultura_listview;
	@FXML
	private Label cultura_name_label;
	@FXML
	private BorderPane info_pane;
	@FXML
	private HBox monitorized_variables_hbox;

	public FXMLMainController(FXMLShellController fxmlShellController, BD_GUI_Connector bd_gui_connector) {
		this.fxmlShellController = fxmlShellController;
		this.bd_gui_connector = bd_gui_connector;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buildLeftPane();
	}

	private void buildLeftPane() {
		cultura_listview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
				refreshCentralPane();
			}
		});
		try {
			LinkedList<String> culturas = bd_gui_connector.getTableColumn("cultura", "nome_cultura");
			cultura_observablelist.addAll(culturas);
			cultura_listview.getItems().addAll(cultura_observablelist);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("NAO DEU :'(");
		}
	}

	@FXML
	public void logout(ActionEvent event) throws IOException {
		FXMLLoader login_loader = new FXMLLoader(getClass().getResource("/application/views/FXMLLogin.fxml"));
		FXMLLoginController login_controller = new FXMLLoginController(fxmlShellController, bd_gui_connector);
		fxmlShellController.setDisplay("Login", login_loader, login_controller, true);
	}

	private void refreshCentralPane() {
		refreshCulturaNameLabel();
		refreshMonitorizedVariablesHBox();
	}

	private void refreshCulturaNameLabel() {
		cultura_name_label.setText(cultura_listview.getSelectionModel().getSelectedItem());
	}

	private void refreshMonitorizedVariablesHBox() {
		try {
			LinkedList<String> variaveis = bd_gui_connector.getTableColumn("variavel", "nome_variavel");
			for (String variavel : variaveis) {
				Label label = new Label(variavel);
				label.setId("monitorized_variables_label");
				label.setContentDisplay(ContentDisplay.TOP);
				monitorized_variables_hbox.getChildren().add(label);

			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("NAO DEU :'(");
		}
	}

	@FXML
	public void setOnMouseEntered_temperature_info_pane(MouseEvent event) {
		playScaleTransition((Node) event.getSource(), 0.1, Duration.millis(120));
	}

	@FXML
	public void setOnMouseExited_temperature_info_pane(MouseEvent event) {
		playScaleTransition((Node) event.getSource(), -0.1, Duration.millis(120));
	}

	@FXML
	public void setOnMouseEntered_luminosity_info_pane(MouseEvent event) {
		playScaleTransition((Node) event.getSource(), 0.1, Duration.millis(120));
	}

	@FXML
	public void setOnMouseExited_luminosity_info_pane(MouseEvent event) {
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
