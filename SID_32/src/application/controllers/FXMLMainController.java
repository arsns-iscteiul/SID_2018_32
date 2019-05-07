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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
	@FXML
	private TableView<Cultura> table_view;

	public FXMLMainController(FXMLShellController fxmlShellController, BD_GUI_Connector bd_gui_connector) {
		this.fxmlShellController = fxmlShellController;
		this.bd_gui_connector = bd_gui_connector;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buildLeftPane();
		buildTableView();
	}

	private void buildLeftPane() {
		cultura_listview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
				refreshCentralPane(cultura_listview.getSelectionModel().getSelectedItem());
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

	@SuppressWarnings("unchecked")
	private void buildTableView() {
		TableColumn<Cultura, String> culturaIdCol = new TableColumn<Cultura, String>("Id");
		TableColumn<Cultura, String> culturaNomeCol = new TableColumn<Cultura, String>("Name");
		TableColumn<Cultura, String> culturaDescricaoCol = new TableColumn<Cultura, String>("Description");
		TableColumn<Cultura, String> culturaTipoCol = new TableColumn<Cultura, String>("Type");

		culturaIdCol.setCellValueFactory(new PropertyValueFactory<>("id_cultura"));
		culturaNomeCol.setCellValueFactory(new PropertyValueFactory<>("nome_cultura"));
		culturaDescricaoCol.setCellValueFactory(new PropertyValueFactory<>("descricao_cultura"));
		culturaTipoCol.setCellValueFactory(new PropertyValueFactory<>("tipo_cultura"));

		table_view.getColumns().addAll(culturaIdCol, culturaNomeCol, culturaDescricaoCol);
	}

	@FXML
	public void logout(ActionEvent event) throws IOException {
		FXMLLoader login_loader = new FXMLLoader(getClass().getResource("/application/views/FXMLLogin.fxml"));
		FXMLLoginController login_controller = new FXMLLoginController(fxmlShellController, bd_gui_connector);
		fxmlShellController.setDisplay("Login", login_loader, login_controller, true);
	}

	private void refreshCentralPane(String cultura_selected) {
		refreshCulturaNameLabel(cultura_selected);
		refreshMonitorizedVariablesHBox(cultura_selected);
		refreshTableView();
	}

	private void refreshCulturaNameLabel(String cultura_selected) {
		cultura_name_label.setText(cultura_selected);
	}

	private void refreshMonitorizedVariablesHBox(String cultura_selected) {
		try {
			String[] a = cultura_selected.split("-");
			String id = a[1];
			System.out.println(id);
			LinkedList<String>[] table = bd_gui_connector.allTableData("variavel_medida");
			for (int i = 0; i != table.length; i++) {
				for (String s : table[1]) {
					System.out.println(s);
					if (s.equals(id)) {
						monitorized_variables_hbox.getChildren().add(new Label("TEM"));
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("nao deu");
		}
	}

	@SuppressWarnings("unchecked")
	private void refreshTableView() {
		clearTableView();
		ObservableList<Cultura> list = getCulturaList();
		table_view.setItems(list);
	}

	public void clearTableView() {
		for (int i = 0; i < table_view.getItems().size(); i++) {
			table_view.getItems().clear();
		}
	}

	private ObservableList<Cultura> getCulturaList() {

		Cultura c1 = new Cultura("1", "Tomates",
				"O tomate é o fruto do tomateiro (Solanum lycopersicum; Solanaceae). Da sua família, fazem também parte as berinjelas, as pimentas e os pimentões, além de algumas espécies não comestíveis. A palavra portuguesa tomate vem do castelhano tomate, derivada do náuatle (língua asteca) tomatl. Esta apareceu pela primeira vez na imprensa em 1595.",
				"Plantae", "1");
		Cultura c2 = new Cultura("2", "Maçã",
				"A maçã é o pseudofruto pomáceo da macieira (Malus domestica), árvore da família Rosaceae. É um dos pseudofrutos de árvore mais cultivados, e o mais conhecido dos muitos membros do género Malus que são usados ​​pelos seres humanos. As maçãs crescem em pequenas árvores, de folha caducifólia que florescem na Primavera e produzem fruto no Outono. A árvore é originária da Ásia Ocidental, onde o seu ancestral selvagem, Malus sieversii, ainda é encontrado atualmente. As maçãs têm sido cultivadas há milhares de anos na Ásia e Europa, tendo sido trazidas para a América do Norte pelos colonizadores europeus. As maçãs têm estado presentes na mitologia e religiões de muitas culturas, incluindo as tradições nórdica, grega e cristã. Em 2010, o genoma da fruta foi descodificado, levando a uma nova compreensão no controle de doenças e na reprodução seletiva durante a produção da maçã.",
				"Plantae", "2");

		ObservableList<Cultura> list = FXCollections.observableArrayList(c1, c2);
		return list;
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
		scaleTransition.autoReverseProperty().set(true);
		scaleTransition.play();
	}
}
