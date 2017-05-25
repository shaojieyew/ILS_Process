package util;

import java.io.File;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class FilesChooser {
	public static final ExtensionFilter FORMAT_EXCEL = new FileChooser.ExtensionFilter("Excel (.xlsx)", "*.xlsx");
	
	public static File show(Stage stage, String title, String initialLocation, ExtensionFilter filters[]){
		File init = null;
		if(initialLocation!=null)
			init = new File(initialLocation);
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(init);
		fileChooser.setTitle(title);
	    fileChooser.getExtensionFilters().addAll(filters);
		return fileChooser.showOpenDialog(stage);
	}
}
