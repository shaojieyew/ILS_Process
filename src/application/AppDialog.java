package application;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AppDialog {
	public static void criticalErrorAndExit(String title,String content ){
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(content);
        a.showAndWait();
        Platform.exit();
        System.exit(0);
	}
}
