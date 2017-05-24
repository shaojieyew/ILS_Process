package application.gui.controller;

import javafx.stage.Stage;

/*
 * FXMLController is common interface for the Controllers for FXML  
 */

public  class FXMLController {
	private Stage stage;
	public void setStageAndSetupListeners(Stage stage) {
		this.stage = stage;
	}
	public Stage getStage() {
		return stage;
	}
}
