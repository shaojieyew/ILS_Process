package reportProcessor.extraction;

import java.io.File;
/*Common interface for different DataExtract implementation for different format*/
/*Data extracted from processFile() are stored in text attribute*/
public abstract class DataExtract {

	private String text;
	
	private File file;
	public DataExtract(File f) {
		file = f;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getText() {
		return text;
	}
	public void setText(String words) {
		this.text = words;
	}

	public abstract void processFile();
	public abstract void reProcessFile();
}
