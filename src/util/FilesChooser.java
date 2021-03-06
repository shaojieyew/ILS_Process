package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import reportSummary.ReportSummaryExcelLayout;
import javafx.stage.Stage;

public class FilesChooser {
	public static final ExtensionFilter FORMAT_EXCEL = new FileChooser.ExtensionFilter("Excel (.xlsx)", "*.xlsx");
	public static final ExtensionFilter FORMAT_JPG = new FileChooser.ExtensionFilter("Image (.jpg)", "*.jpg");
	public static final ExtensionFilter FORMAT_PNG = new FileChooser.ExtensionFilter("Image (.png)", "*.png");
	
	public static File show(Stage stage, String title, String initialLocation, ExtensionFilter filters[]){
		File init = null;
		if(initialLocation!=null)
			init = new File(initialLocation);
		if(!init.exists()){
			init = new File(Paths.get("").toAbsolutePath().toString());
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(init);
		fileChooser.setTitle(title);
	    fileChooser.getExtensionFilters().addAll(filters);
		return fileChooser.showOpenDialog(stage);
	}
	
	public static File save(Stage stage, String title, String initialLocation, ExtensionFilter filters[]){
		File init = null;
		if(initialLocation!=null)
			init = new File(initialLocation);
		if(!init.exists()){
			init = new File(Paths.get("").toAbsolutePath().toString());
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(init);
		fileChooser.setTitle(title);
	    fileChooser.getExtensionFilters().addAll(filters);
		return fileChooser.showSaveDialog(stage);
	}
}
