package reportProcessor.extraction;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/*Extract data from image file*/

public class DataExtractImage extends DataExtract{
	public DataExtractImage(File f) {
		super(f);
	}

	@Override
	public void processFile() throws OutOfMemoryError{
		OCRProcessor ocr = new OCRProcessor();
		BufferedImage in = null;
		BufferedImage newImage = null;
		try {
			//read image file to bufferedImage
			in = ImageIO.read(getFile());
	    	 newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);
	    	 newImage.getGraphics().drawImage(in, 0, 0, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//ocr the bufferedImage and get text
		String text = ocr.ocrImage(newImage,getFile());
		setText(text);
	}

	@Override
	public void reProcessFile() throws OutOfMemoryError{
	}

}
