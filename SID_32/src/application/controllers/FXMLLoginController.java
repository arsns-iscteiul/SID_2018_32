package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import main.BD_GUI_Connector;

public class FXMLLoginController extends FXMLController implements Initializable {

	private FXMLShellController fxmlShellController = null;
	private BD_GUI_Connector bd_gui_connector = null;

	@FXML
	private TextField email_field;
	@FXML
	private PasswordField password_hide_field;
	@FXML
	private TextField password_show_field;
	@FXML
	private ToggleButton password_viewer_btn;
	@FXML
	private Label wrong_credentials_warning_label;
	
	public FXMLLoginController(FXMLShellController fxmlShellController, BD_GUI_Connector bd_gui_connector) {
		this.fxmlShellController = fxmlShellController;
		this.bd_gui_connector = bd_gui_connector;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setDesignProprieties();
	}

	private void setDesignProprieties() {
		// password_show_field
		password_show_field.textProperty().bindBidirectional(password_hide_field.textProperty());
		password_show_field.managedProperty().bind(password_viewer_btn.selectedProperty());
		password_show_field.visibleProperty().bind(password_viewer_btn.selectedProperty());
		password_hide_field.managedProperty().bind(password_viewer_btn.selectedProperty().not());
		password_hide_field.visibleProperty().bind(password_viewer_btn.selectedProperty().not());
		// wrong_credentials_warning_label
		wrong_credentials_warning_label.setManaged(false);
		wrong_credentials_warning_label.setVisible(false);
	}

	@FXML
	public void login(ActionEvent event) throws IOException {
		try {
			if (email_field.getText().equals("test")) {
				FXMLLoader main_loader = new FXMLLoader(getClass().getResource("/application/views/FXMLMain.fxml"));
				FXMLMainController main_controller = new FXMLMainController(fxmlShellController, bd_gui_connector);
				fxmlShellController.setDisplay("Main", main_loader, main_controller, true);
			} else {
				bd_gui_connector.login(email_field.getText(), password_hide_field.getText());
				FXMLLoader main_loader = new FXMLLoader(getClass().getResource("/application/views/FXMLMain.fxml"));
				FXMLMainController main_controller = new FXMLMainController(fxmlShellController, bd_gui_connector);
				fxmlShellController.setDisplay("Main", main_loader, main_controller, true);
			}
		} catch (SQLException e) {
			showWrongCredentialsWarning();
			System.out.println(
					"Unable to establish a connection with the database! - Check credentials/Internet Connection");
		}
	}

	private void showWrongCredentialsWarning() {
		wrong_credentials_warning_label.setManaged(true);
		wrong_credentials_warning_label.setVisible(true);
	}

	public FXMLShellController getFXMLShellController() {
		return fxmlShellController;
	}

	public BD_GUI_Connector getBDGUIConnector() {
		return bd_gui_connector;
	}

}
