package reportProcessor.extraction;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


import application.configurable.AppProperty;
import application.configurable.DebugClass;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.Word;
import report.Report;
import util.FileUtility;
import util.OpenCVUtility;
import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;

public class OCRProcessor extends DebugClass {
	private  File file;
	
	
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
	
	/*
	public String doOcr(BufferedImage image, Report report){
		String str = ocrImage(image,report);
		return str;
	}
*/
    public String ocrImage(BufferedImage image,File file){
    	BufferedImage[] bim = {image};
    	return ocrImage(bim,file);
    }


	public String ocrImage(List<BufferedImage> bims,File file) {
		BufferedImage[] bim = new BufferedImage[bims.size()];
		bim = bims.toArray(bim);
		return ocrImage(bim,file);
	}
    
    public String ocrImage(BufferedImage[] images,File file)  throws OutOfMemoryError{
		this.file=file;
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
    	try{
    		
	        //float owidth = image.getWidth();
	        //float oheight = image.getHeight();
	        image = removeUncessarySpace(image);

	
		    if(isDebug()) {
		                //Imgcodecs.imwrite("C:\\Users\\YSJ laptop\\Desktop\\FYP\\ILS\\test\\saved"+count+".png",source);
	
		         //File outputfile = new File(AppProperty.getValue("output")+"\\saved("+ratio+")"+count+".png");
		            	try {
		            		if(FileUtility.makeFolder(file.getParent()+"\\debug")){
			            	    File outputfile = new File(file.getParent()+"\\debug\\"+file.getName()+"_processed.png");
								ImageIO.write(image, "png", outputfile);
		            		}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    }
	    	return image;
    	}catch(CvException e){
    		throw new OutOfMemoryError("OpenCV Error: Insufficient memory");
    	}
    }

    //crop out bufferedImage
	private BufferedImage removeUncessarySpace(BufferedImage image)  throws OutOfMemoryError{
		boolean firstImageSlice = false;
	    int minX1=image.getWidth();
	    int minY1=image.getHeight();
	    int maxX2=0;
	    int maxY2=0;
	    int subImageCount = 0;
        BufferedImage subImage;
        //slice process due to not enough heap size;
        int sliceX = (int) Math.ceil((float)image.getWidth()/1000f);
        int sliceY = (int) Math.ceil((float)image.getHeight()/1000f);
        double buffer = 0.1;
        float confidence[] = {0,0,0,0};
        int lines[] = {0,0,0,0};


    	BufferedImage testImage=image;
            for(int i=0;i<sliceX;i++){
            	boolean breakout =false;
    	    	for(int j=0;j<sliceY;j++){
    	    		int minX=i*(testImage.getWidth()/sliceX);
    	    		int minY=j*(testImage.getHeight()/sliceY);

    	    		minX=(int) (minX-(buffer*(testImage.getWidth()/sliceX)));
    	    		if(minX<0){
    	    			minX=0;
    	    		}
    	    		minY=(int) (minY-(buffer*(testImage.getHeight()/sliceY)));
    	    		if(minY<0){
    	    			minY=0;
    	    		}

    	    		int maxX = (int) (((i+1)*(testImage.getWidth()/sliceX))+(buffer*(testImage.getWidth()/sliceX)));
    	    		int maxY = (int) (((j+1)*(testImage.getHeight()/sliceY))+(buffer*(testImage.getHeight()/sliceY)));
    	    		if(maxX>testImage.getWidth()){
    	    			maxX=testImage.getWidth();
    	    		}
    	    		if(maxY>testImage.getHeight()){
    	    			maxY=testImage.getHeight();
    	    		}
    	    		int w = maxX-minX;
    	    		int h = maxY-minY;
    	    		subImage = testImage.getSubimage(minX,minY,w ,h );


    	    	    List<Word> textline = instance.getWords(subImage, TessPageIteratorLevel.RIL_TEXTLINE);
    	    	}
    	    	if(breakout){
    	    		break;
    	    	}
    	    }        
        
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
	    	    if(subImageCount<2){
	    	    	if(sliceY>1){
		    	    	if(subImageCount==0&&j>=(sliceY/2)){
		    	    		continue;
		    	    	}
		    	    	if(subImageCount==1&&j<(sliceY/2)){
		    	    		continue;
		    	    	}
	    	    	}
	    	    	
		    	    List<Word> textline1 = instance.getWords(subImage, TessPageIteratorLevel.RIL_TEXTLINE);
		    	    if(!textline1.isEmpty()){
		    	        double fontsize = getFontHeight(textline1);
			    		subImageCount++;
	    	            for(int d =0;d<4;d++){
	    	    	    	BufferedImage testimg = subImage;
	    	            	if(d==0){
	    			    	    //List<Word> testFont = instance.getWords(testimg, TessPageIteratorLevel.RIL_TEXTLINE);
	    			    	    //fontsize = getFontHeight(testFont);
	    	            		testimg = preprocessImage(testimg, fontsize);
	    			    	    List<Word> textline = instance.getWords(testimg, TessPageIteratorLevel.RIL_TEXTLINE);
	        	    	    	for(Word text: textline){
	        	    	    		confidence[d] = confidence[d]+text.getConfidence();
	        	    	    		lines[d]++;
	        	    	    	}
	    	            	}
	    	            	if(d!=0){
	    	            		testimg = rotate(testimg,d);
	    			    	    List<Word> testFont = instance.getWords(testimg, TessPageIteratorLevel.RIL_TEXTLINE);
	    			    	    fontsize = getFontHeight(testFont);
	    	            		testimg = preprocessImage(testimg, fontsize);
	    	    	    	    List<Word> textlines = instance.getWords(testimg, TessPageIteratorLevel.RIL_TEXTLINE);
	        	    	    	for(Word text: textlines){
	        	    	    		confidence[d] = confidence[d]+text.getConfidence();
	        	    	    		lines[d]++;
	        	    	    	}
	    	            	}
	    	            }
		    	    }
	    	    }
	    	}
	    }


