package application.gui.controller;

import java.util.Arrays;

import application.MainApplication;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import report.ReportsCurve;

public class CurveCustomDialog {


	static boolean cancel = false;
	public static boolean showCustomDialog(String title, String content, SummaryGUI summaryGUI, String textFieldValue){
		cancel = false;
		 final Stage dialog = new Stage();
	        dialog.setTitle(title);
	        Button yes = new Button("Save");
	        Button no = new Button("Cancel");

	        BorderPane borderPane = new BorderPane();
	        TextField textFields = new TextField(textFieldValue);
	        textFields.setFont(Font.font(null, FontWeight.NORMAL, 14));
	        Label label = new Label(content);
	        label.setFont(Font.font(null, FontWeight.NORMAL, 14));

	        dialog.initModality(Modality.APPLICATION_MODAL);

	        yes.addEventHandler(MouseEvent.MOUSE_CLICKED,
	                new EventHandler<MouseEvent>() {
	                    @Override
	                    public void handle(MouseEvent e) {
	                        dialog.close();
	                    }
	                });
	        no.addEventHandler(MouseEvent.MOUSE_CLICKED,
	                new EventHandler<MouseEvent>() {
	                    @Override
	                    public void handle(MouseEvent e) {
	                        dialog.close();
	                        cancel =true;
	                    }
	                });
	        borderPane.setCenter(summaryGUI);
	        HBox hbox = new HBox();
	        hbox.setAlignment(Pos.CENTER_RIGHT);
	        hbox.getChildren().addAll(yes,no);
	        hbox.setSpacing(10);
	        VBox vbox = new VBox();
	        vbox.getChildren().addAll(label,textFields,hbox);
	        vbox.setSpacing(10);
	        borderPane.setBottom(vbox);
	        borderPane.setPadding(new Insets(20));
	        Scene dialogScene = new Scene(borderPane, 500, 450);
	        dialogScene.getStylesheets().add(MainApplication.class.getResource("gui/application.css").toExternalForm());
	        dialogScene.getStylesheets().add(MainApplication.class.getResource("gui/bootstrap.css").toExternalForm());
		
	        dialog.setScene(dialogScene);
	        dialog.showAndWait();
	        if(cancel){
			       return false;
	        }else{
				ReportsCurve.saveProfile(textFields.getText(), Arrays.deepToString(summaryGUI.getSelectorsLoc()));
	        	return true;
	        }
	        
	}
}
