package reportProcessor.extraction;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


import application.configurable.AppProperty;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import util.OpenCVUtility;
import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;

public class OCRProcessor {
	public static final boolean DEBUG = false;
	public static int count=0;
	   
	private Tesseract instance;
	public OCRProcessor(){
        String jvmBit = System.getProperty("sun.arch.data.model");
        if(jvmBit.equals("64")){
            System.load(Paths.get("").toAbsolutePath().toString()+"\\opencv\\x64\\opencv_java320.dll");
        }
        if(jvmBit.equals("32")){
            System.load(Paths.get("").toAbsolutePath().toString()+"\\opencv\\x86\\opencv_java320.dll");
        }

        instance = new Tesseract();
        instance.setDatapath(Paths.get("").toAbsolutePath().toString()+"\\tessdata");
        instance.setLanguage("eng");
	}
	
	public String doOcr(BufferedImage image){
		String str = ocrImage(image);
		return str;
	}

    public String ocrImage(BufferedImage image){
    	BufferedImage[] bim = {image};
    	return ocrImage(bim);
    }


	public String ocrImage(List<BufferedImage> bims) {
		BufferedImage[] bim = new BufferedImage[bims.size()];
		bim = bims.toArray(bim);
		return ocrImage(bim);
	}
    
    public String ocrImage(BufferedImage[] images)  throws OutOfMemoryError{
    	if(images==null||images.length==0){
    		return "";
    	}
         String result = "";
       	  for(int i =0;i<images.length;i++){
	          try {
	             // Mat source = OpenCVUtility.img2Mat(images[i]);
	        	  images[i] = ocrPreProcessing(images[i]);
	        	  if(images[i]!=null){
					  result =result+ instance.doOCR(images[i]);
	        	  }
			  } catch (TesseractException e) {
						e.printStackTrace();
			  }
       	  }
		return result;
   }

    
    public BufferedImage ocrPreProcessing(BufferedImage image) throws OutOfMemoryError{
        //float owidth = image.getWidth();
        //float oheight = image.getHeight();
        image = removeUncessarySpace(image);
        /*
        float cwidth = image.getWidth();
        float cheight = image.getHeight();
        int shrinkRatio=1;
        if((owidth-cwidth)>(oheight-cheight)){
        	shrinkRatio= (int) (owidth/(cwidth));
        }else{
        	shrinkRatio= (int) (oheight/(cheight));
        }
        */
        float width = image.getWidth();
        float height = image.getHeight();
        float ratio = 1;
    	//if(height<3000){
      //  	ratio = 3000f/height;
    	//}else{
            if(width<3500){
            	ratio = 3500f/width;
            }
    //	}
        
        if(width>3500){
        	ratio = 3500f/width;
        }
        int newW = (int) (ratio*width);
        int newH = (int) (ratio*height);

        if(ratio!=1){
        	BufferedImage newImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        	Graphics g = newImage.createGraphics();
        	g.drawImage(image, 0, 0, newW, newH, null);
        	g.dispose();
        	//Imgproc.resize(source, source, new Size(newW,newH));
        	image = newImage;
        }
    	Mat source= OpenCVUtility.img2Mat(image);
        int blurMat =3;
        int threshold_val = 195;
        blurMat = (int) ((Math.round(ratio+0.5))*2+1);
        if(blurMat<3){
        	blurMat=3;
        }
        if(ratio>1){
        	threshold_val=threshold_val+(int) ((ratio-0.9)*25);
        	if(threshold_val>220)
        		threshold_val=220;
        }

        //System.out.println(ratio+":"+blurMat);
        if(source.channels()>1)
            Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(source, source, new Size(blurMat, blurMat), 0);
        Imgproc.threshold(source, source, threshold_val, 255, Imgproc.THRESH_BINARY);
        Imgproc.GaussianBlur(source, source, new Size(3, 3), 0);
        Imgproc.threshold(source, source, 0, 255, Imgproc.THRESH_OTSU);
        Imgproc.blur(source, source, new Size(3, 3));
        
        /*
        int blurMat = (int) ((Math.round(ratio+0.1))*2+1);
        if(blurMat<3){
        	blurMat=3;
        }
        if(source.channels()>1)
            Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(source, source, new Size(blurMat, blurMat), 0);
        Imgproc.threshold(source, source, 200, 255, Imgproc.THRESH_BINARY);
        Imgproc.GaussianBlur(source, source, new Size(blurMat, blurMat), 0);
        Imgproc.threshold(source, source, 0, 255, Imgproc.THRESH_OTSU);
        */
        
        /*
        int blurMat = 3;
        if(source.channels()>1)
            Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(source, source, new Size(blurMat, blurMat), 0);
        Imgproc.threshold(source, source, 220, 255, Imgproc.THRESH_BINARY);
         */
        
        /*
        if(source.channels()>1)
            Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(source, source, new Size(3, 3), 0);
        Imgproc.threshold(source, source, 220, 255, Imgproc.THRESH_BINARY);
        Imgproc.GaussianBlur(source, source, new Size(3, 3), 0);
        Imgproc.threshold(source, source, 0, 255, Imgproc.THRESH_OTSU);
        */
        
        
       /*
        if(source.channels()>1)
            Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);
        Mat kernel = Mat.ones(3,3, CvType.CV_8S);
        Imgproc.threshold(source, source, 220, 255, Imgproc.THRESH_BINARY);
        Imgproc.GaussianBlur(source, source, new Size(3, 3), 0);
         Imgproc.threshold(source, source, 0, 255, Imgproc.THRESH_OTSU);
        */
        
        
        
        /*
        if(source.channels()>1)
            Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);
        Mat kernel = Mat.ones(3,3, CvType.CV_8S);
        Imgproc.GaussianBlur(source, source, new Size(3, 3), 0);
        Imgproc.threshold(source, source, 220, 255, Imgproc.THRESH_BINARY);
        Imgproc.morphologyEx(source, source, Imgproc.MORPH_CLOSE, kernel);
        Imgproc.adaptiveThreshold(source, source, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C,  Imgproc.THRESH_BINARY, 11, 12);
         Imgproc.GaussianBlur(source, source, new Size(3, 3), 0);
         Imgproc.threshold(source, source, 0, 255, Imgproc.THRESH_OTSU);
        */

        image = OpenCVUtility.mat2Img(source);

	    if(DEBUG) {
	         count++;
	                //Imgcodecs.imwrite("C:\\Users\\YSJ laptop\\Desktop\\FYP\\ILS\\test\\saved"+count+".png",source);
	                
	         File outputfile = new File(AppProperty.getValue("output")+"\\saved("+ratio+")"+count+".png");
	            	try {
						ImageIO.write(image, "png", outputfile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    }
    	return image;
    }

    //crop out bufferedImage
	private BufferedImage removeUncessarySpace(BufferedImage image)  throws OutOfMemoryError{

	    int minX1=image.getWidth();
	    int minY1=image.getHeight();
	    int maxX2=0;
	    int maxY2=0;
        BufferedImage subImage;
        //slice process due to not enough heap size;
        int sliceX = (int) Math.ceil((float)image.getWidth()/1000f);
        int sliceY = (int) Math.ceil((float)image.getHeight()/1000f);
        double buffer = 0.1;
	    for(int i=0;i<sliceX;i++){
	    	for(int j=0;j<sliceY;j++){
	    	    List<Word> results = new ArrayList<Word>();
	    		int minX=i*(image.getWidth()/sliceX);
	    		int minY=j*(image.getHeight()/sliceY);
	    		//int w=image.getWidth()/slice;
	    		//int h=image.getHeight()/slice;

	    		minX=(int) (minX-(buffer*(image.getWidth()/sliceX)));
	    		if(minX<0){
	    			minX=0;
	    		}
	    		minY=(int) (minY-(buffer*(image.getHeight()/sliceY)));
	    		if(minY<0){
	    			minY=0;
	    		}

	    		int maxX = (int) (((i+1)*(image.getWidth()/sliceX))+(buffer*(image.getWidth()/sliceX)));
	    		int maxY = (int) (((j+1)*(image.getHeight()/sliceY))+(buffer*(image.getHeight()/sliceY)));
	    		if(maxX>image.getWidth()){
	    			maxX=image.getWidth();
	    		}
	    		if(maxY>image.getHeight()){
	    			maxY=image.getHeight();
	    		}
	    		
	    		
	    		int w = maxX-minX;
	    		int h = maxY-minY;
	    		subImage = image.getSubimage(minX,minY,w ,h );

	    	    if(DEBUG) {
		    	    File outputfile = new File(AppProperty.getValue("output")+"\\saved("+i+"-"+j+")"+".png");
		        	try {
		    			ImageIO.write(subImage, "png", outputfile);
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    		}
	    	    }
	        	results.addAll( instance.getWords(subImage, TessPageIteratorLevel.RIL_BLOCK));
	        	
	    	    for(Word result:results){
	    	    	int x1 = (int) result.getBoundingBox().getX()+minX;
	    	    	int y1 = (int) result.getBoundingBox().getY()+minY;
	    	    	int x2 = (int) (x1+result.getBoundingBox().getWidth());
	    	    	int y2 = (int) (y1+result.getBoundingBox().getHeight());
	    	    	if(x1<minX1){
	    	    		minX1=x1;
	    	    	}
	    	    	if(y1<minY1){
	    	    		minY1=y1;
	    	    	}
	    	    	if(x2>maxX2){
	    	    		maxX2=x2;
	    	    	}
	    	    	if(y2>maxY2){
	    	    		maxY2=y2;
	    	    	}
	    	    }
	    	}
	    }
	    
	    /*System.out.println("=====================================");
	    List<Word> results1 = instance.getWords(image, TessPageIteratorLevel.RIL_WORD);
	    System.out.println(results1);
	    System.out.println("=====================================");
	    results1 = instance.getWords(image, TessPageIteratorLevel.RIL_TEXTLINE);
	    System.out.println(results1);
	    System.out.println("=====================================");
	    results1 = instance.getWords(image, TessPageIteratorLevel.RIL_PARA);
	    System.out.println(results1);
	    System.out.println("=====================================");
	    results1 = instance.getWords(image, TessPageIteratorLevel.RIL_BLOCK);
	    System.out.println(results1);*/
	    
	    //System.out.println(results);
	    image = image.getSubimage(minX1, minY1, maxX2-minX1, maxY2-minY1);
        return image;
	}
}
