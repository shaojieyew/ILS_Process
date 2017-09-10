package application.gui.controller;

import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import report.AttributeIndex;
import report.Report;

public class SummaryGUI extends BorderPane {
	private List<Report> observableList = null;

	private DoubleBinding bindingX = widthProperty().divide(14);
	private DoubleBinding bindingY = heightProperty().divide(14);
	private Line []selectors = {new Line(),new Line(),new Line(),new Line()};
	private Line []selectorsJoiner = {new Line(),new Line(),new Line()};
	float selectorsLoc [][]={{-1,0},{-1,0},{-1,0},{-1,0}};
	boolean hideShade = true;
	boolean hideSelector = true;
	
	public float[][] getSelectorsLoc() {
		return selectorsLoc;
	}


	public Line[] getSelectors() {
		return selectors;
	}

	public void setSelectors(Line[] selectors) {
		this.selectors = selectors;
	}
	public void showSelectors(Line[] selectors) {
		this.selectors = selectors;
        this.getChildren().addAll(selectors);
	}

	public enum StatMode {
		MEAN, MEDIAN
	}
	private StatMode statMode = StatMode.MEDIAN;
	public void setStatMode(StatMode statMode) {
		this.statMode = statMode;
		loadGraphic();
	}

	public SummaryGUI(ObservableList<Report> observableList) {
	}
	
	public SummaryGUI() {
		super();
		for(int i =0;i<selectors.length;i++){
			selectors[i].setStroke(Color.web("0xff0000bb"));
			selectors[i].setFill(Color.TRANSPARENT);
			selectors[i].setStrokeWidth(2);
			selectors[i].startYProperty().bind(bindingY.multiply(1.50+(i*3)));
			selectors[i].endYProperty().bind(bindingY.multiply(2.50+(i*3)).add(bindingY).add(bindingY));
			//selectors[i].setStrokeDashOffset(bindingY.get()/10);
		}
		for(int i =0;i<selectorsJoiner.length;i++){
			selectorsJoiner[i].setStroke(Color.web("0xff0000bb"));
			selectorsJoiner[i].setFill(Color.TRANSPARENT);
			selectorsJoiner[i].setStrokeWidth(2);
			selectorsJoiner[i].getStrokeDashArray().addAll(2d);
		}
	}
	public void setReportList(List<Report> observableList) {
		this.observableList = observableList;
		loadGraphic();
	}

