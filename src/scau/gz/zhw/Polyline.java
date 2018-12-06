package scau.gz.zhw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import math.Vector2D;


/**
 * ������
 * @author Administrator
 *
 */
public class Polyline extends Vector{
	private List<Point> points ;
	private Point start;
	private Point end;
	private static boolean debug = false;

	public Polyline(List<Point> points) {
		this.points = points;
		start = points.get(0);
		end = points.get(points.size()-1);
	}
	
	public Polyline(ArrayList<Line> lines) {
		List<Point> points = new ArrayList<>();
		for(Line line:lines) {
			Point start = line.getStart();
			Point end = line.getEnd();
			points.add(start);
			points.add(end);
		}
		removeDuplicateWithOrder(points);
		this.points = new ArrayList<>(points);
		start = this.points.get(0);
		end = this.points.get(points.size()-1);
	}
	
	public static void removeDuplicateWithOrder(List<Point> list) {
        Set<Point> set = new HashSet<Point>();
        List<Point> newList = new ArrayList<Point>();
        for (Iterator<Point> iter = list.iterator(); iter.hasNext();) {
        	Point element = iter.next();
            if (set.add(element))
                newList.add(element);
        }
        list.clear();
        list.addAll(newList);
        //System.out.println(" remove duplicate " + list);
    }
	
	public Polyline getReversePolyline() {
		List<Point> reversePoint = new ArrayList<>();
		for(int i=points.size()-1;i>=0;i--) {
			Point point = new Point(points.get(i).getX(), points.get(i).getY());
			reversePoint.add(point);
		}
		return new Polyline(reversePoint);
	}
	
	
	public Line getStartLine() {
		for(Line line:this.getLines()) {
			if(line.getStart().isEqual(start)) {
				return line;
			}
		}
		return new Line(start,points.get(points.indexOf(start)+1));
	}
	
	public Line getEndLine() {
		for(Line line:this.getLines()) {
			if(line.getEnd().isEqual(end)) {
				return line;
			}
		}
		return new Line(points.get(points.indexOf(end)-1),end);
	}

	
	public Point getStart() {
		return start;
	}
	
	public Point getEnd() {
		return end;
	}
	
	public List<Point> getPoints() {
		return points;
	}
	
	public void setPoints(List<Point> points) {
		this.points = points;
	}
	
	
	public double getLength() {
		double length = 0;
		for(int i=0;i<points.size();i++) {
			if(i==points.size()-1) {
				break;
			}
			Line line = new Line(points.get(i), points.get(i+1));
			length+=line.getLength();
		}
		return length;
	}
	
	public int size() {
		return points.size();
	}
	
	/**
	 * ��ȡÿһ���߶εļ��ϣ�������ظ�
	 * @return
	 */
	public List<Line> getLines(){
		List<Line> lines = new ArrayList<>();
		for(int i=0;i<points.size();i++) {
			if(i==points.size()-1) {
				break;
			}
			Line line = new Line(points.get(i), points.get(i+1));
			lines.add(line);
		}
		return lines;
	}

	@Override
	protected String getType() {
		// TODO Auto-generated method stub
		return "��";
		
	}
	
	//�Ż����� ������˹�տ��㷨
	/**
	 * �����м�㵽��β������ߵľ�������ֵ�Ĺ�ϵ�ж��Ƿ���ĳ��
	 * @param threshold ��ֵԽС�������ĵ�Խ�࣬��ϡ�̶ȵͣ���ֵԽ��ɾ���ĵ�Խ�࣬��ϡ�̶ȸ�
	 * @return
	 */
	public Polyline simplify_Douglas_Peucker(double threshold) {
		//��ʼ��
		for(Point point:this.getPoints()) {
			point.setEnable(true);
		}
		List<Point> selectedPoints = new ArrayList<>();
		Collections.addAll(selectedPoints,  new  Point[this.points.size()]); 
		Collections.copy(selectedPoints, this.points);
		simplify(selectedPoints, threshold);
		//�����ɾ���ĵ㣬ȫ��ɾ��
		Iterator<Point> iterator = selectedPoints.iterator();
		while (iterator.hasNext()) {
			Point point = (Point) iterator.next();
			if(!point.isEnable()) {
				iterator.remove();
			}
		}
		if(debug) {
			System.out.println("������ĵ㣺");
			for(Point p:selectedPoints) {
				System.out.println(p.toString());
			}
		}
		//�ָ�
		for(Point point:this.getPoints()) {
			point.setEnable(true);
		}
		
		return new Polyline(selectedPoints);
	}
	
	/**
	 * 
	 * @param polyline ����
	 * @param threshold ��ֵԽС�������ĵ�Խ�࣬��ϡ�̶ȵͣ���ֵԽ��ɾ���ĵ�Խ�࣬��ϡ�̶ȸ�
	 * @return
	 */
	public static Polyline simplify_Douglas_Peucker(Polyline polyline,double threshold) {
		return polyline.simplify_Douglas_Peucker(threshold);
	}
	
