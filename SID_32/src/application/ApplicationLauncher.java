package application;

import application.connector.Connector;
import application.controllers.FXMLShellController;
import application.support.StageResizeHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationLauncher extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Connector connector = new Connector("localhost:3307/main");

		FXMLLoader shell_loader = new FXMLLoader(getClass().getResource("/application/views/FXMLShell.fxml"));
		FXMLShellController shell_controller = new FXMLShellController(connector);
		shell_loader.setController(shell_controller);
		Parent shell = shell_loader.load();
		shell.getStylesheets().add(getClass().getResource("/application/styles/FXMLShell.css").toExternalForm());

		stage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
		Scene scene = new Scene(shell);
		scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
		stage.setScene(scene);
		StageResizeHelper.addResizeListener(stage);
		stage.show();
	}

}