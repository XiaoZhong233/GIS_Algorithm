package scau.gz.zhw;

import java.util.ArrayList;
import java.util.List;

import math.Vector2D;

public class Line {
	private Point start;
	private Point end;
	//用于构建隐式二维直线方程的系数
	//f(x,y) = ax+by+c=0
	//(a,b)为法线矢量，在通过两点构建方向矢量即可通过
	//nl * （P-P0）= ax+by-Nl*P0=0构建出直线方程
	//(a,b) = (-Yv,Xv) v->方向矢量
	private double a;
	private double b;
	private double c;
	//方向矢量
	private Vector2D v;
	//法线矢量
	private Vector2D n;
	public enum direction{
		up,
		down,
		horizontal;
	}
	
	/**
	 * 
	 * @param p 起点
	 * @param q 终点
	 */
	public Line(Point p, Point q) {
		super();
		
		this.start = p;
		this.end = q;
		//方向向量
		Vector2D v = new Vector2D(p,q);
		this.v=v;
		a=-v.getY();
		b=v.getX();
		Vector2D n = new Vector2D(a,b);
		this.n=n;
		c=new Vector2D(p.getX(),p.getY()).dotProduct(n);
		//隐式方程规范化
		double len = n.getLength();
		a/=len;
		b/=len;
		c/=len;
		
		
	}
	
