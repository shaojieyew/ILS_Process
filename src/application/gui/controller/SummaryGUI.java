package application.gui.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import report.AttributeIndex;
import report.Report;

public class SummaryGUI extends BorderPane {

	public SummaryGUI(ObservableList<Report> observableList) {
		super();
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

		int barMarginX=14;
		int barMarginY=14;
		DoubleBinding bindingX = widthProperty().divide(barMarginX);
		DoubleBinding bindingY = heightProperty().divide(barMarginY);
		ObjectExpression<Font> fontTracking = Bindings.createObjectBinding(() -> Font.font(null,FontWeight.BOLD,getWidth()/28), widthProperty()) ;
        

        String yLabel[] = {"11","9","7","5","3","1","1","3","5","7","9","11"};
        String attribute1[] = {"ACT","SEN","VIS","SEQ"};
        String attribute2[] = {"REF","INT","VRB","GLO"};

        for(int j =0;j<yLabel.length;j++){
        	//draw labelY
            Text textYLabel = new Text(yLabel[j]);
            textYLabel.setTextAlignment(TextAlignment.CENTER);
            textYLabel.fontProperty().bind(fontTracking);
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
            
        	for(int j=0;j<12;j++){
                if(highestIndices[i]<indices[j][i]){
                	highestIndices[i]=indices[j][i];
                }
        	}
        	for(int j=0;j<12;j++){
                Text textIndex = new Text(indices[j][i]+"");
                textIndex.setTextAlignment(TextAlignment.CENTER);
                textIndex.fontProperty().bind(fontTracking);
                if(indices[j][i]==highestIndices[i]){
                	textIndex.setFill(Color.WHITE);
                	Stop[] stops = new Stop[] { 
                            new Stop(0, (j==0||indices[j][i]!=indices[j-1][i])?Color.TRANSPARENT:Color.web("0x333333") ), 
                            new Stop(0.5, Color.web("0x333333")), 
                            new Stop(1, (j==11||indices[j][i]!=indices[j+1][i])?Color.TRANSPARENT:Color.web("0x333333"))};
                    LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
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
                BorderPane summaryGUI = (BorderPane) mouseEvent.getSource();
        		int barMarginX=14;
        		int barMarginY=14;
        		DoubleBinding bindingX = widthProperty().divide(barMarginX);
        		DoubleBinding bindingY = heightProperty().divide(barMarginY);
        		if(mouseEvent.getX()>=bindingX.get()&&mouseEvent.getX()<=(summaryGUI.widthProperty().subtract(bindingX).get())){
        			int selectedIndex = (int) Math.round((mouseEvent.getX()-(mouseEvent.getX()%bindingX.get()))/bindingX.get());
        			for(int i =0;i<4;i++){
            			if(mouseEvent.getY()>=bindingY.multiply(i*3+2).get()&&mouseEvent.getY()<=bindingY.multiply(i*3+4).get()){
            				selectors[i].xProperty().bind(bindingX.multiply(selectedIndex));
            				selectors[i].widthProperty().bind(bindingX);
            				selectors[i].yProperty().bind(bindingY.multiply(2+(i*3)));
            				selectors[i].heightProperty().bind(bindingY.add(bindingY));
            				selectors[i].setStroke(Color.RED);
            				selectors[i].setFill(Color.TRANSPARENT);
            				selectors[i].setStrokeWidth(1);
            				summaryGUI.getChildren().remove(selectors[i]);
            				summaryGUI.getChildren().addAll(selectors[i]);
            			}
        			}
        		}
            }
        });
	}
	Rectangle []selectors = {new Rectangle(),new Rectangle(),new Rectangle(),new Rectangle()};
	
private int[][] computeSummary(ObservableList<Report> observableList){
	int indices[][]= new int[12][4];
	for(int x = 0; x<indices.length;x++){
		for(int y = 0; y<indices[x].length;y++){
			indices[x][y]=0;
		}
	}
	
	for(Report report: observableList){
		if(report.getStatus().equals(Report.STATUS_COMPLETED)){
			for(AttributeIndex ai : report.getAttributes()){
				if(ai.getIndex()>0&&ai.getIndex()%2==1&&ai.getIndex()<=11){
					int x = 0;
					int y = 0;
					if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_ACTIVE)){
						x = (11-ai.getIndex())/2;
						y = 0;
						indices[x][y]=indices[x][y]+1;
					}
					if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_REFLECTIVE)){
						x = ((ai.getIndex()-1)/2)+6;
						y = 0;
						indices[x][y]=indices[x][y]+1;
					}
					if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_SENSING)){
						x = (11-ai.getIndex())/2;
						y = 1;
						indices[x][y]=indices[x][y]+1;
					}
					if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_INTUITIVE)){
						x = ((ai.getIndex()-1)/2)+6;
						y = 1;
						indices[x][y]=indices[x][y]+1;
					}
					if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_VISUAL)){
						x = (11-ai.getIndex())/2;
						y = 2;
						indices[x][y]=indices[x][y]+1;
					}
					if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_VERBAL)){
						x = ((ai.getIndex()-1)/2)+6;
						y = 2;
						indices[x][y]=indices[x][y]+1;
					}
					if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_SEQUENTIAL)){
						x = (11-ai.getIndex())/2;
						y = 3;
						indices[x][y]=indices[x][y]+1;
					}
					if(ai.getAttribute().equals(AttributeIndex.KEYWORD_ILS_GLOBAL)){
						x = ((ai.getIndex()-1)/2)+6;
						y = 3;
						indices[x][y]=indices[x][y]+1;
					}
				}
			}
		}
	}
	return indices;
}

}
