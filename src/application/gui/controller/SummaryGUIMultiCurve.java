package application.gui.controller;

import java.util.ArrayList;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import report.AttributeIndex;
import report.Report;
import report.ReportCurve;

public class SummaryGUIMultiCurve extends SummaryGUI {
	public SummaryGUIMultiCurve(ObservableList<Report> observableList) {
		super(observableList);
	}
	
	public SummaryGUIMultiCurve() {
		super();
		this.setMouseClickEnabled(false);
	}
	
	public  List<ReportCurve> getCurves() {
		return curves;
	}

	private List<ReportCurve> curves = new ArrayList<ReportCurve>();
	private List<Line> lines = new ArrayList<Line>();
	
	private static float[][] getCurveValue(ReportCurve rc){
		float[][][]  curve = new float [3][4][2];
		String[]  results= rc.getCurve().split("],");
		for(int i =0;i<results.length;i++){
			String str = results[i];
			str = str.replace("[", "");
			str = str.replace("]", "");
			curve[i/4][i%4][0] = Float.parseFloat(str.split(",")[0]);
			curve[i/4][i%4][1] = Float.parseFloat(str.split(",")[1]);
		}
		return curve[0];
	}

	public void drawCurve(ReportCurve curve){
		curves.add(curve);
		drawCurve();
	}
	public void drawCurves(List<ReportCurve> list){
		this.curves.addAll(list);
		drawCurve();
	}
	public void removeCurve(ReportCurve curve){
		if(curves.size()>0){
			for(int i =curves.size()-1;i>=0;i--){
				if(curves.get(i).name.equals(curve.name)){
					curves.remove(i);
				}
			}
		}
		drawCurve();
	}

	public void loadGraphic(){
		super.loadGraphic();
		drawCurve();
	}
	public void drawCurve(){
		getChildren().removeAll(lines);
		lines.clear();
		for(ReportCurve rc: curves){
			float[][] rcValue = getCurveValue(rc);
			for(int i =0;i<4;i++){
				Line line = new Line();
				line.setStroke(Color.web(rc.color));
				line.setFill(Color.TRANSPARENT);
				line.setStrokeWidth(2);
				line.startYProperty().bind(bindingY.multiply(1.50+(i*3)));
				line.endYProperty().bind(bindingY.multiply(2.50+(i*3)).add(bindingY).add(bindingY));
				if(rcValue[i][0]>=0){
					line.startXProperty().bind(bindingX.multiply((float)rcValue[i][0]+rcValue[i][1]%1));
					line.endXProperty().bind(bindingX.multiply((float)rcValue[i][0]+rcValue[i][1]%1));
					getChildren().addAll(line);
					lines.add(line);
				}
			}
			for(int i =0;i<3;i++){
				Line line = new Line();
				line.setStroke(Color.web(rc.color));
				line.setFill(Color.TRANSPARENT);
				line.setStrokeWidth(2);
				line.getStrokeDashArray().addAll(2d);
				if(rcValue[i][0]!=-1&&rcValue[i+1][0]!=-1){
					line.startXProperty().bind(bindingX.multiply((float)rcValue[i][0]+rcValue[i][1]%1));
					line.startYProperty().bind(bindingY.multiply(2.50+(i*3)).add(bindingY).add(bindingY));
					line.endXProperty().bind(bindingX.multiply((float)rcValue[i+1][0]+rcValue[i+1][1]%1));
					line.endYProperty().bind(bindingY.multiply(1.50+((i+1)*3)));
					getChildren().addAll(line);
					lines.add(line);
				}
			}
			
			
		
		}
	}

}
