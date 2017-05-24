package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtility {
	/*
	public static final String TYPE_PDF="pdf";
	public static final String TYPE_PNG="png";
	public static final String TYPE_TIF="tif";
	public static final String TYPE_JPG="JPG";
	public static final String TYPE_JPEG="jpeg";
	public static final String TYPE_BMP="bmp";
	public static final String Type_DOCX="docx";
	*/
	
	public static File[] getListOfFile(String path){
		File folder = new File(path);
		return folder.listFiles();
	}
	
	public static String getFileExtension(File file) {
	    String name = file.getName();
	    String ext = "";
	    try {
	    	ext= name.substring(name.lastIndexOf(".") + 1);
	    	ext = ext.toLowerCase();
	    } catch (Exception e) {
	    	ext= "";
	    }
	    return ext.toLowerCase();
	}
	
	public static void copyFileUsingStream(File source, File dest) throws IOException {
	    InputStream is = null;
	    OutputStream os = null;
	    try {
	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	public static void writeWordsToText(String text,String filename){
		BufferedWriter writer = null;
		try
		{
		    writer = new BufferedWriter( new FileWriter(filename));
			String updatedWords = text.replaceAll("\\r?\\n", System.lineSeparator());
		    writer.write( updatedWords);
		}
		catch ( IOException e)
		{
		}
		finally
		{
		    try
		    {
		        if ( writer != null)
		        writer.close( );
		    }
		    catch ( IOException e)
		    {
		    }
		}
	}
}
