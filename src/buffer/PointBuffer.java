package buffer;

import java.util.ArrayList;
import java.util.List;

import math.Vector2D;
import scau.gz.zhw.Point;
import scau.gz.zhw.Polygon;

public class PointBuffer {
	private Point point;
	private List<Point> points = new ArrayList<>();
	//步长
	private double step = 1;
	
	public PointBuffer(Point p) {
		this.point = p;
	}
	
	public double getStep() {
		return step;
	}
	
	public void setStep(double step) {
		if(step<=0) {
			System.out.println("步长不可小于等于0");
			return;
		}
		if(step>90) {
			System.out.println("步长最好小于90");
		}
		this.step = step;
	}
	
	/**
	 * 产生半径为radius的圆形缓冲区
	 * @param radius
	 * @param type cw-0 ccw-1
	 * @return
	 */
	public List<Point> generateBuffer(double radius,int type){
		if(!points.isEmpty())
			return points;
		Point start = new Point(point.getX(), point.getY()+radius);
		points.add(start);
		double sina;
		double cosa;
		double radian;
		for(double i=1;i<=90/step;i++) {
			Point p = points.get(points.size()-1);
			System.out.println(p.toString()+" ");
			//System.out.println("角度: "+Math.toDegrees(p.getAngle()));
			double x,y;
			
			if(type==0) {
				//构建圆心到某点的向量，为了得到其夹角
				Vector2D pVector = new Vector2D(this.point, p);
				radian = pVector.getRadian()-Math.toRadians(step);
				sina = Math.sin(radian);
				cosa = Math.cos(radian);
				x = radius*cosa+point.getX();
				y = radius*sina+point.getY();
				
			}else {
				//构建圆心到某点的向量，为了得到其夹角
				Vector2D pVector = new Vector2D(point, p);
				radian = pVector.getRadian()+Math.toRadians(step);
				sina = Math.sin(radian);
				cosa = Math.cos(radian);
				x = radius*cosa+point.getX();
				y = radius*sina+point.getY();
			}
			Point result = new Point(x, y);
			//System.out.println("算出的点 "+result+"\n");
			points.add(result);
		}
		return points;
	}

	
	
	public static void main(String[] args) {
		Point center = new Point(20, -30);
		PointBuffer pointBuffer = new PointBuffer(center);
		pointBuffer.setStep(20);
		List<Point> points=pointBuffer.generateBuffer(80, 0);
		//points.add(center);
		
		//System.out.println(points.size());
		
		
		Polygon polygon = new Polygon(points, false);
		polygon.showGUI();
		//polyline.showGUI();
		
	}
	
}
