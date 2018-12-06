package scau.gz.zhw;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Triangle extends Polygon{

	private Point[] points;
	//����������
	private Point outside;
	private double outsideRadius;
	private static int count = 0;
	private static final int id = count++;

	
	public Triangle(Point ...points) {
		super(points, true);
		// TODO Auto-generated constructor stub
		if(points.length!=3) {
			throw new RuntimeException("�����εĶ���������Ϊ3");
		}
		this.points = points;
//		Point a = points[0];
//		Point b = points[1];
//		Point c = points[2];
		
//		Line ab = new Line(a,b);
//		Line ac = new Line(a,c);
		
//		Point cenOfab = new Point((a.getX()+b.getX())/2, (a.getY()+b.getY())/2);s
//		Point cenOfac = new Point(a.getX()+c.getX()/2,(a.getY()+c.getY())/2);
//		outside = new Line(ab.getN(), cenOfab).intercourse(new Line(ac.getN(),cenOfac));
//		outsideRadius = new Line(outside, a).getLength();
	}


	public void setOutside(Point outside) {
		this.outside = outside;
	}
	
	public void setOutsideRadius(double outsideRadius) {
		this.outsideRadius = outsideRadius;
	}
	
	public Point[] getPoints() {
		return points;
	}


	public Point getOutside() {
		return outside;
	}


	public double getOutsideRadius() {
		return outsideRadius;
	}

	@Override
	protected String getType() {
		// TODO Auto-generated method stub
		return "������";
	}
	
	@Override
	public void showGUI() {
		// TODO Auto-generated method stub
		super.showGUI();
	}
	
	public static void main(String[] args) {
		Point a = new Point(10, 20);
		Point b = new Point(-10, 20);
		Point c = new Point(0,-20);
		
		Triangle triangle = new Triangle(a,b,c);
		Point point = triangle.getOutside();
		System.out.println(point);
		System.out.println(triangle.getOutsideRadius());
		point.showGUI();
		triangle.showGUI();
	}
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}
	
	/**
	 * �鿴���������Ƿ����ĳ����
	 * @param point
	 * @return
	 */
	public boolean isContain(Point point) {
		for(Point p : points) {
			if(p.isEqual(point)) {
				return true;
			}
		}
		return false;
	}
	

	/**
	 * ��ȡ������������
	 * @param point
	 * @return
	 */
	public Set<Point> getOpposite(Point point){
		Set<Point> set = new HashSet<>(Arrays.asList(points));
		if(!set.contains(point))
			throw new RuntimeException("�������β���������� "+point.toString());
		set.remove(point);
		return set;
	}
}
