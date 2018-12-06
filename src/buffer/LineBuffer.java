package buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import math.Vector2D;
import scau.gz.zhw.Line;
import scau.gz.zhw.PaintVector;
import scau.gz.zhw.Point;
import scau.gz.zhw.Polyline;

public class LineBuffer {
	private Polyline polyline;
	@SuppressWarnings("unused")
	private List<Point> buffer = new ArrayList<>();
	//����
	private static double step = 1;
	private Point bufStart;
	
	public LineBuffer(Polyline polyline) {
		// TODO Auto-generated constructor stub
		this.polyline = polyline;
	}
	
	public Polyline getPolyline() {
		return polyline;
	}
	
	public List<Point> generateBuffer(double bufDis,int type){
		
		//�Խ��㼯���а���x��y����Ϊ�˸�����ıȽϸ�������е��Ƿ��ڶ������
		Comparator<Point> XYcomparator = new Comparator<Point>() {
			//���ȶ�X��������x������y��������
			@Override
			public int compare(Point arg0, Point arg1) {
				// TODO Auto-generated method stub
				if(arg0.getX()-arg1.getX()==0) {
					return -(int) (arg0.getY()-arg1.getY());
				}else {
					return (int) (arg0.getX()-arg1.getX());
				}
				
			}
		};
		List<Point> tem = new ArrayList<>();
		for(Point p:this.polyline.getPoints()) {
			tem.add(new Point(p.getX(), p.getY()));
		}
		Collections.sort(tem,XYcomparator);
		Polyline polyline = new Polyline(tem);
		
		List<Point> points = new ArrayList<>();
//		Point startPoint = polyline.getStart();
//		Point endPoint = polyline.getEnd();
		List<Line> lines = polyline.getLines();
		
		//���뿪ʼ��Ļ�����
		List<Point> bufStart = generateTerminalPoint(polyline, bufDis, type, 0);
		this.bufStart = bufStart.get(0);
		points.addAll(bufStart);
		//�������һ��
		for(int i=0;i<lines.size()-1;i++) {
			Line a = lines.get(i);
			Line b = lines.get(i+1);
			if(turnDirection(a.getVector2D(), b.getVector2D())==-1) {
				points.addAll(generateCircle(a, b, bufDis, type));
			}else {
				Line[] aPara = a.getParallel(bufDis);
				Line[] bPara = b.getParallel(bufDis);
				if(type==0) {
					Line upA = aPara[0];
					Line upB = bPara[0];
					Point insect = upA.intercourse(upB);
					points.add(insect);
				}else {
					Line downA = aPara[1];
					Line downB = bPara[1];
					Point insect = downA.intercourse(downB);
					points.add(insect);
				}
				
			}
		}
		//���������Ļ�����
		points.addAll(generateTerminalPoint(polyline, bufDis, type, 1));
		//�������һ��
		Polyline reversePolyLine = polyline.getReversePolyline();
		lines = reversePolyLine.getLines();
		for(int i=0;i<lines.size()-1;i++) {
			Line a = lines.get(i);
			Line b = lines.get(i+1);
			if(turnDirection(a.getVector2D(), b.getVector2D())==-1) {
				points.addAll(generateCircle(a, b, bufDis, type));
			}else {
				Line[] aPara = a.getParallel(bufDis);
				Line[] bPara = b.getParallel(bufDis);
				if(type==0) {
					Line downA = aPara[0];
					Line downB = bPara[0];
					Point insect = downA.intercourse(downB);
					points.add(insect);
				}else {
					Line upA = aPara[1];
					Line upB = bPara[1];
					Point insect = upA.intercourse(upB);
					points.add(insect);
				}
				
			}
		}
		//�ѿ�ʼ����룬�պϻ�����
		points.add(this.bufStart);
		return points;
	}
	
	
	private static Point getIntersectOfTwoParallel(Vector2D a,Vector2D b,double radius) {
		if(turnDirection(a, b)==-1)
			return null;
		double radian1 = a.getRadian();
		double radian2 = b.getReverseRadian();
		double x,y;
		x=(Math.cos(radian1)+Math.cos(radian2))*radius/Math.sin(radian2-radian1);
		y=(Math.sin(radian1)+Math.sin(radian2))*radius/Math.sin(radian2-radian1);
		return new Point(x, y);
	}
	
	/**
	 * �ж����ߵİ�͹��
	 * @param a
	 * @param b
	 * @return ����1 ����Ϊ��; ����-1 ����Ϊ͹; ����0 ���߹���;
	 */
	private static int turnDirection(Vector2D a,Vector2D b) {
		return a.crossProduct(b)>0?1:a.crossProduct(b)==0?0:-1;
	}
	
