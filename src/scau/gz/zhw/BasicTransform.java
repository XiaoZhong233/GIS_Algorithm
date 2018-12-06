package scau.gz.zhw;





import math.Matrix;
import math.MiscutMatrix;
import math.RotateMatrix;
import math.ScaleMtrix;
import math.SurfaceTransformationMatrix;
import math.SymmetryMatrix;
import math.TransformMatrix;

public class BasicTransform {
	
	public  enum SymmetryType{
		yAxis,
		xAxis,
		origin,
		yx,//y=x
		anti_yx //y=-x
		
	}
	
	/**
	 * ƽ���㷨
	 * @param point
	 * @param x	x������ƫ����
	 * @param y	y������ƫ����
	 * @return
	 */
	public static Point transform(Point point,double x,double y) {
		Matrix matrix = new TransformMatrix(x, y).getTransformMatrix();
		double [][] data= {{point.getX(),point.getY(),1}};
		Matrix pointMatrix = new Matrix(data);
		Matrix result = pointMatrix.RightMultiMatrix(matrix);
		//System.out.println("ƽ�ƺ�ĵ� ��"+new Point(result.getMatrix()[0][0], result.getMatrix()[0][1]).toString());
		return new Point(result.getMatrix()[0][0], result.getMatrix()[0][1]);
	}
	
	public static Line transform(Line line,double x,double y) {
		Point start = line.getStart();
		Point end = line.getEnd();
		
		Point newStart = transform(start, x, y);
		Point newEnd = transform(end, x, y);
		return new Line(newStart,newEnd);
	}
	
	public static Polygon transform(Polygon polygon,double x,double y) {
		Point[] points = polygon.getPoints();
		Point[] result = new Point[points.length];
		for(int i=0;i<points.length;i++) {
			result[i]=transform(points[i], x, y);
			//System.out.println("result ��"+result[i].toString());
		}
		return new Polygon(result, polygon.isClose());
	}
	/**
	 * �����任�㷨
	 * x=yʱ��������Ŵ����С
	 * x!=yʱ��ͼ�������������᷽�����Ǿ��ȱ����任
	 * @param point
	 * @param x 
	 * @param y
	 * @return
	 */
	public static Point scale(Point point,double x,double y) {
		Matrix matrix = new ScaleMtrix(x, y).getScaleMatrix();
		double [][] data= {{point.getX(),point.getY(),1}};
		Matrix pointMatrix = new Matrix(data);
		Matrix result = pointMatrix.RightMultiMatrix(matrix);
		return new Point(result.getMatrix()[0][0], result.getMatrix()[0][1]);
	}
	
	public static Line scale(Line line,double x,double y) {
		Point start = line.getStart();
		Point end = line.getEnd();
		
		Point newStart = scale(start, x, y);
		Point newEnd = scale(end, x, y);
		return new Line(newStart,newEnd);
	}
	
	public static Polygon scale(Polygon polygon,double x,double y) {
		Point[] points = polygon.getPoints();
		Point[] result = new Point[points.length];
		for(int i=0;i<points.length;i++) {
			result[i]=scale(points[i], x, y);
			//System.out.println("result ��"+result[i].toString());
		}
		return new Polygon(result, polygon.isClose());
	}
	
	/**
	 * �ԳƱ任
	 * @param point
	 * @param symmetryType ö������
	 * @return
	 */
	public static Point symmetry(Point point,SymmetryType symmetryType) {
		Matrix matrix;
		switch (symmetryType) {
		case xAxis:
			matrix = new SymmetryMatrix(1, 0, 0, -1).getSymmetryMatrix();
			break;
		case yAxis:
			matrix = new SymmetryMatrix(-1, 0, 0, 1).getSymmetryMatrix();
			break;
		case yx:
			matrix = new SymmetryMatrix(0, 1, 1, 0).getSymmetryMatrix();
			break;
		case anti_yx:
			matrix = new SymmetryMatrix(0, -1, -1, 0).getSymmetryMatrix();
			break;
		case origin:
			matrix = new SymmetryMatrix(-1, 0, 0, -1).getSymmetryMatrix();
		default:
			matrix = new SymmetryMatrix(-1, 0, 0, -1).getSymmetryMatrix();
			break;
		}
		double [][] data= {{point.getX(),point.getY(),1}};
		Matrix pointMatrix = new Matrix(data);
		Matrix result = pointMatrix.RightMultiMatrix(matrix);
		return new Point(result.getMatrix()[0][0], result.getMatrix()[0][1]);
	}
	