	public void loadGraphic(){
		this.getChildren().clear();
		if(observableList==null)
			return;
		int indices[][]=computeSummary(observableList);
		int highestIndices[] = new int[4];
		//border
		Rectangle border = new Rectangle();
		border.widthProperty().bind(widthProperty());
		border.heightProperty().bind(heightProperty());
		border.setFill(Color.TRANSPARENT);
		//border.setStroke(Color.BLUE);
		border.setStrokeWidth(1);
        this.getChildren().addAll(border);

        ObjectExpression<Font> fontTracking = Bindings.createObjectBinding(() -> Font.font(null,FontWeight.NORMAL,getWidth()/28), widthProperty()) ;
        ObjectExpression<Font> fontTrackingBold = Bindings.createObjectBinding(() -> Font.font(null,FontWeight.BOLD,getWidth()/28), widthProperty()) ;
        

        String yLabel[] = {"11","9","7","5","3","1","1","3","5","7","9","11"};
        String attribute1[] = {"ACT","SEN","VIS","SEQ"};
        String attribute2[] = {"REF","INT","VRB","GLO"};

        for(int j =0;j<yLabel.length;j++){
        	//draw labelY
            Text textYLabel = new Text(yLabel[j]);
            textYLabel.setTextAlignment(TextAlignment.CENTER);
            textYLabel.fontProperty().bind(fontTrackingBold);
            textYLabel.xProperty().bind(bindingX.multiply(j+1));
            textYLabel.wrappingWidthProperty().bind(bindingX);
            textYLabel.yProperty().bind(bindingY.multiply(1.4));
            this.getChildren().addAll(textYLabel);
        }
        
        for(int i =0;i<4;i++){
    		Rectangle bar1 = new Rectangle();
        	int posY = 2+(i*3);
        	
        	//draw attribute1
            Text textAttribute1 = new Text(attribute1[i]);
            //textAttribute1.setFont(Font.font(null, FontWeight.BOLD, 10));
            textAttribute1.fontProperty().bind(fontTracking);
            textAttribute1.setTextAlignment(TextAlignment.CENTER);
            textAttribute1.xProperty().bind(widthProperty().subtract(widthProperty()));
            textAttribute1.wrappingWidthProperty().bind(bindingX);
            textAttribute1.yProperty().bind(bindingY.multiply(posY).add(bindingY.multiply(1.4)));
            this.getChildren().addAll(textAttribute1);
            
        	//draw attribute2
            Text textAttribute2 = new Text(attribute2[i]);
            textAttribute2.setTextAlignment(TextAlignment.CENTER);
            textAttribute2.fontProperty().bind(fontTracking);
            textAttribute2.xProperty().bind(widthProperty().subtract(bindingX));
            textAttribute2.wrappingWidthProperty().bind(bindingX);
            textAttribute2.yProperty().bind(bindingY.multiply(posY).add(bindingY.multiply(1.4)));
            this.getChildren().addAll(textAttribute2);
        	
    		//draw bar
    		bar1.xProperty().bind(bindingX);
    		bar1.widthProperty().bind(widthProperty().subtract(bindingX).subtract(bindingX));
    		bar1.yProperty().bind(bindingY.multiply(posY));
    		bar1.heightProperty().bind(bindingY.add(bindingY));
    		bar1.setFill(Color.LIGHTYELLOW);
    		//bar1.setStroke(Color.LIGHTYELLOW);
    		bar1.setStrokeWidth(1);
            this.getChildren().addAll(bar1);
            
            float totalCount = 0;
        	for(int j=0;j<12;j++){
                if(highestIndices[i]<indices[i][j]){
                	highestIndices[i]=indices[i][j];
                }
                totalCount=totalCount+indices[i][j];
        	}
        	
        	
        	for(int j=0;j<12;j++){
                Text textIndex = new Text(indices[i][j]+"");
                textIndex.setTextAlignment(TextAlignment.CENTER);
                textIndex.fontProperty().bind(fontTracking);
                if(indices[i][j]==highestIndices[i] || (((float)indices[i][j]/totalCount)/((float)highestIndices[i]/totalCount))>0.9f){
                	LinearGradient linearGradient = null;
                   	if(!hideShade){
                		textIndex.setFill(Color.WHITE);
                		Color shading = Color.web("0x333333");
	                	if((((float)indices[i][j]/totalCount)/((float)highestIndices[i]/totalCount))>0.9f&&indices[i][j]!=highestIndices[i]){
	                		shading = Color.web("0x888888");
	                	}
	                	Stop[] stops = new Stop[] { 
	                            new Stop(0, (j!=0&&indices[i][j]==indices[i][j-1])?shading:((j!=0&&(((float)indices[i][j-1]/totalCount)/((float)highestIndices[i]/totalCount))>0.9f)?Color.web("0x888888"):Color.TRANSPARENT) ), 
	                            new Stop(0.4, shading), 
	                            new Stop(0.6, shading), 
	                            new Stop(1, (j!=11&&indices[i][j]==indices[i][j+1])?shading:((j!=11&&(((float)indices[i][j+1]/totalCount)/((float)highestIndices[i]/totalCount))>0.9f)?Color.web("0x888888"):Color.TRANSPARENT) )};
	                    linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
                   	}
                    Rectangle rect1 = new Rectangle();
                    rect1.setStroke(linearGradient);
                    rect1.xProperty().bind(bindingX.multiply(j+1));
                    rect1.yProperty().bind(bindingY.multiply(posY));
                    rect1.widthProperty().bind(bindingX);
                    rect1.heightProperty().bind(bindingY.multiply(2));
                    rect1.setFill(linearGradient);
                    this.getChildren().addAll(rect1);
                }
                textIndex.xProperty().bind(bindingX.multiply(j+1));
                textIndex.wrappingWidthProperty().bind(bindingX);
                textIndex.yProperty().bind(bindingY.multiply(posY).add(bindingY.multiply(1.4)));
                this.getChildren().addAll(textIndex);
        	}
        }
        this.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	setSelector(mouseEvent);
            }
        });
        this.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	setSelector(mouseEvent);
            }
        });
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
            	setSelector(mouseEvent);
            }
        });
        setSelector(this.statMode, indices);
	}

	public void setSelectorsLoc(float[][] selectorsLoc) {
		this.selectorsLoc = selectorsLoc;
		for(int i =0;i<4;i++){
			getChildren().remove(selectors[i]);
			if(selectorsLoc[i][0]>=0){
				selectors[i].startXProperty().bind(bindingX.multiply((float)selectorsLoc[i][0]+selectorsLoc[i][1]%1));
				selectors[i].endXProperty().bind(bindingX.multiply((float)selectorsLoc[i][0]+selectorsLoc[i][1]%1));
				getChildren().addAll(selectors[i]);
			}
		}
		setSelectorJoiner();
	}
	private void setSelector(MouseEvent mouseEvent){
		if(hideSelector){
			return;
		}
		BorderPane summaryGUI = (BorderPane) mouseEvent.getSource();
		int barMarginX=14;
		int barMarginY=14;
		DoubleBinding bindingX = widthProperty().divide(barMarginX);
		DoubleBinding bindingY = heightProperty().divide(barMarginY);
		if(mouseEvent.getX()>=bindingX.get()&&mouseEvent.getX()<=(summaryGUI.widthProperty().subtract(bindingX).get())){
			float leftCenterRight=0;
			int selectedIndex = (int) Math.round((mouseEvent.getX()-(mouseEvent.getX()%bindingX.get()))/bindingX.get());
			/*
			if(((mouseEvent.getX()%bindingX.get())/bindingX.get())>0.8f){
				leftCenterRight=1;
			}if(((mouseEvent.getX()%bindingX.get())/bindingX.get())<0.2f){
				leftCenterRight=-1;
			}*/
			leftCenterRight=(float) ((mouseEvent.getX()%bindingX.get())/bindingX.get());
			System.out.println(leftCenterRight);
			for(int i =0;i<4;i++){
    			if(mouseEvent.getY()>=bindingY.multiply(i*3+2).get()&&mouseEvent.getY()<=bindingY.multiply(i*3+4).get()){
    				selectorsLoc[i][0]=selectedIndex;
    				selectorsLoc[i][1]=leftCenterRight;
    				setSelectorsLoc(selectorsLoc);
    			}
			}
			setSelectorJoiner();
		}
	}
	
	private void setSelectorJoiner(){
		for(int i=0;i<3;i++){
			if(selectorsLoc[i][0]!=-1&&selectorsLoc[i+1][0]!=-1){
				selectorsJoiner[i].startXProperty().bind(bindingX.multiply((float)selectorsLoc[i][0]+selectorsLoc[i][1]%1));
				selectorsJoiner[i].startYProperty().bind(bindingY.multiply(2.50+(i*3)).add(bindingY).add(bindingY));
				selectorsJoiner[i].endXProperty().bind(bindingX.multiply((float)selectorsLoc[i+1][0]+selectorsLoc[i+1][1]%1));
				selectorsJoiner[i].endYProperty().bind(bindingY.multiply(1.50+((i+1)*3)));
				getChildren().remove(selectorsJoiner[i]);
				getChildren().addAll(selectorsJoiner[i]);
				}
		}
	}
	
	private void setSelector(StatMode statMode, int[][]indices){
		if(hideSelector){
			return;
		}
		for(int i =0;i<4;i++){
			float indexLoc = -1; 
			if(statMode.equals(StatMode.MEAN)){
				indexLoc=getMeanLoc(indices[i]);
			}
			if(statMode.equals(StatMode.MEDIAN)){
				indexLoc = getMedianLoc(indices[i]);
			}
			selectorsLoc[i][0]=-1;
			if(indexLoc>=0){
				selectorsLoc[i][1]=(float) (0.49+(indexLoc%1));	
				selectorsLoc[i][0]=(int)indexLoc+1+(int)selectorsLoc[i][1];
				setSelectorsLoc(selectorsLoc);
			}
		}
		setSelectorJoiner();
	}
	
	private int[][] computeSummary(List<Report> observableList2){
		int indices[][]= new int[4][12];
		for(int y = 0; y<indices.length;y++){
			for(int x = 0; x<indices[y].length;x++){
				indices[y][x]=0;
			}
		}
		
		for(Report report: observableList2){
			if(report.getStatus().equals(Report.STATUS_COMPLETED)){
				for(AttributeIndex ai : report.getAttributes()){
					if(ai.getIndex()>0&&ai.getIndex()%2==1&&ai.getIndex()<=11){
						int x = 0;
						int y = 0;
						if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_ACTIVE)){
							x = (11-ai.getIndex())/2;
							y = 0;
						}
						if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_REFLECTIVE)){
							x = ((ai.getIndex()-1)/2)+6;
							y = 0;
						}
						if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_SENSING)){
							x = (11-ai.getIndex())/2;
							y = 1;
						}
						if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_INTUITIVE)){
							x = ((ai.getIndex()-1)/2)+6;
							y = 1;
						}
						if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_VISUAL)){
							x = (11-ai.getIndex())/2;
							y = 2;
						}
						if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_VERBAL)){
							x = ((ai.getIndex()-1)/2)+6;
							y = 2;
						}
						if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_SEQUENTIAL)){
							x = (11-ai.getIndex())/2;
							y = 3;
						}
						if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_GLOBAL)){
							x = ((ai.getIndex()-1)/2)+6;
							y = 3;
						}
						indices[y][x]=indices[y][x]+1;
					}
				}
			}
		}
		return indices;
	}

	private static float getMeanLoc(int arr[] ){
		if(arr.length==0){
			return -1;
		}
		int total =0;
		for(int i=0;i<arr.length;i++){
			total = total + arr[i];
		}
		if(total==0){
			return -1;
		}
		int count = 0;
		for(int i=0;i<arr.length;i++){
			count = count + arr[i]*i;
		}
		float mean = (float)count/(float)total;
		return mean;
	}
	
	private static float getMedianLoc(int arr[] ){
		if(arr.length==0){
			return -1;
		}
		//get median
		float median1Loc=-1;
		float median2Loc=-1;
		int total =0;
		for(int i=0;i<arr.length;i++){
			total = total + arr[i];
		}
		if(total==0){
			return -1;
		}
		if((total%2)==0){
			median1Loc =  (float)total/2f;
			median2Loc =  ((float)total/2f)+1f;
		}
		if((total%2)==1){
			median1Loc =  ((float)total+1f)/2f;
			median2Loc =  ((float)total+1f)/2f;
		}
		float median1Index = -1;
		float median2Index = -1;
		int count = 0;
		for(int i=0;i<arr.length;i++){
			count = count + arr[i];
			if(count>=median1Loc){
				if(median1Index==-1){
					median1Index=i;
				}
			}
			if(count>=median2Loc){
				median2Index=i;
				break;
			}
		}
		float median = (median1Index+median2Index)/2;
		return median;
	}
}