	    if(!(minX1>maxX2||minY1>maxY2)){
		    image = image.getSubimage(minX1, minY1, maxX2-minX1, maxY2-minY1);
	    }

	    System.out.println("=====================================");
        for(int i =0;i<4;i++){
    	    System.out.println((confidence[i]/(lines[i])+"%"+"   "+lines[i]));
        }
	    System.out.println("=====================================");
        int orientation = 0;

        int lowest1Count=Integer.MAX_VALUE;
        int lowest2Count=Integer.MAX_VALUE;
        int lowest1orentation=0;
        int lowest2orentation=0;
        for(int i =0;i<4;i++){
        	if(lines[i]<lowest1Count){
        		lowest2Count=lowest1Count;
        		lowest1Count=lines[i];
        		lowest2orentation=lowest1orentation;
        		lowest1orentation=i;
        	}else{
            	if(lines[i]<lowest2Count){
            		lowest2Count=lines[i];
            		lowest2orentation=i;
            	}
        	}
        }
        
        
        float orientationConfidence = 0;
        for(int j =0;j<2;j++){
        	int i=0;
        	if(j==0){
        		i=lowest1orentation;
        	}
        	if(j==1){
        		i=lowest2orentation;
        	}
        	System.out.println(i);
        	float confidenceLevel = confidence[i]/(lines[i]);
        	if(confidenceLevel>orientationConfidence){
        		orientation = i;
        		orientationConfidence = confidenceLevel;
        	}
        }
        if(orientation!=0){
        	image = rotate(image,orientation);
        }
	    
	    List<Word> results1;
	    //System.out.println("=====================================");
	    results1 = instance.getWords(image, TessPageIteratorLevel.RIL_TEXTLINE);
	    //System.out.println(results1);


	    /*
	    System.out.println("=====================================");
	    results1 = instance.getWords(image, TessPageIteratorLevel.RIL_WORD);
	    System.out.println(results1);
	    System.out.println("=====================================");
	    results1 = instance.getWords(image, TessPageIteratorLevel.RIL_TEXTLINE);
	    System.out.println(results1);
	    System.out.println("=====================================");
	    results1 = instance.getWords(image, TessPageIteratorLevel.RIL_PARA);
	    System.out.println(results1);
	    System.out.println("=====================================");
	    results1 = instance.getWords(image, TessPageIteratorLevel.RIL_BLOCK);
	    System.out.println(results1);
	    */
	    
