package scau.gz.zhw;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DrawCircle {
	private Point center;
	private int radius;
	public DrawCircle(Point point,int radius) {
		// TODO Auto-generated constructor stub
		this.center = point;
		this.radius = radius;
	}
	
	public Point getCenter() {
		return center;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public List<Point> draw() {
		List<Point> points1 = new ArrayList<>();
		List<Point> points2= new ArrayList<>();
		List<Point> points3 = new ArrayList<>();
		List<Point> points4 = new ArrayList<>();
		List<Point> points5 = new ArrayList<>();
		List<Point> points6 = new ArrayList<>();
		List<Point> points7 = new ArrayList<>();
		List<Point> points8 = new ArrayList<>();
		List<Point> points = new ArrayList<>();
		Point seed = new Point(center.getX(), center.getY()+radius);
		double d = 3-2*radius;
		int x = (int)seed.getX();
		int y = (int)seed.getY();
		//升序排序
		Comparator<Point> XYcomparator1 = new Comparator<Point>() {
			//优先对X进行排序，x相等则对y进行排序
			@Override
			public int compare(Point arg0, Point arg1) {
				// TODO Auto-generated method stub
				if(arg0.getX()-arg1.getX()==0) {
					return (int) (arg0.getY()-arg1.getY());
				}else {
					return (int) (arg0.getX()-arg1.getX());
				}
				
			}
		};
		//降序排序
		Comparator<Point> XYcomparator2 = new Comparator<Point>() {
			//优先对X进行排序，x相等则对y进行排序
			@Override
			public int compare(Point arg0, Point arg1) {
				// TODO Auto-generated method stub
				if(arg0.getX()-arg1.getX()==0) {
					return -(int) (arg0.getY()-arg1.getY());
				}else {
					return -(int) (arg0.getX()-arg1.getX());
				}
				
			}
		};
		while(x<=y) {
			//得出另外7个对称点
			
			Point s,q,w,e,r,t,p,u;
			//防止出现粘连
			
			s =new Point(x, y);
			
			q = new Point(-x, y);
			
			w = new Point(x, -y);
			
			e = new Point(-x, -y);
			
			r = new Point(y, x);
			
			t = new Point(-y, x);
			
			p = new Point(y, -x);
			
			u = new Point(-y, -x);
			points1.add(s);
			points2.add(w);
			points3.add(p);
			points4.add(u);
			points5.add(e);
			points6.add(q);
			points7.add(t);
			points8.add(r);
			if(d<0) {
				d+=4*x+6;
			}else {
				d+=4*(x-y)+10;
				y--;
			}
			x++;
			//System.out.println(String.format("%d,%d", x,y));
			
		}
		//Collections.sort(points,XYcomparator);
		//为了产生在数值上连续的圆
		Collections.sort(points8,XYcomparator1);
		Collections.sort(points2,XYcomparator2);
		Collections.sort(points4,XYcomparator2);
		Collections.sort(points6,XYcomparator1);
		points.addAll(points1);
		points.addAll(points8);
		points.addAll(points3);
		points.addAll(points2);
		points.addAll(points5);
		points.addAll(points4);
		points.addAll(points7);
		points.addAll(points6);
		
		return points;
	}
	
	public static void main(String[] args) {
		DrawCircle drawCircle = new DrawCircle(new Point(0, 0), 100);
		List<Point> points = drawCircle.draw();
		List<Polyline> polylines = new ArrayList<>();
		Polyline polyline = new Polyline(points);
		//Polyline simplifyLine1 = polyline.simplify_LightBar(10);
		//Polyline simplifyLine2 = polyline.simplify_LightBar(50);
		//polylines.add(simplifyLine2);
		//polylines.add(simplifyLine1);
		polylines.add(polyline);
		System.out.println("size: "+points.size());
		PaintVector.createAndShowGUIPlus(null,polylines, null);
	}
}
