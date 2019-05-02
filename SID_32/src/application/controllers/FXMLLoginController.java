package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

public class FXMLLoginController extends FXMLController implements Initializable {

	private FXMLShellController fxmlShellController = null;
	
	@FXML
	private PasswordField password_hide_field;
	@FXML
	private ToggleButton password_viewer_btn;
	@FXML
	private TextField password_show_field;

	public FXMLLoginController(FXMLShellController fxmlShellController) {
		setFXMLShellController(fxmlShellController);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addPasswordViewerProperty();
	}

	private void addPasswordViewerProperty() {
		password_show_field.textProperty().bindBidirectional(password_hide_field.textProperty());
		password_show_field.managedProperty().bind(password_viewer_btn.selectedProperty());
		password_show_field.visibleProperty().bind(password_viewer_btn.selectedProperty());
		password_hide_field.managedProperty().bind(password_viewer_btn.selectedProperty().not());
		password_hide_field.visibleProperty().bind(password_viewer_btn.selectedProperty().not());
	}

	@FXML
	public void login(ActionEvent event) throws IOException {
		FXMLLoader main_loader = new FXMLLoader(getClass().getResource("/application/views/FXMLMain.fxml"));
		FXMLMainController main_controller = new FXMLMainController(fxmlShellController);
		fxmlShellController.setDisplay("Main", main_loader, main_controller, true);
	}

	public FXMLShellController getFXMLShellController() {
		return fxmlShellController;
	}

	public void setFXMLShellController(FXMLShellController fxmlShellController) {
		this.fxmlShellController = fxmlShellController;
	}

}