	    //System.out.println(results);

	    
	    
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
        double median = getFontHeight(results1);
        image = preprocessImage(image, median);
        return image;
	}
	
	public BufferedImage preprocessImage(BufferedImage testimg, double fontsize){
		float width = testimg.getWidth();
        float height = testimg.getHeight();
        float ratio = 1;
        if(fontsize!=50){
        	ratio = (float) (50/fontsize);
        }

        int newW = (int) (ratio*width);
        int newH = (int) (ratio*height);

        
        if(ratio!=1){
        	BufferedImage newImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        	Graphics g = newImage.createGraphics();
        	g.drawImage(testimg, 0, 0, newW, newH, null);
        	g.dispose();
        	//Imgproc.resize(source, source, new Size(newW,newH));
        	testimg = newImage;
        }
        
    	Mat source= OpenCVUtility.img2Mat(testimg);
    	if(source==null)
    		throw new OutOfMemoryError("OpenCV Error: Insufficient memory");
        int blurMat =3;
        int threshold_val = 195;
        blurMat = (int) ((Math.round(ratio))*2+1);
        if(blurMat<3){
        	blurMat=3;
        }
        if(ratio>1){
        	threshold_val=threshold_val+(int) ((Math.log10(ratio))*55);
        	if(threshold_val>220)
        		threshold_val=220;
        }

       // blurMat=5;
       // threshold_val=195;
        System.out.println(ratio+":"+blurMat+":"+threshold_val);
        if(source.channels()>1)
            Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(source, source, new Size(blurMat, blurMat), 0);
        Imgproc.threshold(source, source, threshold_val, 255, Imgproc.THRESH_BINARY);
        Imgproc.GaussianBlur(source, source, new Size(3, 3), 0);
        Imgproc.threshold(source, source, 0, 255, Imgproc.THRESH_OTSU);
        //Imgproc.blur(source, source, new Size(3, 3));
        
        testimg = OpenCVUtility.mat2Img(source);
        return testimg;
	}
	
	public double getFontHeight(List<Word> results1){
	    int count = 0;
	    int[] numArray = new int[results1.size()];
	    for(Word w: results1){
	    	numArray[count] = (int) w.getBoundingBox().getHeight();
	    	count++;
	    }
	    Arrays.sort(numArray);
		double median;
		if (numArray.length % 2 == 0)
		    median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
		else
		    median = (double) numArray[numArray.length/2];
		return median;
	}

	public BufferedImage rotate( BufferedImage inputImage, int orientation ){
		if(orientation!=0){
			if(orientation==1){
				return rotate90(  inputImage );
			}
			if(orientation==2){
				return rotate180(  inputImage );
			}
			if(orientation==3){
				return rotate270(  inputImage );
			}
		}
		return inputImage;
	}
	
	public BufferedImage rotate90( BufferedImage inputImage ){
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		BufferedImage returnImage = new BufferedImage( height, width , inputImage.getType()  );

		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				returnImage.setRGB( height - y -1, x, inputImage.getRGB( x, y  )  );
			}
		}
		return returnImage;
	}
	public BufferedImage rotate180( BufferedImage inputImage ) {
			int width = inputImage.getWidth(); //the Width of the original image
			int height = inputImage.getHeight();//the Height of the original image

			BufferedImage returnImage = new BufferedImage( width, height, inputImage.getType()  );
			for( int x = 0; x < width; x++ ) {
				for( int y = 0; y < height; y++ ) {
					returnImage.setRGB( width - x- 1, height - y - 1, inputImage.getRGB( x, y  )  );
				}
			}
			return returnImage;

		}
	public BufferedImage rotate270( BufferedImage inputImage ){
			int width = inputImage.getWidth();
			int height = inputImage.getHeight();
			BufferedImage returnImage = new BufferedImage( height, width , inputImage.getType()  );

			for( int x = 0; x < width; x++ ) {
				for( int y = 0; y < height; y++ ) {
					returnImage.setRGB(y, width - x - 1, inputImage.getRGB( x, y  )  );
				}
				}
			return returnImage;

		}
}
