package scau.gz.zhw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;

import voronoi.NPoint;
import voronoi.Triangle;
import voronoi.Triangulation;




public class Voronoi {
	//ԭʼ���ݵ�
	private Point[] points;
	//voronois���������
	private List<Polygon> voronois = new ArrayList<>();
	//delaunay����������
	private Delaunay delaunay;
	//delaunay��������������
	private List<Point> outsideCircle = new ArrayList<>();
	//��¼�����εĹ�ϵ
	private Triangulation triangulation;
	

	
	public Voronoi (Point...points) {
		this.points = points;
		getVoronois();
	}
	
	private double getMaxOrMin(double[] num,BiFunction<Double, Double, Boolean> function) {
		double x = num[0];
		for(double xx : num) {
			if(function.apply(xx, x)) {
				x=xx;
			}
		}
		return x;
	}
	
	/**
	 * ��ȡԭ���ݵ�
	 * @return
	 */
	public Point[] getPoints() {
		return points;
	}
	
	/**
	 * ��ȡ��������
	 * @return
	 */
	public List<Point> getOutsideCircle() {
		if(outsideCircle.isEmpty()) {
			for(Triangle triangle:triangulation) {
				NPoint point = triangle.getCircumcenter();
				outsideCircle.add(new Point(point.coord(0),point.coord(1)));
			}
		}
		return outsideCircle;
	}
	