	//��ϡ��
	private static void simplify(List<Point> points,double threshold) {
		//threshold = Math.abs(threshold);
		if(points.size()<3) {
			return;
		}
		Line line = new Line(points.get(0),points.get(points.size()-1));
		Point maxPoint = null;
		double maxDis = 0;
		//System.out.println(points.size());
		for(Point point:points) {
			double dis =  line.getDistancefromPoint(point);
			if(debug) {
				System.out.println(point.toString());
			}
			//System.out.println(dis);
			if(Double.doubleToLongBits(dis)>Double.doubleToLongBits(maxDis) && line.getStart()!=point && line.getEnd()!=point) {
				maxDis = dis;
				maxPoint = point;
			}
			
		}
		if(debug) {
			System.out.println("max:"+maxDis);
			System.out.println("��ֵ:"+threshold);
			System.out.println("������������β��");
		}
		//������������β��
		if(maxDis>threshold && maxPoint!=null) {
			if(debug) {
				System.out.println("����:"+maxPoint.toString());
			}
			
			//�漰��ɾ��Ԫ�ز�������for��whileѭ������Ϊ��ı�Ԫ�ص�λ��
			Iterator<Point> iterator = points.iterator();
			while (iterator.hasNext()) {
				Point p = (Point) iterator.next();
				if(!(p==line.getStart()||p==line.getEnd()||p==maxPoint)) {
					//��ǲ����ã�����ָɾ��
					//��Ϊ����Ҫ�������Ҳ��ֵĵ�����һ���ļ򻯣����������������ɾ������ ��ֹ��gc��
					p.setEnable(false);
					if(debug) {
						System.out.println("ɾ���ĵ㣺"+p.toString());
					}
				}else {
					p.setEnable(true);
					if(debug) {
						System.out.println("�����ĵ㣺"+p.toString());
					}
				}
			}
			//��maxPoint�����߷�Ϊ�����ֽ��ż�
			List<Point> leftPart = points.subList(0, points.indexOf(maxPoint)+1);
			List<Point> rightPart = points.subList(points.indexOf(maxPoint), points.size());
			if(debug) {
				System.out.println(String.format("left [1]-[%d]", points.indexOf(maxPoint)+1));
				System.out.println(String.format("right [%d]-[%d]", points.indexOf(maxPoint)+1, points.size()));
			}
			simplify(leftPart, threshold);
			simplify(rightPart, threshold);
		//ֻ������β��
		}else {
			Iterator<Point> iterator = points.iterator();
			while (iterator.hasNext()) {
				Point p = (Point) iterator.next();
				if(!(p.equals(line.getStart())||p.equals(line.getEnd()))) {
					p.setEnable(false);
					if(debug) {
						System.out.println("ɾ���ķ���β�㣺"+p.toString());
					}
				}else {
					if(debug) {
						System.out.println("��������β�㣺"+p.toString());
					}
					p.setEnable(true);
				}
			}
		}
	}

