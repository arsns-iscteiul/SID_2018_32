package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

public class FXMLMainController extends FXMLController implements Initializable {
	
	private FXMLShellController fxmlShellController = null;
	
	public FXMLMainController(FXMLShellController fxmlShellController) {
		setFXMLShellController(fxmlShellController);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	@FXML
	public void logout(ActionEvent event) throws IOException {
		FXMLLoader login_loader = new FXMLLoader(getClass().getResource("/application/views/FXMLLogin.fxml"));
		FXMLLoginController login_controller = new FXMLLoginController(fxmlShellController);
		fxmlShellController.setDisplay("Login", login_loader, login_controller, true);
	}

	public FXMLShellController getFXMLShellController() {
		return fxmlShellController;
	}

	public void setFXMLShellController(FXMLShellController fxmlShellController) {
		this.fxmlShellController = fxmlShellController;
	}

}
