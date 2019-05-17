package application.controllers.popups;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import application.connector.Connector;
import application.connector.objects.VariavelMedida;
import application.controllers.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLPopUpEditVariableMesuredController extends FXMLController implements Initializable {
	private Connector connector = null;
	private String mesured_variable_id;

	@FXML
	private Label variable_image_view_label;
	@FXML
	private TextField red_up_field;
	@FXML
	private TextField orange_up_field;
	@FXML
	private TextField orange_down_field;
	@FXML
	private TextField red_down_field;

	public FXMLPopUpEditVariableMesuredController(Connector connector, String mesured_variable_id) {
		this.connector = connector;
		this.mesured_variable_id = mesured_variable_id;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			for (VariavelMedida vm : connector.getVariaveisMedidas()) {
				if (vm.getVariavel_medida_id().equalsIgnoreCase(mesured_variable_id)) {
					red_up_field.setText(vm.getLimiteSuperiorVermelho());
					orange_up_field.setText(vm.getLimiteSuperiorLaranja());
					orange_down_field.setText(vm.getLimiteInferiorLaranja());
					red_down_field.setText(vm.getLimiteInferiorVermelho());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void updateUserInfo(ActionEvent event) {
		try {
			connector.updateVariableMesuredInfo(Integer.parseInt(mesured_variable_id),
					Float.parseFloat(orange_up_field.getText()), Float.parseFloat(orange_down_field.getText()),
					Float.parseFloat(red_up_field.getText()), Float.parseFloat(red_down_field.getText()));
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		} finally {
			close(event);
		}
	}

	@FXML
	public void close(ActionEvent event) {
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.close();
	}

}
