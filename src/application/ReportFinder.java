package application;

import java.io.File;
import java.util.ArrayList;

import application.configurable.InputConfiguration;
import util.FileUtility;

public class ReportFinder {
	
	//get all the reports base on the input configuration from inputConfiguration
	public ArrayList<Report> findAllReport() {
		InputConfiguration inputDirectory=InputConfiguration.getInstance();
		ArrayList<Report> reports = new ArrayList<Report>();
	    for (final File fileEntry : FileUtility.getListOfFile(inputDirectory.getDirectory())) {
	    	String fileType = FileUtility.getFileExtension(fileEntry);
	    	for(String type : inputDirectory.getFileType()){
	    		if(fileType.equals(type)){
	    	      	reports.add(new Report(fileEntry.getName(),fileType,fileEntry.getPath()));
	    			break;
	    		}
	    	}
	    }
	    return reports;
	}

}
