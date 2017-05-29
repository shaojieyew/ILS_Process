package util;

import java.io.File;
import java.nio.file.Paths;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FolderChooser {
	public static File show(Stage stage, String title, String initialLocation){
		File init = null;
		if(initialLocation!=null)
			init = new File(initialLocation);
		if(!init.exists()){
			init = new File(Paths.get("").toAbsolutePath().toString());
		}
		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setInitialDirectory(init);
		fileChooser.setTitle(title);
		return fileChooser.showDialog(stage);
	}
}