	public static Line symmetry(Line line,SymmetryType symmetryType) {
		Point start = line.getStart();
		Point end = line.getEnd();
		
		Point newStart = symmetry(start,symmetryType);
		Point newEnd = symmetry(end,symmetryType);
		return new Line(newStart,newEnd);
	}
	
	public static Polygon symmetry(Polygon polygon,SymmetryType symmetryType) {
		Point[] points = polygon.getPoints();
		Point[] result = new Point[points.length];
		for(int i=0;i<points.length;i++) {
			result[i]=symmetry(points[i], symmetryType);
			//System.out.println("result ��"+result[i].toString());
		}
		return new Polygon(result, polygon.isClose());
	}
	
	/**
	 * ��ת�任
	 * @param point
	 * @param angle �Ƕ��Ƶ�λ
	 * @return
	 */
	public static Point rotate(Point point,double angle) {
		Matrix matrix = new RotateMatrix(angle).getRotateMatrix();
		double [][] data= {{point.getX(),point.getY(),1}};
		Matrix pointMatrix = new Matrix(data);
		Matrix result = pointMatrix.RightMultiMatrix(matrix);
		return new Point(result.getMatrix()[0][0], result.getMatrix()[0][1]);
	}
	
	public static Line rotate(Line line,double angle) {
		Point start = line.getStart();
		Point end = line.getEnd();
		
		Point newStart = rotate(start,angle);
		Point newEnd = rotate(end,angle);
		return new Line(newStart,newEnd);
	}
	
	public static Polygon rotate(Polygon polygon,double angle) {
		Point[] points = polygon.getPoints();
		Point[] result = new Point[points.length];
		for(int i=0;i<points.length;i++) {
			result[i]=rotate(points[i], angle);
			//System.out.println("result ��"+result[i].toString());
		}
		return new Polygon(result, polygon.isClose());
	}
	
	/**
	 * ���б任
	 * @param point 
	 * @param b=0,y����任ϵ��d�任  b>0,ͼ����+y���������б任,b<0,ͼ����-y���������б任 
	 * @param d=0,y����任ϵ��b�任  b>0,ͼ����+x���������б任,b<0,ͼ����-x���������б任
	 * 		  b!=0 && d!=0ʱ,x*=x+by y*=dx+y ͼ����x,y�������������б任
	 * @return
	 */
	public static Point miscut(Point point,double b,double d) {
		Matrix matrix = new MiscutMatrix(b,d).getMiscutMatrix();
		double [][] data= {{point.getX(),point.getY(),1}};
		Matrix pointMatrix = new Matrix(data);
		Matrix result = pointMatrix.RightMultiMatrix(matrix);
		return new Point(result.getMatrix()[0][0], result.getMatrix()[0][1]);
	}
	
	public static Line miscut(Line line,double b,double d) {
		Point start = line.getStart();
		Point end = line.getEnd();
		Point newStart = miscut(start,b,d);
		Point newEnd = miscut(end,b,d);
		return new Line(newStart,newEnd);
	}
	
	public static Polygon miscut(Polygon polygon,double b,double d) {
		Point[] points = polygon.getPoints();
		Point[] result = new Point[points.length];
		for(int i=0;i<points.length;i++) {
			result[i]=miscut(points[i], b,d);
			//System.out.println("result ��"+result[i].toString());
		}
		return new Polygon(result, polygon.isClose());
	}
	
