package application;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

/*
 * Entity Class for ILS Report 
 */
public class Report extends ReportObservable{
	public static  final  String STATUS_NOT_PROCESSED = "Ready";
	public static  final  String STATUS_IN_PROCESSING = "In Process";
	public static  final  String STATUS_COMPLETED = "Completed";
	public static  final  String STATUS_FAILED = "Failed";
	
	
	private final SimpleStringProperty fileName = new SimpleStringProperty("");
	private final SimpleStringProperty fileType = new SimpleStringProperty("");
	private final SimpleStringProperty path = new SimpleStringProperty("");
	private final SimpleStringProperty status = new SimpleStringProperty("");
	private String author_name;
	private List<AttributeIndex> attributes = new ArrayList<AttributeIndex>();

	public Report(String fName,String fType, String fpath) {
        fileName.set(fName);
    	fileType.set(fType);
    	path.set(fpath);
		this.status.set(STATUS_NOT_PROCESSED);
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
}