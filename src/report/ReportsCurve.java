package report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsCurve implements Serializable {
    private static final long serialVersionUID = 5864896800675704551L;
    private List<String> list;
    private Map<String, String> map;
    private static final String FILE_NAME = Paths.get("").toAbsolutePath().toString()+"\\profileDB.txt";

    public static void saveProfile(String key, String value){
    	ReportsCurve db = new ReportsCurve();// Read Object
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                try {
					db = (ReportsCurve) ois.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            } catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	 	Map<String, String> map = db.getMap();
        	if(map==null){
        	 	map = new HashMap<String, String>();
        	}
	        map.put(key, value);
	        db.setMap(map);
	        try {
	            // Save Object
	            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
	                oos.writeObject(db);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
    }

    public static Map<String, String>  get(){
    	Map<String, String> map = new HashMap<String, String>();
        try {

        	ReportsCurve db = new ReportsCurve();
            // Read Object
        	File f = new File(FILE_NAME);
        	if(!f.exists()){
        		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
	                oos.writeObject(db);
	            }
        	}
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                db = (ReportsCurve) ois.readObject();
            }
            if(db.getMap()!=null){
                map = db.getMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public static String  get(String key){
    	Map<String, String> map = new HashMap<String, String>();
        try {

        	ReportsCurve db = new ReportsCurve();
            // Read Object
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                db = (ReportsCurve) ois.readObject();
            }

            map = db.getMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map.get(key);
    }


    public static String  delete(String key){
    	Map<String, String> map = new HashMap<String, String>();
        try {

        	ReportsCurve db = new ReportsCurve();
            // Read Object
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                db = (ReportsCurve) ois.readObject();
            }

            map = db.getMap();
            map.remove(key);
            db.setMap(map);
	        try {
	            // Save Object
	            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
	                oos.writeObject(db);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map.get(key);
    }
    
    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}