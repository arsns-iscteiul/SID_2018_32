package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.connector.Connector;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class FXMLShellController extends FXMLController implements Initializable {

	private Connector connector = null;

	private double stageX = 0;
	private double stageY = 0;
	private double sceneXOffset = 0;
	private double sceneYOffset = 0;
	private double stageWidth = 0;
	private double stageHeight = 0;

	@FXML
	private AnchorPane shell;
	@FXML
	private AnchorPane root;
	@FXML
	private AnchorPane top;
	@FXML
	private Button close_btn;
	@FXML
	private ToggleButton maximize_btn;
	@FXML
	public AnchorPane display;

	public FXMLShellController(Connector connector) {
		this.connector = connector;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		doClips();
		FXMLLoader login_loader = new FXMLLoader(getClass().getResource("/application/views/FXMLLogin.fxml"));
		FXMLLoginController login_controller = new FXMLLoginController(this, connector);
		try {
			setDisplay("Login", login_loader, login_controller, true);
		} catch (IOException e) {
			System.out.println("Unable to load Login");
			e.printStackTrace();
		}
	}

	private void doClips() {
		doRootClip();
		doTopClip();

	}

	private void doRootClip() {
		Rectangle root_clip = new Rectangle();
		root_clip.widthProperty().bind(root.widthProperty());
		root_clip.heightProperty().bind(root.heightProperty());
		root_clip.setArcWidth(10);
		root_clip.setArcHeight(10);
		root.setClip(root_clip);
	}

	private void doTopClip() {
		Rectangle top_clip = new Rectangle();
		top_clip.widthProperty().bind(top.widthProperty());
		top_clip.heightProperty().bind(top.heightProperty());
		top.setClip(top_clip);
		top.toFront();
	}

	@FXML
	private void setOnMousePressed_DragableStage(MouseEvent event) {
		sceneXOffset = event.getSceneX();
		sceneYOffset = event.getSceneY();
	}

	@FXML
	private void setOnMouseDragged_DragableStage(MouseEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setX(event.getScreenX() - sceneXOffset);
		stage.setY(event.getScreenY() - sceneYOffset);
		if (maximize_btn.isSelected()) {
			stage.setWidth(stageWidth);
			stage.setHeight(stageHeight);
			maximize_btn.setSelected(false);
		}
		setNotMaximizedProprieties();
	}

	@FXML
	public void close() {
		Platform.exit();
	}

	@FXML
	public void maximize(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		if (maximize_btn.isSelected()) {
			Screen screen = Screen.getPrimary();
			Rectangle2D bounds = screen.getVisualBounds();
			stageWidth = stage.getWidth();
			stageHeight = stage.getHeight();
			stageX = stage.getX();
			stageY = stage.getY();
			stage.setX(bounds.getMinX());
			stage.setY(bounds.getMinY());
			stage.setWidth(bounds.getWidth());
			stage.setHeight(bounds.getHeight());
			setMaximizedProprieties();
		} else {
			stage.setWidth(stageWidth);
			stage.setHeight(stageHeight);
			stage.setX(stageX);
			stage.setY(stageY);
			setNotMaximizedProprieties();
		}

	}

	@FXML
	public void minimize(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setIconified(true);
		setNotMaximizedProprieties();
	}

	@FXML
	public void setMaximizedProprieties() {
		shell.setId("shell_maximized");
		root.setClip(null);
	}

	@FXML
	public void setNotMaximizedProprieties() {
		shell.setId("shell");
		doRootClip();
	}

	public AnchorPane getDisplay() {
		return display;
	}

	@SuppressWarnings("static-access")
	public void setDisplay(String fxml_name, FXMLLoader fxml_loader, FXMLController fxml_controller, boolean clear)
			throws IOException {
		fxml_loader.setController(fxml_controller);
		Parent display_node = fxml_loader.load();
		if (fxml_name.equals("Auditor")) {
			display_node.getStylesheets().add(
					getClass().getResource("/application/styles/auditor/FXML" + fxml_name + ".css").toExternalForm());
		} else if (fxml_name.equals("Admin")) {
			display_node.getStylesheets().add(
					getClass().getResource("/application/styles/admin/FXML" + fxml_name + ".css").toExternalForm());
		} else {
			display_node.getStylesheets()
					.add(getClass().getResource("/application/styles/FXML" + fxml_name + ".css").toExternalForm());

		}
		if (clear)
			display.getChildren().clear();
		display.getChildren().add(display_node);
		display.setBottomAnchor(display_node, 0.0);
		display.setLeftAnchor(display_node, 0.0);
		display.setRightAnchor(display_node, 0.0);
		display.setTopAnchor(display_node, 0.0);
	}

}
