package application.controllers.popups;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.controllers.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class FXMLPopUpShellController extends FXMLController implements Initializable {

	private Connector connector = null;

	private double sceneXOffset = 0;
	private double sceneYOffset = 0;

	private String title_name;

	@FXML
	private AnchorPane popup_shell;
	@FXML
	private AnchorPane root;
	@FXML
	private AnchorPane top;
	@FXML
	private Label title_name_label;
	@FXML
	public AnchorPane display;

	public FXMLPopUpShellController(Connector connector, String title_name) {
		this.connector = connector;
		this.title_name = title_name;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		doClips();
		title_name_label.setText(title_name);
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
	}

	@FXML
	public void close(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

	@SuppressWarnings("static-access")
	public void setDisplay(String fxml_name, FXMLLoader fxml_loader, FXMLController fxml_controller)
			throws IOException {
		fxml_loader.setController(fxml_controller);
		Parent display_node = fxml_loader.load();
		display_node.getStylesheets()
				.add(getClass().getResource("/application/styles/popups/FXML" + fxml_name + ".css").toExternalForm());
		display.getChildren().add(display_node);
		display.setBottomAnchor(display_node, 0.0);
		display.setLeftAnchor(display_node, 0.0);
		display.setRightAnchor(display_node, 0.0);
		display.setTopAnchor(display_node, 0.0);
	}

	public Connector getConnector() {
		return connector;
	}

}
