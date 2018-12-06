package scau.gz.zhw;

import java.util.ArrayList;
import java.util.List;


public class Polygon extends Vector{
	private Point[] points;
	private boolean isClose;
	protected ArrayList<Line> lines = new ArrayList<Line>();
	
	
	public Polygon(Point[] points,boolean isClose) {
		this.points = points;
		this.isClose=isClose;
		boolean flag = false;
		if(Double.doubleToLongBits(points[points.length-1].getX())==Double.doubleToLongBits(points[0].getX()) && 
				Double.doubleToLongBits(points[points.length-1].getY()) == Double.doubleToLongBits((points[0].getY()))){
			flag = true;
		}
		//如果最后一个点不等于第一个点
				//自动闭合
		if(!isClose) {
			flag=true;
		}
		if(!flag) {
			for(int i=0;i<points.length;i++) {
				Line line = new Line(points[i%points.length], points[(i+1)%points.length]);
				lines.add(line);
			}
		}else {
			for(int i=0;i<points.length-1;i++) {
				Line line = new Line(points[i], points[i+1]);
				lines.add(line);
			}
		}
		
	}
	
	public Polygon(List<Point> pointss,boolean isClose) {
		Point[] points = new Point[pointss.size()];
		pointss.toArray(points);
		
		this.points = points;
		this.isClose=isClose;
		boolean flag = false;
		if(Double.doubleToLongBits(points[points.length-1].getX())==Double.doubleToLongBits(points[0].getX()) && 
				Double.doubleToLongBits(points[points.length-1].getY()) == Double.doubleToLongBits((points[0].getY()))){
			flag = true;
		}
		//如果最后一个点不等于第一个点
				//自动闭合
		if(!isClose) {
			flag=true;
		}
		if(!flag) {
			for(int i=0;i<points.length;i++) {
				Line line = new Line(points[i%points.length], points[(i+1)%points.length]);
				lines.add(line);
			}
		}else {
			for(int i=0;i<points.length-1;i++) {
				Line line = new Line(points[i], points[i+1]);
				lines.add(line);
			}
		}
	}
	
	public boolean isClose() {
		return isClose;
	}


	public void printPoint() {
		for(Point point:points) {
			System.out.println(String.format("(%.2f,%.2f)", point.getX(),point.getY()));
		}
		System.out.println();
	}



	public ArrayList<Line> getLines() {
		return lines;
	}
	
	public Point[] getPoints() {
		return points;
	}
	
	public List<Point> getPointsList(){
		List<Point> points = new ArrayList<>();
		for(Point p: this.points) {
			points.add(p);
		}
		return points;
	}
	
	/**
	 * 求算数中心点
	 * @return
	 */
	public Point getCenterPoint() {
		double cenX=0,cenY=0;
		for(int i=0;i<points.length;i++) {
			cenX+=points[i].getX();
			cenY+=points[i].getY();
		}
		
		cenX/=points.length;
		cenY/=points.length;
		
		return new Point(cenX, cenY);
	}
	
	@Override
	protected String getType() {
		// TODO Auto-generated method stub
		return "面";
	}
	
	@Override
	public void showGUI() {
		// TODO Auto-generated method stub
		List<Polygon> pList = new ArrayList<>();
		pList.add(this);
		
		//PaintVector.createAndShowGUI(null, lines, null);
		PaintVector.createAndShowGUI(null, null, pList);
	}
	
	public static void main(String[] args) {
		Point a = new Point(10, 20);
		Point b = new Point(-10, 20);
		Point c = new Point(0,-20);
		Point[] points = {a,b,c};
		Polygon triangle = new Polygon(points,true);
		triangle.showGUI();
	}
}
