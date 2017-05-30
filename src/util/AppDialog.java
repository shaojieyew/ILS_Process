package util;

import java.util.Optional;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

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
	public static void alert(String title,String content ){
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(title);
        a.setContentText(content);
        a.showAndWait();
	}
	
	public static int multiButtonDialog(String buttonNames[],String title, String content){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(title);
		alert.setContentText(content);
		alert.getButtonTypes().clear();
		ButtonType[] buttonTypes = new ButtonType[buttonNames.length];
		for(int i =0;i<buttonNames.length;i++){
			buttonTypes[i]=new ButtonType(buttonNames[i]);
			alert.getButtonTypes().add(buttonTypes[i]);
		}
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().add(buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		for(int i =0;i<buttonTypes.length;i++){
			if(buttonTypes[i]==result.get()){
				return i;
			}
		}
		return -1;
	}
}
