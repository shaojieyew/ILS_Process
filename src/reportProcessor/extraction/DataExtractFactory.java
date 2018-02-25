package reportProcessor.extraction;

import java.io.File;

import util.FileUtility;

//This class is used for managing the creation of DataExtract instance for corresponding file type

public class DataExtractFactory {
	public static DataExtract getInstance(File f){
    	String fileType = FileUtility.getFileExtension(f);
    	DataExtract de = null;
    	switch(fileType){
		case("pdf"):
			de= new DataExtractPDF(f);
			break;
		case("png"):
		case("jpeg"):
		case("jpg"):
		case("bmp"):
		case("tiff"):
		case("tif"):
		case("gif"):
			de= new DataExtractImage(f);
			break;
		case("html"):
			de= new DataExtractHTML(f);
			break;
		case("htm"):
			de= new DataExtractHTML(f);
			break;
		default: 
			de= null;
			break;
    	}
    	return de;
	}
}
