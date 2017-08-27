package reportProcessor.extraction;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import reportProcessor.analysis.ReportDataReader;

/*Method for extracting text data from PDF*/

public class DataExtractPDF extends DataExtract {

	public DataExtractPDF(File f) {
		super(f);
	}

	@Override
	public void processFile() throws OutOfMemoryError {
		File file = getFile();
		String text="";
		boolean rerunByImageProcessing = false;
		
		//get text from pdf
		text = getTextFromPDF(file);
		text = text.replace('\u00A0',' ');  //replace space that is not whitespace; U00A0 to U0020
		if(text.replaceAll("\\s+","").length()==0){	//check if it contain any character other than whitespace
			//if the text contain only white space, rerun get text by image processing
			rerunByImageProcessing=true;
		}

		
		//check if text contain any of the ILS attribute, if not rerun get text by image processing
		if((text.indexOf(ReportDataReader.KEYWORD_ILS_ACTIVE)==-1&&
				text.indexOf(ReportDataReader.KEYWORD_ILS_REFLECTIVE)==-1&&
				text.indexOf(ReportDataReader.KEYWORD_ILS_SENSING)==-1&&
				text.indexOf(ReportDataReader.KEYWORD_ILS_INTUITIVE)==-1&&
				text.indexOf(ReportDataReader.KEYWORD_ILS_VISUAL)==-1&&
				text.indexOf(ReportDataReader.KEYWORD_ILS_VERBAL)==-1&&
				text.indexOf(ReportDataReader.KEYWORD_ILS_SEQUENTIAL)==-1&&
				text.indexOf(ReportDataReader.KEYWORD_ILS_GLOBAL)==-1)||text.indexOf(ReportDataReader.KEYWORD_QUESTIONNAIRE)==-1){
			rerunByImageProcessing=true;
		}

		//get text by image processing using OCR
		if(rerunByImageProcessing){
			BufferedImage[] bim=pdfToBufferimage(file);
		    OCRProcessor ocr = new OCRProcessor();
		    try{
		    	text = ocr.ocrImage(bim);
		    }catch(OutOfMemoryError e){
		    	
		    }
		}
		setText(text);
	}
	
	//get all images in pdf in bufferedimages
	public List<BufferedImage> extractPDFImages() {
	    List<BufferedImage> bimgs = null;
		try {
	    PDDocument document;
			document = PDDocument.load(getFile());
	    PDPageTree list = document.getPages();
	    for (PDPage page : list) {
	        PDResources pdResources = page.getResources();
	        for (COSName c : pdResources.getXObjectNames()) {
	            PDXObject o = pdResources.getXObject(c);
	            if (o instanceof org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject) {
	            	if(bimgs==null)
	            		bimgs=new ArrayList<BufferedImage>();
	                BufferedImage bimg = ((org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject)o).getImage();
	                bimgs.add(bimg);
	            }
	        }
	    }

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return bimgs;
	}
	
	//get text from pdf using pdfBox's stripper
	public static String getTextFromPDF(File input){
		if(!input.exists()){
			return "";
		}
    	PDDocument pd;
    	try {
    	         pd = PDDocument.load(input);
    	         PDFTextStripper stripper = new PDFTextStripper();
    	         if(pd.getNumberOfPages()<=0)
    	        	 return "";
    	         stripper.setStartPage(1); 
    	         stripper.setEndPage(pd.getNumberOfPages()); 
    	         String text = stripper.getText(pd);
    	         if (pd != null) {
    	             pd.close();
    	         }
    	         return text;
    	} catch (Exception e){
    	         e.printStackTrace();
    	}
 		return "";
    }
	
	//convert all the pdf pages to bufferedImages
    public BufferedImage[] pdfToBufferimage(File file){
    	if(!file.exists()){
    		return null;
    	}
    	BufferedImage[] bi=null;
        try {
	        PDDocument document = PDDocument.load(file);
	        PDFRenderer pdfRenderer = new PDFRenderer(document);
	        bi = new BufferedImage[document.getNumberOfPages()];
	        int maxPageLimit = document.getNumberOfPages();
	        if(maxPageLimit>4){
	        	maxPageLimit=4;
	        }
	        for (int page = 0; page < maxPageLimit; ++page)
	        { 
	            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
	            bi[page]=bim;
	        }
	        document.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
        return bi;
    }

	@Override
	public void reProcessFile() throws OutOfMemoryError{
		/*
		File file = getFile();
		BufferedImage[] bim=pdfToBufferimage(file);
	    OCRProcessor ocr = new OCRProcessor();
	    setText(ocr.ocrImage(bim));
	    */
	}
}
