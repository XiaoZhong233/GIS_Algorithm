package math;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import scau.gz.zhw.Line;
import scau.gz.zhw.Point;
import tanling.matplot_4j.d3d.base.speg.Point3D;
import tanling.matplot_4j.d3d.base.speg.Range;
import tanling.matplot_4j.d3d.facade.MatPlot3DMgr;

/**
 * 插值工具类
 * @author Administrator
 *
 */
public class Interpolation {
	
	public static Function<Point, Double> xFunction = new Function<Point,Double>() {

		@Override
		public Double apply(Point arg0) {
			// TODO Auto-generated method stub
			return arg0.getX();
		}
		
	};
	
	public static Function<Point, Double> yFunction = new Function<Point,Double>() {

		@Override
		public Double apply(Point arg0) {
			// TODO Auto-generated method stub
			return arg0.getY();
		}
		
	};
	
	public static Function<Point, Double> valueFunction = new Function<Point,Double>() {

		@Override
		public Double apply(Point arg0) {
			// TODO Auto-generated method stub
			return arg0.getValue();
		}
		
	};
	
	public static double sum(Function<Point, Double> function,Point...points) {
		double sum = 0;
		for(Point point: points) {
			sum+=function.apply(point);
		}
		return sum;
	}
	
	
	
	
	//一次趋势面插值(2维)
	public static Function<Point,Double> trendSurface1D(Point ...points){
		int len = points.length;
		double sumX = sum(xFunction, points); 
		double sumY = sum(yFunction, points);
		double sumZ = sum(valueFunction, points);
		double sumX2 = sum(new Function<Point,Double>() {

			@Override
			public Double apply(Point arg0) {
				// TODO Auto-generated method stub
				return arg0.getX()*arg0.getX();
			}
			
		}, points);
		double sumY2 = sum(new Function<Point,Double>() {

			@Override
			public Double apply(Point arg0) {
				// TODO Auto-generated method stub
				return arg0.getY()*arg0.getY();
			}
			
		}, points);
		double sumXY = sum(new Function<Point,Double>() {

			@Override
			public Double apply(Point arg0) {
				// TODO Auto-generated method stub
				return arg0.getX()*arg0.getY();
			}
			
		}, points);
		double sumXZ = sum(new Function<Point,Double>() {

			@Override
			public Double apply(Point arg0) {
				// TODO Auto-generated method stub
				return arg0.getX()*arg0.getValue();
			}
			
		}, points);
		double sumYZ = sum(new Function<Point,Double>() {

			@Override
			public Double apply(Point arg0) {
				// TODO Auto-generated method stub
				return arg0.getY()*arg0.getValue();
			}
			
		}, points);
		
		double[][] unKnownData = {
				{len,sumX,sumY},
				{sumX,sumX2,sumXY},
				{sumY,sumXY,sumY2}
				};
		double [][]constantData = {
				{sumZ},
				{sumXZ},
				{sumYZ}
		};
		//利用矩阵建立法方程
		//未知数矩阵(参数矩阵)
		Matrix unKnown = new Matrix(unKnownData) ;
		//系数矩阵
		Matrix coefficient;
		//常数矩阵
		Matrix constant = new Matrix(constantData);
		//求系数矩阵
		coefficient = constant.LeftMultiMatrix(unKnown.getInverseMatrix());
		Matrix.printMatrix(coefficient);
		final double b0=coefficient.getMatrix()[0][0];
		final double b1=coefficient.getMatrix()[1][0];
		final double b2=coefficient.getMatrix()[2][0];
		
		return new Function<Point,Double>() {

			@Override
			public Double apply(Point p) {
				// TODO Auto-generated method stub
//				MatPlot3DMgr matPlot3DMgr = new MatPlot3DMgr();
//				matPlot3DMgr.setDataInputType(MatPlot3DMgr.DATA_TYPE_FUNCTION3D);
//
//				tanling.matplot_4j.d3d.facade.Function function = new tanling.matplot_4j.d3d.facade.Function() {
//					@Override
//					public double f(double x, double y) {
//						return b1*x+b2*y+b0;//函数算式
//					}
//				};
//				
//				
//
//				matPlot3DMgr.addData(function, new Range(-6, 6), new Range(-6, 6), 70, 70);           //x,y方向的范围和曲面分段
//				matPlot3DMgr.setTitle("z = 0.8 * sin(y) * cos(x)");
//				matPlot3DMgr.show();
//				
				
				return b0+b1*p.getX()+b2*p.getY();
			}
		};
	}
	
	
	//二次趋势面插值(2维)
	public static BiFunction<Double, Double,Double> trendSurface2D(){
		
		
		
		return new BiFunction<Double,Double,Double>() {

			@Override
			public Double apply(Double x, Double y) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
	//三次趋势面插值(2维)
	public static BiFunction<Double, Double,Double> trendSurface3D(){
		return new BiFunction<Double,Double,Double>() {

			@Override
			public Double apply(Double x, Double y) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
	
	
	/**
	 *反距离权重插值法 
	 * @param k 幂值 幂越大 则越靠近已知点 数值越大，1为线性插值
	 * @param points 已知点
	 * @return
	 */
	public static Function<Point, Double> idw(double k,Point...points){
		return new Function<Point,Double>() {
			
			@Override
			public Double apply(Point p) {
				// TODO Auto-generated method stub
				//分子部分
				double sumDisMulZ = sum(new Function<Point, Double>() {

					@Override
					public Double apply(Point arg0) {
						// TODO Auto-generated method stub
						Line line = new Line(p,arg0);
						return arg0.getValue()*Math.pow(line.getLength(), -k);
					}
					
				}, points);
				//分母部分
				double sumDis = sum(new Function<Point,Double>() {
					@Override
					public Double apply(Point arg0) {
						// TODO Auto-generated method stub
						Line line = new Line(p,arg0);
						return Math.pow(line.getLength(), -k);
					}
				}, points);
				return sumDisMulZ/sumDis;
			}
		};
	}
	
	/**
	 * 薄板样条函数插值法，当数据贫乏时会出现坡度增大的现象，出现"过伸"的现象
	 * @param points
	 * @return
	 */
	public static Function<Point,Double> plateSpline(Point...points){
		if(points.length<3) {
			throw new RuntimeException("薄板样条函数要求三个控制点以上");
		}
		//方程数= points.length+3；
		return new Function<Point,Double>(){

			private double dis(Point p,Point t) {
				return new Line(p, t).getLength();
			}
			//实时演算系数
			//系数有Ai...An，a,b,c 总共(控制点个数+3)个
			
			@Override
			public Double apply(Point p) {
				// TODO Auto-generated method stub
				
				double x = p.getX();
				double y = p.getY();
				
				int len = points.length;
				//构建系数矩阵
				double[][] coex = new double[len+3][len+3];
				for(int j=0;j<len;j++) {
					double dis = dis(p, points[j]);
					for(int i=0;i<len;i++) {
						coex[i][j] = Math.pow(dis, 2)*Math.log(dis);
					}
				}
				
				for(int j=len;j<len+3;j++) {
					for(int i=0;i<len;i++) {
						if(j==len) {
							coex[i][j] = 1;
						}else if (j==len+1) {
							coex[i][j] = points[i].getX();
						}else {
							coex[i][j] = points[i].getY();
						}
					}
				}
				for(int i=0;i<len;i++) {
					coex[len][i]=1;
				}
				for(int i=0;i<len;i++) {
					coex[len+1][i]=points[i].getX();
				}
				for(int i=0;i<len;i++) {
					coex[len+2][i]=points[i].getY();
				}
				Matrix coexMatrix = new Matrix(coex);
				
				//构建常数矩阵
				double[][] count = new double[len+3][1];
				for(int i=0;i<len;i++) {
					count[i][0] = points[i].getValue();
				}
				Matrix countMatrix = new Matrix(count);
				
				Matrix result = countMatrix.LeftMultiMatrix(coexMatrix.getInverseMatrix());
				
				double[][] results = result.getMatrix();
				double a=0,b=0,c=0,d=0;
				
				
				
				a=results[len][0];
				b=results[len+1][0];
				c=results[len+2][0];
				
				//算基函数的系数，用之前的系数矩阵的一部分乘以结果矩阵A1.A2.A3...AN就可以得出
				double [][] temD1 = new double[1][len];
				for(int i=0;i<len;i++) {
					temD1[0][i] = coex[0][i];
				}
				double [][] temD2 = new double[len][1];
				for(int i=0;i<len;i++) {
					temD2[i][0] = results[i][0];
				}
				
				Matrix temD1Matrix = new Matrix(temD1);
				Matrix temD2Matrix = new Matrix(temD2);
				//Matrix.printMatrix(temD1Matrix);
				d = temD1Matrix.RightMultiMatrix(temD2Matrix).getMatrix()[0][0];
				
				
				return d+a+b*x+c*y;
			}
			
		};
		
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
		Point a,b,c,d,e,f;
		a = new Point(69, 76);
		b = new Point(59, 64);
		c = new Point(75, 52);
		d = new Point(86, 73);
		e = new Point(88, 53);
		a.setValue(20.820);
		b.setValue(10.910);
		c.setValue(10.380);
		d.setValue(14.600);
		e.setValue(10.560);
		//待插值的点
		f = new Point(69, 67);
		Point[] points = {a,b,c,d,e};
		
		
		Function<Point, Double> trendSurface1D=trendSurface1D(points);
		double predicate =  trendSurface1D.apply(f);
		System.out.println("一次趋势面插值结果:");
		System.out.println(predicate);
		
		Function<Point, Double> idw = idw(2, points);
		double idwValue = idw.apply(f);
		System.out.println("反距离权重插值结果:");
		System.out.println(idwValue);
		
		Function<Point, Double> plateSpline=plateSpline(points);
		double plateSplineValue =  plateSpline.apply(f);
		System.out.println("薄板样条插值结果:");
		System.out.println(plateSplineValue);
		
		
		boolean flag=true;
		Point3D pa = new Point3D(a.getX(), a.getY(), a.getValue());
		Point3D pb = new Point3D(b.getX(), b.getY(), b.getValue());
		Point3D pc = new Point3D(c.getX(), c.getY(), c.getValue());
		Point3D pd = new Point3D(d.getX(), d.getY(), d.getValue());
		Point3D pe = new Point3D(e.getX(), e.getY(), e.getValue());
		List<Point3D> point3ds = new ArrayList<Point3D>();
		point3ds.add(pa);
		point3ds.add(pb);
		point3ds.add(pc);
		point3ds.add(pd);
		point3ds.add(pe);
		//随机点
		//List<Point> randomPointList = new ArrayList<>();
		
		
		
		if(flag) {
			MatPlot3DMgr matPlot3DMgr = new MatPlot3DMgr();
			matPlot3DMgr.setDataInputType(MatPlot3DMgr.DATA_TYPE_DOTS);

			Point3D pf = new Point3D(f.getX(), f.getY(), predicate);
			Point3D pg = new Point3D(f.getX(), f.getY(), idwValue);
			Point3D ph = new Point3D(f.getX(), f.getY(), plateSplineValue);
			
			
			List<Point3D> trendPoint3ds = new ArrayList<Point3D>();
			trendPoint3ds.add(pf);
			List<Point3D> idwPoint3ds = new ArrayList<Point3D>();
			idwPoint3ds.add(pg);
			List<Point3D> plateSplinePoint3ds = new ArrayList<Point3D>();
			plateSplinePoint3ds.add(ph);
			
			//随机生成数据，进行插值
			for(int i=0;i<10;i++) {
				double x = next(20d, 100d);
				double y = next(20d,100d);
				Point p = new Point(x, y);
				double trendValue = trendSurface1D.apply(p);
				Point3D point3d = new Point3D(x, y, trendValue);
				trendPoint3ds.add(point3d);
				double idwValues = idw.apply(p);
				Point3D point3d2 = new Point3D(x, y, idwValues);
				idwPoint3ds.add(point3d2);
				double plateValue = plateSpline.apply(p);
				Point3D point3d3 = new Point3D(x, y, plateValue);
				plateSplinePoint3ds.add(point3d3);
			}
			
			
			matPlot3DMgr.addData("观测数据", point3ds);
			//matPlot3DMgr.addData("趋势面拟合点", trendPoint3ds);
			matPlot3DMgr.addData("IDW拟合点", idwPoint3ds);
			//matPlot3DMgr.setTitle("内插拟合效果");
			//matPlot3DMgr.addData("薄板样条函数拟合点", plateSplinePoint3ds);
			matPlot3DMgr.show();
		}
		
		
//		int pNum = 5;
//		Point[] pointss = new Point[pNum];
//		List<Point3D> point3dss = new ArrayList<Point3D>();
//		for(int i=0;i<pNum;i++) {
//			double x = next(0d, 100d);
//			double y = next(0d,100d);
//			double z = next(0, 100d);
//			Point p = new Point(x, y);
//			Point3D point3d = new Point3D(x,y,z);
//			point3dss.add(point3d);
//			p.setValue(z);
//			pointss[i]=p;
//		}
//		
//		MatPlot3DMgr matPlot3DMgrPoint = new MatPlot3DMgr();
//		matPlot3DMgrPoint.setDataInputType(MatPlot3DMgr.DATA_TYPE_DOTS);
//		matPlot3DMgrPoint.addData("薄板样条函数观测点", point3dss);
//		matPlot3DMgrPoint.show();
//		
		MatPlot3DMgr matPlot3DMgr = new MatPlot3DMgr();
		matPlot3DMgr.setDataInputType(MatPlot3DMgr.DATA_TYPE_FUNCTION3D);

			tanling.matplot_4j.d3d.facade.Function plateSplineFunction = new tanling.matplot_4j.d3d.facade.Function() {
				
							
				//Function<Point, Double> plateSpline=plateSpline(points);
				@Override
				public double f(double x, double y) {
					Point point = new Point(x, y);
					
					double plateSplineValue =  idw.apply(point);
					return plateSplineValue;//函数算式
				}
			};
			//x,y方向的范围和曲面分段
			matPlot3DMgr.addData(plateSplineFunction, new Range(0, 100), new Range(0, 100), 50, 50);
			matPlot3DMgr.setTitle("样条函数曲面");
			matPlot3DMgr.show();
			
	}
}