	/**
	 * ����ƽ��
	 * @param point
	 * @param matrixs
	 * @return
	 */
	public static Point complexTransform(Point point,TransformMatrix...matrixs) {
		int len = matrixs.length;
		Matrix matrix = matrixs[0].getTransformMatrix();
		
		for(int i=1;i<len;i++) {
			matrix = matrix.RightMultiMatrix(matrixs[i].getTransformMatrix());
		}
		double [][] data= {{point.getX(),point.getY(),1}};
		Matrix pointMatrix = new Matrix(data);
		Matrix result = pointMatrix.RightMultiMatrix(matrix);
		return new Point(result.getMatrix()[0][0], result.getMatrix()[0][1]);
	}
	
	public static Line complexTransform(Line line,TransformMatrix...matrixs) {
		Point start = line.getStart();
		Point end = line.getEnd();
		Point newStart = complexTransform(start, matrixs);
		Point newEnd = complexTransform(end, matrixs);
		return new Line(newStart,newEnd);
	}
	
	public static Polygon complexTransform(Polygon polygon,TransformMatrix...matrixs) {
		Point[] points = polygon.getPoints();
		Point[] result = new Point[points.length];
		for(int i=0;i<points.length;i++) {
			result[i]=complexTransform(points[i], matrixs);
		}
		return new Polygon(result, polygon.isClose());
	}
	
	/**
	 * ���ϱ����任
	 * @param point
	 * @param matrixs
	 * @return
	 */
	public static Point complexScale(Point point,ScaleMtrix...matrixs) {
		int len = matrixs.length;
		Matrix matrix = matrixs[0].getScaleMatrix();
		for(int i=1;i<len;i++) {
			matrix = matrix.RightMultiMatrix(matrixs[i].getScaleMatrix());
		}
		double [][] data= {{point.getX(),point.getY(),1}};
		Matrix pointMatrix = new Matrix(data);
		Matrix result = pointMatrix.RightMultiMatrix(matrix);
		return new Point(result.getMatrix()[0][0], result.getMatrix()[0][1]);
	}
	
	public static Line complexScale(Line line,ScaleMtrix...matrixs) {
		Point start = line.getStart();
		Point end = line.getEnd();
		Point newStart = complexScale(start, matrixs);
		Point newEnd = complexScale(end, matrixs);
		return new Line(newStart,newEnd);
	}
	
	public static Polygon complexScale(Polygon polygon,ScaleMtrix...matrixs) {
		Point[] points = polygon.getPoints();
		Point[] result = new Point[points.length];
		for(int i=0;i<points.length;i++) {
			result[i]=complexScale(points[i], matrixs);
		}
		return new Polygon(result, polygon.isClose());
	}
	
	/**
	 * ������ת�任
	 * @param point
	 * @param matrixs
	 * @return
	 */
	public static Point complexRotate(Point point,RotateMatrix ...matrixs) {
		int len = matrixs.length;
		Matrix matrix = matrixs[0].getRotateMatrix();
		for(int i=1;i<len;i++) {
			matrix = matrix.RightMultiMatrix(matrixs[i].getRotateMatrix());
		}
		double [][] data= {{point.getX(),point.getY(),1}};
		Matrix pointMatrix = new Matrix(data);
		Matrix result = pointMatrix.RightMultiMatrix(matrix);
		return new Point(result.getMatrix()[0][0], result.getMatrix()[0][1]);
	}
	
	public static Line complexRotate(Line line,RotateMatrix...matrixs) {
		Point start = line.getStart();
		Point end = line.getEnd();
		Point newStart = complexRotate(start, matrixs);
		Point newEnd = complexRotate(end, matrixs);
		return new Line(newStart,newEnd);
	}
	