	/**
	 * 
	 * @param angle 直线方位角
	 * @param sPoint 起点
	 */
	public Line(double angle,Point sPoint) {
		//方向向量
		Vector2D Vl = new Vector2D(Math.cos(Math.toRadians(angle)),Math.sin(Math.toRadians(angle)));
		this.v=Vl;
		a=-Vl.getY();
		b=Vl.getX();
		//法线矢量
		Vector2D Vn = new Vector2D(a,b);
		this.n=Vn;
		c=new Vector2D(sPoint.getX(), sPoint.getY()).dotProduct(Vn);
		//隐式方程规范化
		double len = Vn.getLength();
		a/=len;
		b/=len;
		c/=len;
		Vector2D vlplus=Vl.multiply(200);
		this.start = sPoint;
		this.end = new Point(sPoint.getX()+vlplus.getX(), sPoint.getY()+vlplus.getY());
	}
	
	
	public Line(Vector2D vector2d,Point sPoint) {
		this.v=vector2d;
		a=-vector2d.getY();
		b=vector2d.getX();
		//法线矢量
		Vector2D n = new Vector2D(a, b);
		this.n = n;
		c=new Vector2D(sPoint.getX(),sPoint.getY()).dotProduct(n);
		//隐式方程规范化
		double len = n.getLength();
		a/=len;
		b/=len;
		c/=len;
		this.start = sPoint;
		this.end = new Point(sPoint.getX()+v.getX(), sPoint.getY()+v.getY());
		
	}
	/**
	 * 通过此构造方法构造的直线没有开始点和结束点
	 * @param a
	 * @param b
	 * @param c
	 */
	public Line(double a,double b,double c) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.v = new Vector2D(b,-a);
		this.n = new Vector2D(a,b);
		//隐式方程规范化
		double len = n.getLength();
		a/=len;
		b/=len;
		c/=len;
		
	}
	
	public double getSlope() {
		if(Double.doubleToLongBits(end.getX()-start.getX())==0) {
			throw new RuntimeException("斜率不存在");
		}
		return (end.getY()-start.getY())/(end.getX()-start.getX());
	}
	
	//获取Y轴截距
	public double getInterceptOfY() {
		return end.getY() - getSlope()*end.getX();
	}
	
	//获取X轴截距
	public double getInterceptOfX() {
		//如果Y轴截距为0，X轴截距也为0
		if(getInterceptOfY() == 0) {
			return 0;
		}
		//直线为水平则截距不存在;
		if(getSlope() == 0) {
			throw new RuntimeException("斜率为0,X轴截距不存在");
		}
		return getInterceptOfY()/getSlope();
	}
	
	public double getYByX(double X) {
		
		try {
			return X*getSlope()+getInterceptOfY();	
		} catch (Exception e) {
			// TODO: handle exception
			return end.getY();
		}
	}
	
	public double getXByY(double Y) {
		double X = start.getX();
		try {
			if(getSlope() == 0) {
				throw new RuntimeException("斜率为0,已知Y坐标不可求出X坐标");
			}
			return (Y-getInterceptOfY())/getSlope();
		} catch (Exception e) {
			// TODO: handle exception
			if(getSlope() == 0) {
				throw e;
			}
			return X;
		}
	}
	
	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public Point getEnd() {
		return end;
	}

	public void setEnd(Point end) {
		this.end = end;
	}

	public double getLength() {
		return Math.sqrt(Math.pow(start.getX()-end.getX(), 2)+Math.pow(start.getY()-end.getY(), 2));
	}
	
	public Vector2D getVector2D() {
		return getVector(start, end);
	}
	
	
	public double getA() {
		return a;
	}


	public double getB() {
		return b;
	}



	public double getC() {
		return c;
	}


	/**
	 * 求与另一直线的交点
	 * @param line 另一直线
	 * @return
	 */
	public Point intercourse(Line line) {
		if(Double.doubleToLongBits(a*line.b) == Double.doubleToLongBits(line.a *b)) {
			//throw new RuntimeException("直线平行或重叠");
		}
		double x = -(line.c*b-c*line.b)/(a*line.b-line.a*b);
		double y = -(line.c*a-c*line.a)/(b*line.a-line.b*a);
		return new Point(x, y);
	}
	
	/**
	 * 求距离为dis的平行线
	 * @param dis
	 * @return 0-位于直线上方的平行线，1-位于直线下方的平行线
	 */
	public Line[] getParallel(double dis) {
		double t1 = c+dis*Math.sqrt(a*a+b*b);
		double t2 = c-dis*Math.sqrt(a*a+b*b);
		Line line1 = new Line(this.a,this.b,t1);
		Line line2 = new Line(this.a,this.b,t2);
		Line[] lines = new Line[2];
		lines[0]=line1;
		lines[1]=line2;
		return lines;
	}
	
	
	/**
	 * 
	 * @return 返回该线段的方向矢量
	 */
	public Vector2D getV() {
		return v;
	}
	
	/**
	 * 
	 * @return 返回该线段的法线矢量
	 */
	public Vector2D getN() {
		return n;
	}
	
	
	public boolean isHorizontal() {
		Vector2D line = getVector2D();
		Vector2D xVector2d = new Vector2D(1,0);
		return line.crossProduct(xVector2d)==0;
	}
	
	
	public boolean isUp() {
		Vector2D line = getVector2D();
		Vector2D xVector2d = new Vector2D(1,0);
		return line.crossProduct(xVector2d)<0;
		
	}
	
	public boolean isDown() {
		Vector2D line = getVector2D();
		Vector2D xVector2d = new Vector2D(1,0);
		return line.crossProduct(xVector2d)>0;
	}
	
	
	public direction getDirection() {
		if(isHorizontal()) {
			return direction.horizontal;
		}
		if(isUp()) {
			return direction.up;
		}
		if(isDown()) {
			return direction.down;
		}
		return null;
	}
	
	private  Vector2D getVector(Point p,Point q) {
		double x = q.getX()-p.getX();
		double y = q.getY()-p.getY();
		Vector2D result = new Vector2D(x, y);
		return result;
	}
	
	
	/**
	 * 获取点到线段(或延长线)的距离
	 */
	public double getDistancefromPoint(Point point) {
		double x = point.getX();
		double y = point.getY();
		return Math.abs(x*a+y*b+c);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("start :%s end :%s", start.toString(),end.toString());
	}
	
	public void showGUI() {
		List<Line> lines = new ArrayList<>();
		lines.add(this);
		PaintVector.createAndShowGUI(null, lines, null);
	}
	
	public static void main(String[] args) {
		Point a = new Point(0, 0);
		Point b = new Point(100, 0);
		Point c = new Point(50, 0);
		Line line = new Line(a,b);
		System.out.println(line.getDistancefromPoint(c));
		line.showGUI();
	}
}
