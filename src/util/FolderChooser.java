package util;

import java.io.File;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class FolderChooser {
	public static File show(Stage stage, String title, String initialLocation){
		File init = null;
		if(initialLocation!=null)
			init = new File(initialLocation);
		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setInitialDirectory(init);
		fileChooser.setTitle(title);
		return fileChooser.showDialog(stage);
	}
}
