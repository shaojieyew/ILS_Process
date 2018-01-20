package application.gui.controller;

import java.util.Arrays;

import application.MainApplication;
import application.configurable.AppProperty;
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
import util.AppDialog;

public class CustomDialogSetting {


	static boolean cancel = false;
	public static boolean showCustomDialog(String title){
		cancel = false;
		 final Stage dialog = new Stage();
	        dialog.setTitle(title);
	        Button yes = new Button("Save");
	        Button no = new Button("Cancel");

	        BorderPane borderPane = new BorderPane();
	        

	        dialog.initModality(Modality.APPLICATION_MODAL);

	        
	        HBox hbox = new HBox();
	        hbox.setAlignment(Pos.CENTER_RIGHT);
	        hbox.getChildren().addAll(yes,no);
	        hbox.setSpacing(10);
	        VBox vbox = new VBox();
	        
	        HBox hbox1 = new HBox();
	        hbox1.setAlignment(Pos.TOP_RIGHT);
	    	int multi_thread_count = 2;
			try{
				multi_thread_count = Integer.parseInt(AppProperty.getValue("multi_thread"));
			}catch(Exception ex){
				
			}
	        TextField textField_multi_thread = new TextField(Integer.toString(multi_thread_count));
	        textField_multi_thread.setFont(Font.font(null, FontWeight.NORMAL, 14));
	        Label label_multi_thread = new Label("Number of process in parallel: ");
	        label_multi_thread.setFont(Font.font(null, FontWeight.NORMAL, 14));
	        hbox1.getChildren().addAll(label_multi_thread,textField_multi_thread);
	        hbox1.setSpacing(10);
	        
	        vbox.getChildren().addAll(hbox1,hbox);
	        yes.addEventHandler(MouseEvent.MOUSE_CLICKED,
	                new EventHandler<MouseEvent>() {
	                    @Override
	                    public void handle(MouseEvent e) {
	                    	if(textField_multi_thread.getText().length()>0&&textField_multi_thread.getText().matches("-?\\d+(\\.\\d+)?")){
	                    		int maxVal = 4;
	                    		String val = textField_multi_thread.getText();
	                    		if(val.equals("0")){
	                    			val="1";
	                    		}else{
	                    			if(Integer.parseInt(val)>maxVal){
		                    			val=Integer.toString(maxVal);
		                    		}
	                    		}
	                    		AppProperty.setValue("multi_thread", val);
	                    		dialog.close();
	                    	}
	                    	else
	                    		AppDialog.alert("Invalid input", "Invalid input");
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
	        
	        vbox.setSpacing(10);
	        borderPane.setCenter(vbox);
	        borderPane.setPadding(new Insets(20));
	        Scene dialogScene = new Scene(borderPane, 450, 100);
	        dialogScene.getStylesheets().add(MainApplication.class.getResource("gui/application.css").toExternalForm());
	        dialogScene.getStylesheets().add(MainApplication.class.getResource("gui/bootstrap.css").toExternalForm());
	        dialog.setResizable(false);
	        dialog.setScene(dialogScene);
	        dialog.showAndWait();
	        if(cancel){
			       return false;
	        }else{
				return true;
	        }
	        
	}
}
