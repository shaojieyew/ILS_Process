package report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class ReportCurve  implements  Comparable<ReportCurve> {
	public String name;
	public String curve;
	public String color;
    private static final String FILE_NAME = Paths.get("").toAbsolutePath().toString()+"\\curveDB.txt";
   
    public ReportCurve(){}
    public ReportCurve(String name, String curve, String color) {
		super();
		this.name = name;
		this.curve = curve;
		this.color = color;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCurve() {
		return curve;
	}
	public void setCurve(String curve) {
		this.curve = curve;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	public static void newCurve(String name, String curve, String color){
		List<ReportCurve> list =  getAll();
		if(list!=null&&list.size()>0){
			for(int i =0;i<list.size();i++){
				if(list.get(i).getName().equals(name)){
					list.remove(i);
					break;
				}
			}
		}
		if(list==null){
			list =new ArrayList<ReportCurve>();
		}
        list.add(new ReportCurve(name,  curve,  color));
        Collections.sort(list);
        
        final StringWriter sw =new StringWriter();
        final ObjectMapper mapper = new ObjectMapper();
        try {
			mapper.writeValue(new File(FILE_NAME), list);
	        sw.close(); 
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static List<ReportCurve> getAll(){
    	ObjectMapper mapper = new ObjectMapper();
    
    		File f = new File(FILE_NAME);
    		if(f.exists()){
    			try {
    				List<ReportCurve> myObjects = mapper.readValue(new File(FILE_NAME), new TypeReference<List<ReportCurve>>(){});
    	    		return myObjects;
    	    	//	Collections.sort(myObjects);
    	    	} catch (JsonParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (JsonMappingException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}	
		return null;
    }

    
    public static void deleteCurve(String name){
    	List<ReportCurve> list =  getAll();
		if(list!=null&&list.size()>0){
			for(int i =0;i<list.size();i++){
				if(list.get(i).getName().equals(name)){
					list.remove(i);
					break;
				}
			}
		}
		if(list==null){
			list =new ArrayList<ReportCurve>();
		}
    
        final StringWriter sw =new StringWriter();
        final ObjectMapper mapper = new ObjectMapper();
        try {
			mapper.writeValue(new File(FILE_NAME), list);
	        sw.close(); 
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	@Override
	public int compareTo(ReportCurve b) {
		 return this.name.compareTo(b.name);
	}
	public static ReportCurve getCurve(String name) {
		List<ReportCurve> list =  getAll();
		if(list!=null&&list.size()>0){
			for(int i =0;i<list.size();i++){
				if(list.get(i).getName().equals(name)){
					return list.get(i);
				}
			}
		}
		return null;
	}
   
}