	public  static List<Point> generateCircle(Point center,double radius,int type,Point start,Point end){
		List<Point> points = new ArrayList<>();
		//Point start = new Point(center.getX(), center.getY()+radius);
		double angle = Math.toDegrees(Vector2D.radianBetween(new Vector2D(center, start), new Vector2D(center,end)));
		//System.out.println("�Ƕȣ�" +angle);
		points.add(start);
		double sina;
		double cosa;
		double radian;
		for(double i=1;i<=angle/step;i++) {
			Point p = points.get(points.size()-1);
			//System.out.println(p.toString()+" ");
			//System.out.println("�Ƕ�: "+Math.toDegrees(p.getAngle()));
			double x,y;
			
			if(type==0) {
				//����Բ�ĵ�ĳ���������Ϊ�˵õ���н�
				Vector2D pVector = new Vector2D(center, p);
				radian = pVector.getRadian()-Math.toRadians(step);
				sina = Math.sin(radian);
				cosa = Math.cos(radian);
				x = radius*cosa+center.getX();
				y = radius*sina+center.getY();
				
			}else {
				//����Բ�ĵ�ĳ���������Ϊ�˵õ���н�
				Vector2D pVector = new Vector2D(center, p);
				radian = pVector.getRadian()+Math.toRadians(step);
				sina = Math.sin(radian);
				cosa = Math.cos(radian);
				x = radius*cosa+center.getX();
				y = radius*sina+center.getY();
			}
			Point result = new Point(x, y);
			//System.out.println("����ĵ� "+result+"\n");
			points.add(result);
		}
		return points;
	}
	
	public static List<Point> generateCircle(Line a,Line b,double radius,int type){
		if(Double.doubleToLongBits(a.getA()*b.getB()) == Double.doubleToLongBits(b.getA() *a.getB())) {
			System.out.println("ֱ���ص���ƽ��");
			Point point = getIntersectOfTwoParallel(a.getVector2D(), b.getVector2D(), radius);
			List<Point> points = new ArrayList<>();
			points.add(point);
			return points;
		}
		Point center = a.intercourse(b);
		Line line1 = a.getParallel(radius)[0];
		Line line2 = b.getParallel(radius)[0];
		Point start = new Line(a.getN(), center).intercourse(line1);
		Point end = new Line(b.getN(), center).intercourse(line2);
		System.out.println("center: "+center);
		System.out.println("start: "+start);
		System.out.println("end: "+end);
		return generateCircle(center, radius, type, start, end);
	}
	
	/**
	 * ���ɶ˵㴦�Ļ���Բ��һ������1/4��Բ��
	 * @param terminal
	 * @param radius
	 * @param type 0-cw 1-ccw
	 * @param pointType 0-��ʼ�� 1-������
	 * @param v �˵���ڽ�һ��
	 * @return
	 */
	public static List<Point> generateTerminalPoint(Polyline polyline,double radius,int type,int pointType){
		Point start,end;
		//��ʼ��
		if(pointType==0) {
			Line startLine = polyline.getStartLine();
			Line[] paralles = startLine.getParallel(radius);
			Line upLine = paralles[0];
			Line downLine = paralles[1];
			Line perLine = new Line(startLine.getN(),polyline.getStart());
			//˳ʱ��
			if(type==0) {
				start = perLine.intercourse(downLine);
				end = perLine.intercourse(upLine);
				return generateCircle(polyline.getStart(), radius, type, start, end);
			}
			//��ʱ��
			if(type==1) {
				start = perLine.intercourse(upLine);
				end = perLine.intercourse(downLine);
				return generateCircle(polyline.getStart(), radius, type, start, end);
			}
		}
		//������
		if(pointType==1) {
			Line endLine = polyline.getEndLine();
			Line[] paralles = endLine.getParallel(radius);
			Line upLine = paralles[0];
			Line downLine = paralles[1];
			Line perLine = new Line(endLine.getN(),polyline.getEnd());
			//˳ʱ��
			if(type==0) {
				start = perLine.intercourse(upLine);
				end = perLine.intercourse(downLine);
				return generateCircle(polyline.getEnd(), radius, type, start, end);
			}
			//��ʱ�� 
			if(type==1) {
				start = perLine.intercourse(downLine);
				end = perLine.intercourse(upLine);
				return generateCircle(polyline.getEnd(), radius, type, start, end);
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		Point a  = new Point(-100, -100);
		Point b = new Point(-80, 100);
		Point c = new Point(30, -120);
		Point d = new Point(100, 100);
		Point e = new Point(120, 10);
		List<Point> points = new ArrayList<>();
		points.add(a);
		points.add(b);
		points.add(c);
		points.add(d);
		points.add(e);
		Polyline polyline = new Polyline(points);
		List<Polyline> polylines = new ArrayList<>();
		polylines.add(polyline);
//		List<Line> lines = polyline.getLines();
//		List<Point> pointss = new ArrayList<>();
//		Point insectP = getIntersectOfTwoParallel(lines.get(0).getVector2D(), lines.get(1).getVector2D(), 10);
//		pointss.add(insectP);
//		Line line1 = new Line(lines.get(0).getV().reverse(), insectP);
//		Line line2 = new Line(lines.get(1).getV(), insectP);
//		line1.showGUI();
//		line2.showGUI();
		
//		List<Point> points2 =  generateCircle(lines.get(0), lines.get(1), 10, 0);
//		points2.addAll(generateTerminalPoint(polyline, 10, 0, 0));
//		points2.addAll(generateTerminalPoint(polyline, 10, 0, 1));
		List<Point> points2 = new LineBuffer(polyline).generateBuffer(15, 0);
		Polyline polyline2 = new Polyline(points2);
		polylines.add(polyline2);
		PaintVector.createAndShowGUIPlus(points2, polylines, null);
		
	}
}
