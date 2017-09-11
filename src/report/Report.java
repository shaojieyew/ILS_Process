package report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import application.configurable.InputConfiguration;
import javafx.beans.property.SimpleStringProperty;
import util.FileUtility;

/*
 * Entity Class for ILS Report 
 */
public class Report extends ReportObservable{
	public static  final  String STATUS_NOT_PROCESSED = "Ready to Process";
	public static  final  String STATUS_IN_PROCESSING = "In Process";
	public static  final  String STATUS_COMPLETED = "Completed";
	public static  final  String STATUS_FAILED = "Failed";
	public static  final  String STATUS_NOT_FOUND = "Not Found";
	
	private final SimpleStringProperty fileName = new SimpleStringProperty("");
	private final SimpleStringProperty fileType = new SimpleStringProperty("");
	private final SimpleStringProperty path = new SimpleStringProperty("");
	private final SimpleStringProperty status = new SimpleStringProperty("");
	private String author_name;
	private List<AttributeIndex> attributes = new ArrayList<AttributeIndex>();

	public Report(String author,ArrayList<AttributeIndex> att,  String status) {
		author_name= author;
		attributes = att;
		this.status.set(status);
    }
	
	public Report(String fName,String fType, String fpath) {
        fileName.set(fName);
    	fileType.set(fType);
    	path.set(fpath);
		this.status.set(STATUS_NOT_PROCESSED);
    }
	
	public AttributeIndex getAttributeIndexByAttribute(String arg){
		if(arg==null)
			return null;
		AttributeIndex attribute = null;
		for(AttributeIndex a: attributes){
			if(a.getAttribute().equals(arg)){
					attribute=a;
					break;
			}
		}
		if(attribute==null){
			attribute = new AttributeIndex(arg,0);
			attributes.add(attribute);
		}
		return attribute;
	}
	
	
	public String getFileName() {
        return fileName.get();
    }
 
    public void setFileName(String fName) {
        fileName.set(fName);
    }
	public String getFileType() {
        return fileType.get();
    }
 
    public void setFileType(String fType) {
    	fileType.set(fType);
    }
	public String getPath() {
        return path.get();
    }
 
    public void setPath(String fpath) {
    	path.set(fpath);
    }

    public String getAuthor_name() {
		return author_name;
	}


	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}

	public List<AttributeIndex> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AttributeIndex> attributes) {
		this.attributes = attributes;
	}

	public String getStatus() {
		return status.get();
	}
	
	public void setStatus(String fstatus) {
		this.status.set(fstatus);
		super.notifyChange();
	}
	
	public static ArrayList<Report> findAllReport(String inputDirectory, String fileType[]) {
		ArrayList<Report> reports = new ArrayList<Report>();
		File[] files = FileUtility.getListOfFile(inputDirectory);
		if(files!=null){
		    for (final File fileEntry : files) {
		    	String fileType1 = FileUtility.getFileExtension(fileEntry);
		    	for(String type : fileType){
		    		if(fileType1.equals(type)){
		    	      	reports.add(new Report(fileEntry.getName(),fileType1,fileEntry.getPath()));
		    			break;
		    		}
		    	}
		    }
		}
	    return reports;
	}
	
	public boolean validateData(){
		boolean fail= false;
		if(this.getAuthor_name()==null||this.getAuthor_name().length()==0){
			 fail=true;
			//report.setAuthor_name(report.getFileName());
		} 
		for(int i =0;i<AttributeIndex.LEFT_ILS_INDEX.length;i++){
			AttributeIndex leftIndex =this.getAttributeIndexByAttribute(AttributeIndex.LEFT_ILS_INDEX[i]);
			AttributeIndex rightIndex =this.getAttributeIndexByAttribute(AttributeIndex.RIGHT_ILS_INDEX[i]);
			int leftIndexValue=0,rightIndexValue=0;
			if(leftIndex!=null){
				leftIndexValue = leftIndex.getIndex();
			}
			if(rightIndex!=null){
				rightIndexValue = rightIndex.getIndex();
			}
			
			if(leftIndexValue==0&&rightIndexValue==0){
				 fail=true;
				 break;
			}
			if(leftIndexValue!=0&&rightIndexValue!=0){
				 fail=true;
				 break;
			}
			if(leftIndexValue!=0){
				if(leftIndexValue%2==0||leftIndexValue<0||leftIndexValue>11){
					 fail=true;
					 break;
				}
			}
			if(rightIndexValue!=0){
				if(rightIndexValue%2==0||rightIndexValue<0||rightIndexValue>11){
					 fail=true;
					 break;
				}
			}
		}
		return !fail;
	}
	
	public boolean validateFile(){
		boolean no_name=false;
		if(this.getAuthor_name()==null||this.getAuthor_name().length()==0){
			no_name=true;
		}
		int totalIndex = 0;
		for(AttributeIndex ai : this.getAttributes()){
			totalIndex = totalIndex+ai.getIndex();
		}
		if(totalIndex==0 && no_name){
			return false;
		}
		return true;
	}
}