	/**
	 * waston������delaunay�㷨���ŵ㣺��
	 * ȱ�㣺δ�����������ԭ�����ε�Ӱ��,���������֡���������
	 * @return
	 */
	public Delaunay getDelaunay1() {
		double [] X = new double[points.length];
		double [] Y = new double[points.length];
		for(int i=0;i<points.length;i++) {
			X[i] = points[i].getX();
			Y[i] = points[i].getY();
		}
		BiFunction<Double, Double, Boolean> maxComparetor = new BiFunction<Double, Double, Boolean>() {

			@Override
			public Boolean apply(Double arg0, Double arg1) {
				// TODO Auto-generated method stub
				return arg0>arg1;
			}
			
		};

		BiFunction<Double, Double, Boolean> minComparetor = new BiFunction<Double, Double, Boolean>() {


			@Override
			public Boolean apply(Double arg0, Double arg1) {
				// TODO Auto-generated method stub
				return arg0<arg1;
			}
			
		};
		
		List<scau.gz.zhw.Triangle> temTriangle = new ArrayList<>();
		List<scau.gz.zhw.Triangle> delaunay = new ArrayList<>();
		//��¼��ԣ�����������
		Set<Set<Point>> edgeBuffer = new HashSet<>();
		//List<Edge> edgesBuffer = new ArrayList<>();
		//��õ㼯�İ�Χ��
		double Xmax = getMaxOrMin(X, maxComparetor);
		double Ymax = getMaxOrMin(Y, maxComparetor);
		double Xmin = getMaxOrMin(X, minComparetor);
		double Ymin = getMaxOrMin(Y, minComparetor);
		//���쳬��������,�������е㼯
		double halfX = (Xmax - Xmin) /2;
		Point top = new Point((Xmin+Xmax)/2, Ymax+(Ymax - Ymin)*10);
		Point right = new Point(Xmax+halfX*10+1, Ymin-1);
		Point left = new Point(Xmin-halfX*10-1, Ymin-1);
		scau.gz.zhw.Triangle superTriangle = new scau.gz.zhw.Triangle(top,left,right);
		//�����������η����ѡ��������
		temTriangle.add(superTriangle);
		
		//��������
		Comparator<Point> XYcomparator1 = new Comparator<Point>() {
			//���ȶ�X��������x������y��������
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
		
		List<Point> points = Arrays.asList(this.points);
		Collections.sort(points,XYcomparator1);
		
		//����ÿһ����
		for(Point point:points) {
			//��ʼ��edgeBuffer
			System.out.println("��ʼ���� "+point);
			System.out.println("��ǰ��ѡ��������Ŀ��" +temTriangle.size());
			
			if(!edgeBuffer.isEmpty()) {
				
				edgeBuffer.clear();
				
			}
			java.util.Iterator<scau.gz.zhw.Triangle> iterator = temTriangle.iterator();
			while(iterator.hasNext()) {
				//������������ε����Բ�ڣ���϶�����Delaunay�����Σ���Ҫ���������ν�һ���ж�
				scau.gz.zhw.Triangle triangle = iterator.next();
				if(isPointInCircle(triangle, point)) {
					
					//�������ε����㱣����buffer��
//					edgeBuffer.add(triangle.getPoints()[0]);
//					edgeBuffer.add(triangle.getPoints()[1]);
//					edgeBuffer.add(triangle.getPoints()[2]);
					//�������εĵ�Դ���Buffer
					for(Point p :triangle.getPoints()) {
						Set<Point> pp = triangle.getOpposite(p);
						if(edgeBuffer.contains(pp)) {
							//ȥ�ش���
							edgeBuffer.remove(pp);
						}else {
							edgeBuffer.add(pp);
						}
					}
					
					//��ѡ��������ɾ����������
					//temTriangle.remove(triangle);
					System.out.println("�������Բ��");
					iterator.remove();
					//temTriangle.addAll(getTemTriangle(point, triangle));
					
				}else {
					//�������������Բ��������Բ�ϵ�ʱ��
					//�жϵ��Ƿ���Բ���Ҳ�
					if(triangle.getOutside().getX()+triangle.getOutsideRadius()<point.getX()) {
						//���Կ϶����������Ϊdelaunay������
						System.out.println("���������ΪDelaunay������");
						delaunay.add(triangle);
						//temTriangle.remove(triangle);
						iterator.remove();
						continue;
					}else {
						//��ȷ��������,������
						System.out.println("������ȷ�����������");
						continue;
					}
				}
				
			}
			//���ݵ�ǰ��,�ع�������
//			System.out.println("��ǰ�߻����еĵ����Ϊ��"+edgeBuffer.size());
//			if(edgeBuffer.size()==3 ) {
//			Triangle triangle = new Triangle(edgeBuffer.get(0),edgeBuffer.get(1),edgeBuffer.get(2));
//			temTriangle.addAll(getTemTriangle(point, triangle));
//			}else if(!edgeBuffer.isEmpty()) {
				//˵����ֹһ���������ˣ���Ҫȥ�ظ��ߴ���
//				System.out.println("ȥ�ظ�ǰ��"+edgeBuffer.size());
//				List<Point> repeatList=removeDuplicateWithOrder(edgeBuffer);
				//dedup(edgeBuffer);
//				System.out.println("ȥ�ظ���"+edgeBuffer.size());
				//�����Ҫ�����������򣡣�
//				Collections.sort(edgeBuffer,new Comparator<Point>() {
//				///18/10/11
				//��ôȥ���ظ��ߣ�
				
//					@Override
//					public int compare(Point a, Point b) {
//						// TODO Auto-generated method stub
//						return -(int)(a.getAngle()-b.getAngle());
//					}
//					
//				});
	
				//Collections.sort(edgeBuffer,XYcomparator1);
				
//				for(int i=0;i<edgeBuffer.size();i++) {
//					//һ�������ڵ������λ��������ܿ���κε�
//					Point p1 = edgeBuffer.get(i%edgeBuffer.size());
//						Point p2 = edgeBuffer.get((i+1)%edgeBuffer.size());
//						Triangle triangle = new Triangle(p1,p2,point);
//						temTriangle.addAll(getTemTriangle(point, triangle));
//					
//					
//				}
//			}
			//�ع�������
			for(Set<Point> vetices : edgeBuffer) {
				vetices.add(point);
				Point[] veticess = vetices.toArray(new Point[vetices.size()]);
				temTriangle.add(new scau.gz.zhw.Triangle(veticess));
			}
		
		}
		delaunay.addAll(temTriangle);
		//ȥ���볬���������������������
		java.util.Iterator<scau.gz.zhw.Triangle> iterator = delaunay.iterator();
		while (iterator.hasNext()) {
			scau.gz.zhw.Triangle triangle = (scau.gz.zhw.Triangle) iterator.next();
			if(triangle.isContain(top) || triangle.isContain(right) || triangle.isContain(left)) {
				iterator.remove();
			}
		}
		return new Delaunay(delaunay);
	}
	
	
	public Delaunay getDelaunay2() {
		double [] X = new double[points.length];
		double [] Y = new double[points.length];
		for(int i=0;i<points.length;i++) {
			X[i] = points[i].getX();
			Y[i] = points[i].getY();
		}
		BiFunction<Double, Double, Boolean> maxComparetor = new BiFunction<Double, Double, Boolean>() {

			@Override
			public Boolean apply(Double arg0, Double arg1) {
				// TODO Auto-generated method stub
				return arg0>arg1;
			}
			
		};

		BiFunction<Double, Double, Boolean> minComparetor = new BiFunction<Double, Double, Boolean>() {


			@Override
			public Boolean apply(Double arg0, Double arg1) {
				// TODO Auto-generated method stub
				return arg0<arg1;
			}
			
		};
		
		//��õ㼯�İ�Χ��
		double Xmax = getMaxOrMin(X, maxComparetor);
		double Ymax = getMaxOrMin(Y, maxComparetor);
		double Xmin = getMaxOrMin(X, minComparetor);
		double Ymin = getMaxOrMin(Y, minComparetor);
		//���쳬��������,�������е㼯
		double halfX = (Xmax - Xmin) /2;
		Point top = new Point((Xmin+Xmax)/2, Ymax+(Ymax - Ymin)*10);
		Point right = new Point(Xmax+halfX*10+1, Ymin-1);
		Point left = new Point(Xmin-halfX*10-1, Ymin-1);
		Triangle superTriangle = new Triangle(new NPoint(top.getX(),top.getY()),new NPoint(right.getX(),right.getY()),new NPoint(left.getX(),left.getY()));
		Triangulation triangulation = new Triangulation(superTriangle);
		this.triangulation = triangulation;
		for(Point point:points) {
			NPoint nPoint = new NPoint(point.getX(),point.getY());
			triangulation.delaunayPlace(nPoint);
		}
		
		Iterator<Triangle> iterator = triangulation.iterator();
		List<scau.gz.zhw.Triangle> delaunay = new ArrayList<>();
		while (iterator.hasNext()) {
			Triangle triangle = (Triangle) iterator.next();
			NPoint p0 = triangle.get(0);
			NPoint p1 = triangle.get(1);
			NPoint p2 = triangle.get(2);
			Point pp0 = new Point(p0.coord(0), p0.coord(1));
			Point pp1 = new Point(p1.coord(0), p1.coord(1));
			Point pp2 = new Point(p2.coord(0), p2.coord(1));
			NPoint c = triangle.getCircumcenter();
			Point outside = new Point(c.coord(0), c.coord(1));
            double radius = c.subtract(triangle.get(0)).magnitude();
			scau.gz.zhw.Triangle tri = new scau.gz.zhw.Triangle(pp0,pp1,pp2);
			tri.setOutside(outside);
			tri.setOutsideRadius(radius);
			delaunay.add(tri);
		}
		
		java.util.Iterator<scau.gz.zhw.Triangle> triItr = delaunay.iterator();
		while (triItr.hasNext()) {
			scau.gz.zhw.Triangle triangle = (scau.gz.zhw.Triangle) triItr.next();
			if(triangle.isContain(top) || triangle.isContain(right) || triangle.isContain(left)) {
				triItr.remove();
			}
		}
		this.delaunay = new Delaunay(delaunay);
		return this.delaunay;
	}
	
	
	public List<Polygon> getVoronois() {
		getDelaunay2();
		//��ʶ�Ѿ��������������εĶ���
		HashSet<NPoint> done = new HashSet<NPoint>(triangulation.getInitTriangle());
		for(Triangle triangle:triangulation) {
			for(NPoint point:triangle) {
				if(done.contains(point)) {
					continue;
				}
				done.add(point);
				List<Triangle> list = triangulation.surroundingTriangles(point, triangle);
				//�м��������ξ��м�������
				Point[] points = new Point[list.size()];
				int i=0;
				for(Triangle tri :list) {
					points[i++] = new Point(tri.getCircumcenter().coord(0), tri.getCircumcenter().coord(1));
				}
				 this.voronois.add(new Polygon(points, true));
			}
		}
		return  this.voronois;
	}
	
	
	
	//�жϵ��Ƿ������Բ��
	private boolean isPointInCircle(scau.gz.zhw.Triangle triangle,Point point) {
		Point outside = triangle.getOutside();
		double dis = new Line(outside, point).getLength();
		return Double.doubleToLongBits(dis)<Double.doubleToLongBits(triangle.getOutsideRadius());
	}
	
	
	
	
	//��ȡ�����
	public static int next(int max,int min) {
		Random random = new Random();
		return random.nextInt(max-min+1)+min;
	}

	public static double next(double max,double min) {
		Random random = new Random();
		return random.nextDouble()*(max-min)+min;
	}
	
	public static void main(String[] args) {

		int len = 10;
		Point[] points = new Point[len]; 
		//�����������
		//Բ������
		double radius = 180;
		double radiusSqure = radius*radius;
		for(int i=0;i<len;) {
			double x = next(-radius, radius);
			double y = next(-radius, radius);
			if(x*x+y*y<=radiusSqure) {
				Point p = new Point(x, y);
				//System.out.println(p);
				points[i]=p;
				//System.out.println("���ɵ㣺"+p);
				i++;
			}
		}
//		Point a,b,c,d,e;
//		a = new Point(53.484506,-49.644315);
//		b = new Point(84.476428,52.886434);
//		c = new Point(-63.998504,1.355077);
//		d = new Point(-34.952220,19.513382);
//		e = new Point(10, 10);
//		Point[] points = {a,b,c,d,e};
		for(Point p:points) {
			System.out.println("ԭʼ�㣺 "+p);
		}
		Voronoi voronoi = new Voronoi(points);
		//Delaunay delaunay=voronoi.getDelaunay1();
		Delaunay delaunay2 = voronoi.getDelaunay2();
		//PaintVector.createAndShowGUIDrawDelauary(delaunay);
		PaintVector.createAndShowGUIDrawDelauary(delaunay2);
		List<Polygon> voronoisPolygon = voronoi.getVoronois();
		System.out.println(voronoisPolygon.size());
		System.out.println(voronoi.getOutsideCircle().size());
		PaintVector.createAndShowGUI(Arrays.asList(voronoi.getPoints()), null, voronoi.getVoronois());
	}
}
