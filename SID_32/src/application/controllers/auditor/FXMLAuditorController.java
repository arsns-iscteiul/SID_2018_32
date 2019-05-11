package application.controllers.auditor;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.controllers.FXMLController;
import application.controllers.FXMLLoginController;
import application.controllers.FXMLShellController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

public class FXMLAuditorController extends FXMLController implements Initializable {

	private FXMLShellController fxmlShellController = null;
	private Connector connector = null;

	LinkedList<ListView<String>> list_views = new LinkedList<>();
	private ObservableList<String> tables_observable_list = FXCollections.observableArrayList();

	@FXML
	private ChoiceBox<String> tables_choice_box;
	@FXML
	private TableView<String> table_view;
	@FXML
	private HBox tables;

	public FXMLAuditorController(FXMLShellController fxmlShellController, Connector connector) {
		this.fxmlShellController = fxmlShellController;
		this.connector = connector;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			tables_observable_list.addAll(connector.getAllLogTables());
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
		tables.getChildren().clear();
		try {
			LinkedList<String>[] array = connector
					.allTableData(tables_choice_box.getSelectionModel().getSelectedItem());
			for (int i = 0; i != array.length - 1; i += 1) {

				ObservableList<String> ol1 = FXCollections.observableArrayList(array[i]);
				ListView<String> lv1 = new ListView<>();
				lv1.getItems().addAll(ol1);
				tables.getChildren().add(lv1);
				list_views.add(lv1);

				bindSelectors(lv1);
				bindScrollBars(lv1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void bindScrollBars(ListView<String> lv1) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (!list_views.isEmpty()) {
					for (ListView<String> lv : list_views) {
						if (lv1.equals(lv)) {
							return;
						}
						Node n1 = lv1.lookup(".scroll-bar");
						if (n1 instanceof ScrollBar) {
							ScrollBar bar1 = (ScrollBar) n1;
							if (bar1.getOrientation().equals(Orientation.VERTICAL)) {
								Node n2 = lv.lookup(".scroll-bar");
								if (n2 instanceof ScrollBar) {
									ScrollBar bar2 = (ScrollBar) n2;
									if (bar1.getOrientation().equals(Orientation.VERTICAL)) {
										bar1.valueProperty().bindBidirectional(bar2.valueProperty());
									}
								}
							}
						}
					}
				}
			}
		});
	}

	private void bindSelectors(ListView<String> lv1) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				lv1.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
						if (!list_views.isEmpty()) {
							for (ListView<String> lv : list_views) {
								if (lv1.equals(lv)) {
									break;
								}
								lv.getSelectionModel().select(lv1.getSelectionModel().getSelectedIndex());
							}
						}
					}
				});

			}
		});
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
