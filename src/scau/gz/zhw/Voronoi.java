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
	//原始数据点
	private Point[] points;
	//voronois多边形数据
	private List<Polygon> voronois = new ArrayList<>();
	//delaunay三角网数据
	private Delaunay delaunay;
	//delaunay三角形外心数据
	private List<Point> outsideCircle = new ArrayList<>();
	//记录三角形的关系
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
	 * 获取原数据点
	 * @return
	 */
	public Point[] getPoints() {
		return points;
	}
	
	/**
	 * 获取外心数据
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
	 * waston法构建delaunay算法，优点：简单
	 * 缺点：未考虑增量点对原三角形的影响,点数多会出现“交叉现象”
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
		//记录点对，构成三角形
		Set<Set<Point>> edgeBuffer = new HashSet<>();
		//List<Edge> edgesBuffer = new ArrayList<>();
		//获得点集的包围盒
		double Xmax = getMaxOrMin(X, maxComparetor);
		double Ymax = getMaxOrMin(Y, maxComparetor);
		double Xmin = getMaxOrMin(X, minComparetor);
		double Ymin = getMaxOrMin(Y, minComparetor);
		//构造超级三角形,包含所有点集
		double halfX = (Xmax - Xmin) /2;
		Point top = new Point((Xmin+Xmax)/2, Ymax+(Ymax - Ymin)*10);
		Point right = new Point(Xmax+halfX*10+1, Ymin-1);
		Point left = new Point(Xmin-halfX*10-1, Ymin-1);
		scau.gz.zhw.Triangle superTriangle = new scau.gz.zhw.Triangle(top,left,right);
		//将超级三角形放入待选三角形中
		temTriangle.add(superTriangle);
		
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
		
		List<Point> points = Arrays.asList(this.points);
		Collections.sort(points,XYcomparator1);
		
		//遍历每一个点
		for(Point point:points) {
			//初始化edgeBuffer
			System.out.println("开始遍历 "+point);
			System.out.println("当前备选三角形数目：" +temTriangle.size());
			
			if(!edgeBuffer.isEmpty()) {
				
				edgeBuffer.clear();
				
			}
			java.util.Iterator<scau.gz.zhw.Triangle> iterator = temTriangle.iterator();
			while(iterator.hasNext()) {
				//如果点在三角形的外接圆内，则肯定不是Delaunay三角形，需要构造三角形进一步判断
				scau.gz.zhw.Triangle triangle = iterator.next();
				if(isPointInCircle(triangle, point)) {
					
					//将三角形的三点保存在buffer中
//					edgeBuffer.add(triangle.getPoints()[0]);
//					edgeBuffer.add(triangle.getPoints()[1]);
//					edgeBuffer.add(triangle.getPoints()[2]);
					//将三角形的点对存入Buffer
					for(Point p :triangle.getPoints()) {
						Set<Point> pp = triangle.getOpposite(p);
						if(edgeBuffer.contains(pp)) {
							//去重处理
							edgeBuffer.remove(pp);
						}else {
							edgeBuffer.add(pp);
						}
					}
					
					//备选三角形中删除该三角形
					//temTriangle.remove(triangle);
					System.out.println("点在外接圆内");
					iterator.remove();
					//temTriangle.addAll(getTemTriangle(point, triangle));
					
				}else {
					//点在三角形外接圆的外侧或者圆上的时候
					//判断点是否在圆的右侧
					if(triangle.getOutside().getX()+triangle.getOutsideRadius()<point.getX()) {
						//可以肯定这个三角形为delaunay三角形
						System.out.println("这个三角形为Delaunay三角形");
						delaunay.add(triangle);
						//temTriangle.remove(triangle);
						iterator.remove();
						continue;
					}else {
						//不确定三角形,先跳过
						System.out.println("还不能确定这个三角形");
						continue;
					}
				}
				
			}
			//根据当前点,重构三角形
//			System.out.println("当前边缓存中的点对数为："+edgeBuffer.size());
//			if(edgeBuffer.size()==3 ) {
//			Triangle triangle = new Triangle(edgeBuffer.get(0),edgeBuffer.get(1),edgeBuffer.get(2));
//			temTriangle.addAll(getTemTriangle(point, triangle));
//			}else if(!edgeBuffer.isEmpty()) {
				//说明不止一个三角形了，需要去重复边处理
//				System.out.println("去重复前："+edgeBuffer.size());
//				List<Point> repeatList=removeDuplicateWithOrder(edgeBuffer);
				//dedup(edgeBuffer);
//				System.out.println("去重复后："+edgeBuffer.size());
				//缓存点要进行排序排序！！
//				Collections.sort(edgeBuffer,new Comparator<Point>() {
//				///18/10/11
				//怎么去除重复边？
				
//					@Override
//					public int compare(Point a, Point b) {
//						// TODO Auto-generated method stub
//						return -(int)(a.getAngle()-b.getAngle());
//					}
//					
//				});
	
				//Collections.sort(edgeBuffer,XYcomparator1);
				
//				for(int i=0;i<edgeBuffer.size();i++) {
//					//一定是相邻的三角形互连，不能跨过任何点
//					Point p1 = edgeBuffer.get(i%edgeBuffer.size());
//						Point p2 = edgeBuffer.get((i+1)%edgeBuffer.size());
//						Triangle triangle = new Triangle(p1,p2,point);
//						temTriangle.addAll(getTemTriangle(point, triangle));
//					
//					
//				}
//			}
			//重构三角形
			for(Set<Point> vetices : edgeBuffer) {
				vetices.add(point);
				Point[] veticess = vetices.toArray(new Point[vetices.size()]);
				temTriangle.add(new scau.gz.zhw.Triangle(veticess));
			}
		
		}
		delaunay.addAll(temTriangle);
		//去掉与超级三角形相关联的三角形
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
		
		//获得点集的包围盒
		double Xmax = getMaxOrMin(X, maxComparetor);
		double Ymax = getMaxOrMin(Y, maxComparetor);
		double Xmin = getMaxOrMin(X, minComparetor);
		double Ymin = getMaxOrMin(Y, minComparetor);
		//构造超级三角形,包含所有点集
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
		//标识已经遍历过的三角形的顶点
		HashSet<NPoint> done = new HashSet<NPoint>(triangulation.getInitTriangle());
		for(Triangle triangle:triangulation) {
			for(NPoint point:triangle) {
				if(done.contains(point)) {
					continue;
				}
				done.add(point);
				List<Triangle> list = triangulation.surroundingTriangles(point, triangle);
				//有几个三角形就有几个外心
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
	
	
	
	//判断点是否在外接圆内
	private boolean isPointInCircle(scau.gz.zhw.Triangle triangle,Point point) {
		Point outside = triangle.getOutside();
		double dis = new Line(outside, point).getLength();
		return Double.doubleToLongBits(dis)<Double.doubleToLongBits(triangle.getOutsideRadius());
	}
	
	
	
	
	//获取随机数
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
		//随机生成数据
		//圆形区域
		double radius = 180;
		double radiusSqure = radius*radius;
		for(int i=0;i<len;) {
			double x = next(-radius, radius);
			double y = next(-radius, radius);
			if(x*x+y*y<=radiusSqure) {
				Point p = new Point(x, y);
				//System.out.println(p);
				points[i]=p;
				//System.out.println("生成点："+p);
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
			System.out.println("原始点： "+p);
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
