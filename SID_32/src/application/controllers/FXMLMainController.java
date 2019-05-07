package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import main.BD_GUI_Connector;

public class FXMLMainController extends FXMLController implements Initializable {

	private FXMLShellController fxmlShellController = null;
	private BD_GUI_Connector bd_gui_connector = null;

	private ObservableList<String> cultura_observablelist = FXCollections.observableArrayList();

	@FXML
	private ListView<String> cultura_listview;

	public FXMLMainController(FXMLShellController fxmlShellController, BD_GUI_Connector bd_gui_connector) {
		this.fxmlShellController = fxmlShellController;
		this.bd_gui_connector = bd_gui_connector;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			LinkedList<String> culturas = bd_gui_connector.getTableContentNames("cultura");
			cultura_observablelist.addAll(culturas);
			cultura_listview.getItems().addAll(cultura_observablelist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

	public FXMLShellController getFXMLShellController() {
		return fxmlShellController;
	}

	public void setFXMLShellController(FXMLShellController fxmlShellController) {
		this.fxmlShellController = fxmlShellController;
	}

}
