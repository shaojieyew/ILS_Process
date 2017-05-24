package reportProcessor.extraction;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*Method for extracting text data from PDF*/

public class DataExtractHTML extends DataExtract{

	public DataExtractHTML(File f) {
		super(f);
	}

	@Override
	public void processFile() {
		File file = getFile();
		String text="";
		text = getTextFromHTML(file);
		text = text.replace('\u00A0',' ');  //replace space that is not whitespace; U00A0 to U0020
		setText(text);
	}

	private String getTextFromHTML(File file) {
		String str="";
		try {
			 str = FileUtils.readFileToString(file, "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result="";
		String name = Jsoup.parse(str).getElementById("result-summary").toString();
		name = Jsoup.parse(name).text();
		Elements attributes = Jsoup.parse(str).getElementsByClass("summary-list").get(0).getAllElements();
		
		result=result+name;
		for(Element attribute:attributes){
			result=result+System.lineSeparator()+Jsoup.parse(attribute.toString()).text();
		}
		return result;
	}

	@Override
	public void reProcessFile() {
	}
}
