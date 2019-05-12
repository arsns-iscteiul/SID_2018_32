package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.controllers.admin.FXMLAdminController;
import application.controllers.auditor.FXMLAuditorController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class FXMLLoginController extends FXMLController implements Initializable {

	private FXMLShellController fxmlShellController = null;
	private Connector connector = null;

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

	public FXMLLoginController(FXMLShellController fxmlShellController, Connector connector) {
		this.fxmlShellController = fxmlShellController;
		this.connector = connector;
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
			String id_investigador = connector.login("main", email_field.getText(), password_hide_field.getText());
			if (!(id_investigador == null)) {
				if (id_investigador.equals("auditor")) {
					Connector auditorConnector = new Connector();
					auditorConnector.login("auditor", email_field.getText(), password_hide_field.getText());
					FXMLLoader auditor_loader = new FXMLLoader(
							getClass().getResource("/application/views/auditor/FXMLAuditor.fxml"));
					FXMLAuditorController auditor_controller = new FXMLAuditorController(fxmlShellController, connector,
							auditorConnector);
					fxmlShellController.setDisplay("Auditor", auditor_loader, auditor_controller, true);
				} else if (id_investigador.equals("admin")) {
					FXMLLoader admin_loader = new FXMLLoader(
							getClass().getResource("/application/views/admin/FXMLAdmin.fxml"));
					FXMLAdminController admin_controller = new FXMLAdminController(fxmlShellController, connector);
					fxmlShellController.setDisplay("Admin", admin_loader, admin_controller, true);
				} else {
					FXMLLoader main_loader = new FXMLLoader(getClass().getResource("/application/views/FXMLMain.fxml"));
					FXMLMainController main_controller = new FXMLMainController(fxmlShellController, connector,
							id_investigador);
					fxmlShellController.setDisplay("Main", main_loader, main_controller, true);
				}
			} else {
				showWrongCredentialsWarning();
			}
		} catch (SQLException e) {
			showWrongCredentialsWarning();
			e.printStackTrace();
		}
	}

	private void showWrongCredentialsWarning() {
		wrong_credentials_warning_label.setManaged(true);
		wrong_credentials_warning_label.setVisible(true);
	}

	public FXMLShellController getFXMLShellController() {
		return fxmlShellController;
	}

	public Connector getBDGUIConnector() {
		return connector;
	}

}
