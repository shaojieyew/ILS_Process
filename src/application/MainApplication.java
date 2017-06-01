package application;
	
import java.io.File;
import java.nio.file.Paths;

import application.configurable.InputConfiguration;
import application.gui.controller.FXMLController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import util.AppDialog;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


public class MainApplication extends Application {
	@Override
	public void start(Stage stage) {
		String jvmBit="";
		String opencv_lib_path= "";
        jvmBit = System.getProperty("sun.arch.data.model");
        if(jvmBit.equals("64")){
        	jvmBit="64";
        }
        if(jvmBit.equals("32")){
        	jvmBit="86";
        }
        opencv_lib_path = Paths.get("").toAbsolutePath().toString()+"\\opencv\\x"+jvmBit+"\\opencv_java320.dll";
        
        File varTmpDir = new File(opencv_lib_path);
        boolean exists = varTmpDir.exists();
        if(exists){
            System.load(opencv_lib_path);
        }else{
        	AppDialog.criticalErrorAndExit("Missing opencv_java320.dll", "Ensure opencv_java320.dll is in \n"+opencv_lib_path);
        }
		
		try {

			//Load FXML Main.fxml and setup controller of FXML
            FXMLLoader mainLoader = new FXMLLoader();
            mainLoader.setLocation(MainApplication.class.getResource("gui/Main.fxml"));
            BorderPane mainLayout = (BorderPane) mainLoader.load();

            FXMLController controller = (FXMLController)mainLoader.getController();
            controller.setStageAndSetupListeners(stage); 
            
			//Setup CSS Style for the FXML
			Scene scene = new Scene(mainLayout);
			scene.getStylesheets().add(MainApplication.class.getResource("gui/application.css").toExternalForm());
			stage.setScene(scene);
			stage.setTitle("ILS Process App");
			//Show the stage; application window
			stage.show();
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	            @Override
	            public void handle(WindowEvent t) {
	                Platform.exit();
	                System.exit(0);
	            }
			});
			stage.focusedProperty().addListener(new ChangeListener<Boolean>()
			{
				@Override
				public void changed(ObservableValue<? extends Boolean> focused, Boolean arg1, Boolean arg2) {
					if(focused.getValue()==true){
						InputConfiguration.getInstance().setReportSummaryFile(InputConfiguration.getInstance().getReportSummaryFile());
					}
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