	public static Polygon complexRotate(Polygon polygon,RotateMatrix...matrixs) {
		Point[] points = polygon.getPoints();
		Point[] result = new Point[points.length];
		for(int i=0;i<points.length;i++) {
			result[i]=complexRotate(points[i], matrixs);
		}
		return new Polygon(result, polygon.isClose());
	}
	
	/**
	 * ���ϱ任
	 */
	public static Point complexTransmit(Point point,SurfaceTransformationMatrix...matrixs) {
		int len = matrixs.length;
		Matrix matrix = matrixs[0].getMatrix();
		for(int i=1;i<len;i++) {
			matrix = matrix.RightMultiMatrix(matrixs[i].getMatrix());
		}

		
		double [][] data= {{point.getX(),point.getY(),1}};
		Matrix pointMatrix = new Matrix(data);
		Matrix result = pointMatrix.RightMultiMatrix(matrix);
		return new Point(result.getMatrix()[0][0], result.getMatrix()[0][1]);
	}
	
	public static Line complexTransmit(Line line,SurfaceTransformationMatrix...matrixs) {
		Point start = line.getStart();
		Point end = line.getEnd();
		Point newStart = complexTransmit(start, matrixs);
		Point newEnd = complexTransmit(end, matrixs);
		return new Line(newStart,newEnd);
	}
	
	public static Polygon complexTransmit(Polygon polygon,SurfaceTransformationMatrix...matrixs) {
		Point[] points = polygon.getPoints();
		Point[] result = new Point[points.length];
		for(int i=0;i<points.length;i++) {
			result[i]=complexTransmit(points[i], matrixs);
		}
		return new Polygon(result, polygon.isClose());
	}
	
	/**
	 * �����ĳ��ı����任
	 * @param point ���任�ĵ�
	 * @param center ��Ե�
	 * @param x
	 * @param y
	 * @return
	 */
	public static Point scaleAround(Point point,Point center,double x,double y) {
		TransformMatrix t1 = new TransformMatrix(-center.getX(), -center.getY());
		ScaleMtrix scaleMtrix = new ScaleMtrix(x, y);
		TransformMatrix t2 = new TransformMatrix(center.getX(), center.getY());
		return complexTransmit(point, t1,scaleMtrix,t2);
	}
	
	public static Line scaleAround(Line line,Point center,double x,double y) {
		Point start = line.getStart();
		Point end = line.getEnd();
		Point newStart = scaleAround(start, center,x,y);
		Point newEnd = scaleAround(end, center,x,y);
		return new Line(newStart,newEnd);
	}
	
	public static Polygon scaleAround(Polygon polygon,Point center,double x,double y) {
		Point[] points = polygon.getPoints();
		Point[] result = new Point[points.length];
		for(int i=0;i<points.length;i++) {
			result[i]=scaleAround(points[i], center,x,y);
		}
		return new Polygon(result, polygon.isClose());
	}
	
	/**
	 * Χ��ĳ�����ת�任
	 * @param point ���任�ĵ�
	 * @param center ��Ե�
	 * @param angle
	 * @return
	 */
	public static Point rotateAround(Point point,Point center,double angle) {
		TransformMatrix t1 = new TransformMatrix(-center.getX(), -center.getY());
		RotateMatrix rotateMatrix = new RotateMatrix(angle);
		TransformMatrix t2 = new TransformMatrix(center.getX(), center.getY());
		return complexTransmit(point, t1,rotateMatrix,t2);
	}
	
	public static Line rotateAround(Line line,Point center,double angle) {
		Point start = line.getStart();
		Point end = line.getEnd();
		Point newStart = rotateAround(start, center,angle);
		Point newEnd = rotateAround(end, center,angle);
		return new Line(newStart,newEnd);
	}
	
	public static Polygon rotateAround(Polygon polygon,Point center,double angle) {
		Point[] points = polygon.getPoints();
		Point[] result = new Point[points.length];
		for(int i=0;i<points.length;i++) {
			result[i]=rotateAround(points[i], center,angle);
		}
		return new Polygon(result, polygon.isClose());
	}
}