	/**
	 * �������Ż�����
	 * @param caliber �ھ�
	 * @return
	 */
	public Polyline simplify_LightBar(double caliber) {
		if(caliber<=0)
			return null;
		if(this.points.size()<2) {
			return this;
		}
		//������±߽�
		List<Point> points = this.getPoints();
		Point p1 = points.get(0);
		Point p2 = points.get(1);
		Line line = new Line(p1,p2);
		double len = line.getLength();
		double angle1 = Math.toDegrees(Math.atan2(.5*caliber,len));
		double angle2 = line.getVector2D().getAngle();
		
		//������±߽�
		Line down = new Line(angle2-angle1, p1);
		//������ϱ߽�
		Line up = new Line(angle1+angle2,p1);
		

//		//�������a1,a2����
//		//p1p2ֱ�ߵķ���ʸ��
//		Vector2D n = line.getN();
//		//������ֱƽ���ߵĴ���
//		Line l = new Line(n,p2);
//		Point a1 = l.intercourse(down);
//		Point a2 = l.intercourse(up);
		for(int i=2;i<points.size();i++) {
			Point p = points.get(i);
			//�����һ�����ڹ����ڣ���ɾ����һ���㣬��ǰ��Ϊ��p2
			//������ڣ�������һ���㣬����һ����Ϊ��p1
			if(isInLightBar(up, down, p)) {
				points.get(i-1).setEnable(false);
				p2=p;
				//��ǰ����p1�Ĵ���
				Line line2 = new Line(p1,p2);
				Vector2D nn = line.getN();
				Line line3 = new Line(nn,p);
				//�����µĹ���
				double length =  line2.getLength();
				double angle11 = Math.toDegrees(Math.atan(.5*caliber/length));
				double angle22 = line2.getVector2D().getAngle();
				Line newDown = new Line(angle22-angle11, p1);
				Line newUp = new Line(angle11+angle22,p1);
				//��ǰ����p1�����ߵĴ������¹����Ľ���
				Point b1 = line3.intercourse(newDown);
				Point b2 = line3.intercourse(newUp);
				//����¹����Ľ����Ƿ���ԭ������
				//����ھ�ʹ���¹��������ھ͹�����һ������
				if(isInLightBar(up, down, b1) && isInLightBar(up, down, b2)) {
					down = newDown;
					up = newUp;
				}else {
					//ֻ��b1�ڹ�����
					if(isInLightBar(up, down, b1)) {
						down = new Line(p1,b1);
					}
					if(isInLightBar(up, down, b2)) {
						up = new Line(p1,b2);
					}
				}
				
			}else {
				points.get(i-1).setEnable(true);
				p1=points.get(i-1);
				p2=points.get(i);
				line = new Line(p1,p2);
				len = line.getLength();
				angle1 = Math.toDegrees(Math.atan(.5*caliber/len));
				angle2 = line.getVector2D().getAngle();
				//��������±߽�
				down = new Line(angle2-angle1, p1);
				up = new Line(angle1+angle2,p1);
			}
			
			
			
		}
		
		
//		List<Line> lines = new ArrayList<>();
//		lines.add(line);
//		lines.add(down);
//		lines.add(up);
//		lines.add(l);
//		List<Point> points2 = new ArrayList<>();
//		points2.add(a1);
//		points2.add(a2);
//		System.out.println(isInLightBar(up, down, a2));
//		PaintVector.createAndShowGUI(points2, lines, null);
		
		
		List<Point> selectedPoints = new ArrayList<>();
		Collections.addAll(selectedPoints,  new  Point[this.points.size()]); 
		Collections.copy(selectedPoints, this.points);
		Iterator<Point> iterator = selectedPoints.iterator();
		while (iterator.hasNext()) {
			Point point = (Point) iterator.next();
			if(!point.isEnable()) {
				iterator.remove();
			}
		}
		
		return new Polyline(selectedPoints);
	}
	/**
	 * �жϵ��Ƿ������
	 * @param up
	 * @param down
	 * @param point
	 * @return
	 */
	private static boolean isInLightBar(Line up,Line down,Point point) {
		Point start = up.getStart();
		Line line = new Line(start,point);
		Vector2D upVector = up.getVector2D();
		Vector2D downVector = down.getVector2D();
		Vector2D target = line.getVector2D();
		//����ʸ���Ĳ���жϼ���
		if(target.crossProduct(upVector)>=0 && target.crossProduct(downVector) <=0) {
			return true;
		}
		return false;
	}
	
	@Override
	public void showGUI() {
		// TODO Auto-generated method stub
		List<Polyline> lPolylines = new ArrayList<>();
		lPolylines.add(this);
		PaintVector.createAndShowGUIPlus(null, lPolylines, null);
	}
	
	
	
	public static void main(String[] args) {
		
		Point a = new Point(-150,50);
		Point b = new Point(-120, -30);
		Point c = new Point(-50, 80);
		Point d =new Point(-23, -42);
		Point e = new Point(10, 53);
		Point f = new Point(25, -23);
		Point i = new Point(30, 83);
		Point g = new Point(55, -43);
		Point h = new Point(60, 13);
		Point q = new Point(63, -33);
		Point j = new Point(90, 24);
		Point k = new Point(100, -100);
		
		List<Point> points = new ArrayList<>();
		points.add(a);
		points.add(b);
		points.add(c);
		points.add(d);
		points.add(e);
		points.add(f);
		points.add(i);
		points.add(g);
		points.add(h);
		points.add(q);
		points.add(j);
		points.add(k);
		
		Polyline polyline = new Polyline(points);
		System.out.println(polyline.size());
		
		List<Line> lines = polyline.getLines();
		System.out.println(lines.size());
		for(Line line:lines) {
			System.out.println(line);
		}
		
		
		
		Polyline simplifyLine = polyline.simplify_Douglas_Peucker(20);
		
		List<Polyline> polylines = new ArrayList<>();
		//polylines.add(polyline);
		//polylines.add(simplifyLine);
		//PaintVector.createAndShowGUIPlus(polyline.getPoints(), polylines, null);
		polyline.showGUI();
		
		
		Polyline simplifyLine2 = polyline.simplify_LightBar(1000);
		polylines.add(simplifyLine);
		PaintVector.createAndShowGUIPlus(polyline.getPoints(), polylines, null);
		polylines.clear();
		polylines.add(simplifyLine2);
		PaintVector.createAndShowGUIPlus(polyline.getPoints(), polylines, null);
	}
	

}
