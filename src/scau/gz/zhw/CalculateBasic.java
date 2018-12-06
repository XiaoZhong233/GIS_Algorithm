package scau.gz.zhw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import math.Vector2D;

public class CalculateBasic {
	/**
	 * 返回pq向量
	 * @param p
	 * @param q
	 * @return
	 */
	public static Vector2D getVector(Point p,Point q) {
		double x = q.getX()-p.getX();
		double y = q.getY()-p.getY();
		Vector2D result = new Vector2D(x, y);
		return result;
	}
	
	public static void getDirection(Vector2D p,Vector2D q) {
		if(p.crossProduct(q)>0) {
			System.out.println("顺时针");
		}else if (p.crossProduct(q)<0) {
			System.out.println("逆时针");
		}else {
			System.out.println("共线");
		}
	}
	
	
	/**
	 * 判断点是否在线段上
	 * @param p1 线段端点
	 * @param p2 线段端点
	 * @param q	需要判断的点
	 * @return
	 */
	public static boolean isPointAtSegment(Point p1,Point p2,Point q) {
		
		//判断是否在线段围成的区域内
		if(q.getX()<=Math.max(p1.getX(), p2.getX()) && q.getX()>=Math.min(p1.getX(), p2.getX())
				&& q.getY()<= Math.max(p1.getY(), p2.getY()) && q.getY()>=Math.min(p1.getY(), p2.getY()))
		{
		Vector2D qp1 = getVector(q, p1);
		Vector2D p2p1 = getVector(p2, p1);
		return qp1.crossProduct(p2p1)==0?true:false;
		}else {
			return false;
		}
		
	}
	
	public static boolean isPointAtSegment(Line line,Point q) {
		return isPointAtSegment(line.getStart(), line.getEnd(), q);
	}
	
	/**
	 * 判断两线段是否相交
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static boolean isTwoSegmentIntersect(Point a,Point b,Point c,Point d) {
		boolean flag1 = false;
		boolean flag2 = false;
		//快速排斥试验
		if(Math.min(a.getY(), b.getY())<=Math.max(c.getY(), d.getY()) && Math.min(c.getX(), d.getX())<=Math.max(a.getX(), b.getX())
				&& Math.min(c.getY(), d.getY())<= Math.max(a.getY(), b.getY()) && Math.min(a.getX(), b.getX())<= Math.max(c.getX(), d.getX())) {
			flag1 = true;
		}
		Vector2D ab = getVector(a, b);
		Vector2D ac = getVector(a, c);
		Vector2D bd = getVector(b, d);
		//跨立试验
		if(ac.crossProduct(ab) * bd.crossProduct(ab) <=0) {
			flag2 = true;
		}
		return flag1&&flag2;
	}
	
	public static boolean isTwoSegmentIntersect(Line a,Line b) {
		return isTwoSegmentIntersect(a.getStart(), a.getEnd(), b.getStart(), b.getEnd());
	}
	
	
	/**
	 * 
	 * @param points
	 * @param p
	 * @param type 0代表使用射线法,1代表使用转角法
	 * @return
	 */
	public static boolean isPointAtPolygon(Point[] points,Point p,int type) {
		int crossNum = 0;
		boolean flag = false;
		if(Double.doubleToLongBits(points[points.length-1].getX())==Double.doubleToLongBits(points[0].getX()) && 
				Double.doubleToLongBits(points[points.length-1].getY()) == Double.doubleToLongBits((points[0].getY()))){
			flag = true;
		}
		
		//如果最后一个点不等于第一个点
		//自动闭合
		ArrayList<Line> lines = new ArrayList<Line>();
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
		//遍历每条边
		if(type==0) {
			for(Line line : lines) {
				//rule#1:方向向上的边包括其开始点,不包括其终止点
				//rule#2:方向向下的边包括其终止点,不包括其开始点
				//rule#3:水平边不参与穿越测试
				//rule#4:射线和多边形的边的交点必须严格在点p的右边
				
				//如果点在线段上则直接判定为在多边形内部
				if(isPointAtSegment(line.getStart(), line.getEnd(), p)) {
					return true;
				}
				
				//rule#3
				if(!line.isHorizontal()) {
					
					//rule#4,保证p在边的右边
					if(Double.doubleToLongBits(p.getX()) < Double.doubleToLongBits(line.getXByY(p.getY()))
							&& Double.doubleToLongBits(line.getXByY(p.getY()))>=Double.doubleToLongBits(Math.min(line.getStart().getX(),line.getEnd().getX()))
							&& Double.doubleToLongBits(line.getXByY(p.getY()))<=Double.doubleToLongBits(Math.max(line.getStart().getX(), line.getEnd().getX()))) {
						//rule#1,2
						//当点的射线穿过每条边的端点时，规则1,2起作用
						if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())
								||Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getEnd().getY()) ) {
							if(line.isUp()) {
								//判断是穿过开始点还是终止点
								if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())) {
									//方向为上，穿过开始点，则有效穿越
									crossNum++;
								}else {
									//方向为上，穿过终止点，则无效穿越
								}
							}else {
								//判断是穿过开始点还是终止点
								if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())) {
									//方向为下，穿过开始点，则无效穿越
								}else {
									//方向为下，穿过终止点，则有效穿越
									crossNum++;
								}
							}
						}else {
							//直接计算
							++crossNum;	
						}
					}
				}
			}
				//奇数在内，偶数在外
				//System.out.println("crossNum : " +crossNum);
				return crossNum%2==0?false:true;
		}else {
			//转角法需要判断从p向右出发的水平射线与线段方向的关系，即p是否在边的左边
			//int count = 0;
			for(Line line:lines) {
				//如果点在线段上则直接判定为在多边形内部
				if(isPointAtSegment(line.getStart(), line.getEnd(), p)) {
					return true;
				}
				if(!line.isHorizontal()) {
					//构造从p出发的水平向右射线，rule#3
					Vector2D pVector2d = new Vector2D(p.getX(),0);
					//保证p在边的左边，rule#4
					if(Double.doubleToLongBits(p.getX()) < Double.doubleToLongBits(line.getXByY(p.getY()))
							&& Double.doubleToLongBits(line.getXByY(p.getY()))>=Double.doubleToLongBits(Math.min(line.getStart().getX(),line.getEnd().getX()))
							&& Double.doubleToLongBits(line.getXByY(p.getY()))<=Double.doubleToLongBits(Math.max(line.getStart().getX(), line.getEnd().getX()))) {
						//System.out.println("id : "+count++ +" direction: "+line.getDirection());
						
						//边向上,p在边左边->p的向右出发的水平射线在边的顺时针方向
						if(line.isUp()) {
							//rule#1,2
							//这种情况只在点的射线穿过每条边的才有用
							if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())
									||Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getEnd().getY()) ) {
								//判断是穿过开始点还是终止点
								if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())) {
									//方向为上，穿过开始点，则有效穿越
									if(pVector2d.crossProduct(line.getVector2D())>0) {
										crossNum++;
									}else {
										crossNum--;
									}
								}else {
									//方向为上，穿过终止点，则无效穿越
								}
							}else {
								//不穿过端点的情况
								if(pVector2d.crossProduct(line.getVector2D())>0) {
									crossNum++;
								}else {
									crossNum--;
								}
							}
							
						}
						//边向下,p在边左边->p的向右出发的水平射线在边的顺时针方向
						if (line.isDown()) {
							//rule#1,2
							//这种情况只在点的射线穿过每条边的才有用
							if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())
									||Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getEnd().getY()) ) {
								//判断是穿过开始点还是终止点
								if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())) {
									//方向为下，穿过开始点，则无效穿越
								}else {
									//方向为下，穿过终止点，则有效穿越
									if(pVector2d.crossProduct(line.getVector2D())>0) {
										crossNum++;
									}else {
										crossNum--;
									}	
								}
							}else {
								//不穿过端点的情况
								if(pVector2d.crossProduct(line.getVector2D())>0) {
									crossNum++;
								}else {
									crossNum--;
								}	
							}
						}
					}
				}
			}
			//System.out.println("crossNum : " +crossNum);
			return crossNum==0?false:true;
		}
	}

	public static boolean isPointAtPolygon(Polygon polygon,Point p,int type) {
		return isPointAtPolygon(polygon.getPoints(), p, type);
	}
	
	
	public static boolean isSegmentAtPolygon(Line line,Polygon polygon) {
		//判断线段的两端点是否在多边形内部
		if(!isPointAtPolygon(polygon, line.getStart(), 0) || !isPointAtPolygon(polygon, line.getEnd(), 0)) {
			System.out.println("端点不在多边形内部");
			return false;
		}
		//交点集
		List<Point> pointSet = new ArrayList<>();
		//判断多边形的各条边与线段不内交
		for(Line bian : polygon.getLines()) {
			//判断线段的两端点是否在多边形上
			if(isPointAtSegment(bian, line.getStart()) || isPointAtSegment(bian, line.getEnd())) {
				if(isPointAtSegment(bian, line.getStart())) {
					pointSet.add(line.getStart());
				}
				if(isPointAtSegment(bian, line.getEnd())) {
					pointSet.add(line.getEnd());
				}
			//判断多边形的边的某个端点是否在线段上
			}else if (isPointAtSegment(line, bian.getStart())||isPointAtSegment(line, bian.getEnd())) {
				if(isPointAtSegment(line, bian.getStart())) {
					pointSet.add(bian.getStart());
				}
				if(isPointAtSegment(line, bian.getEnd())) {
					pointSet.add(bian.getEnd());
				}
			//判断是否相交，如果程序进行此次判断，则说明上两个判断都不满足，则说明线段与边内交
			}else if (isTwoSegmentIntersect(bian, line)) {
				System.out.println("线段与多边形内交");
				return false;
			}
		}
		
		//对交点集进行按照x，y排序，为了更方便的比较各交点的中点是否在多边形内
		Comparator<Point> XYcomparator = new Comparator<Point>() {
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
		
		Collections.sort(pointSet, XYcomparator);
		//一次判断每两个相邻点的中点是否在多边形内
		for(int i=0;i<pointSet.size();i++) {
			//如果是最后一个点，循环结束
			if(i==pointSet.size()-1) {
				break;
			}
			Point a = pointSet.get(i);
			Point b = pointSet.get(i+1);
			Point center = new Point((a.getX()+b.getX())/2.0,(b.getY()+a.getY())/2.0);
			if(!isPointAtPolygon(polygon, center, 0)) {
				System.out.println("中点不在多边形内部");
				return false;
			}
		}
		return true;
	}
	
	
	
	
	public static void main(String[] args) {
		Point aPoint = new Point(0, 0);
		Point bPoint = new Point(20, -20);
		Point cPoint = new Point(50, 30);
		Point dPoint = new Point(30, 30);
		Point ePoint = new Point(60, -20);
		Point fPoint = new Point(80, 0);
		Point gPoint = new Point(40, 70);
		System.out.println("点在线段上："+isPointAtSegment(aPoint, bPoint, cPoint));
		System.out.println("两线段是否相交："+isTwoSegmentIntersect(aPoint, bPoint, cPoint, dPoint));
		
		
		Point[] points = new Point[] {
				aPoint,bPoint,cPoint,dPoint,ePoint,fPoint,gPoint
		};
		Polygon polygon = new Polygon(points, true);
		Point p = new Point(40, 60);
		List<Point> pointList = new ArrayList<>();
		List<Polygon> polygonList = new ArrayList<>();
		pointList.add(p);
		polygonList.add(polygon);
		
		System.out.println("射线法:"+isPointAtPolygon(polygon, p,0));
		System.out.println("转角法"+isPointAtPolygon(points, p,1));
		
//		Polygon TransformPolygon = BasicTransform.transform(polygon, 0, 30);
//		TransformPolygon = BasicTransform.scaleAround(TransformPolygon, TransformPolygon.getCenterPoint(), 0.3, 0.3);
//		TransformPolygon = BasicTransform.transform(TransformPolygon, -25, -30);
		//polygonList.add(TransformPolygon);
		
//		TransformPolygon.printPoint();
		
//		Raster raster = new Raster(30,30);
//		raster.RasterPolygon1(TransformPolygon,1,1);
//		raster.render(new Renderer() {
//
//			@Override
//			public void render(Raster raster) {
//				// TODO Auto-generated method stub
//				String[][] renderData = new String[raster.getROW()][raster.getCOLUMN()];		
//				for(int i=0;i<raster.getROW();i++) {
//				for(int j=0;j<raster.getCOLUMN();j++) {
//					if(Double.doubleToLongBits(raster.getData()[i][j].getValue())==0) {
//						renderData[i][j]="-";
//					}else if(raster.getData()[i][j].getValue()==1.0){
//						renderData[i][j]="●";
//					}
//				}
//			}
//				
//				// 逐行输出，每行输出COLUMN个数据后换行。
//				for (int i = 0; i < raster.getROW(); i++) {
//					for (int j = 0; j < raster.getCOLUMN(); j++) {
//						System.out.print(renderData[i][j] + " ");
//						//System.out.print(String.format("%s ", renderData[i][j]));
//					}
//					
//					// 换行符
//					System.out.println();
//				}
//				
//			}
//		});
//		System.out.println();
//		raster.setNeighbourhood();
//		raster.getMinDis(DisType.CityBlock);
//		raster.render();
		PaintVector.createAndShowGUI(pointList, null, polygonList);

		
		Point p1 = new Point(20, 32);
		Point p2 = new Point(60, 34);
		Line line1 = new Line(p1,p2);

		Point q1 = new Point(0, 0);
		Point q2 = new Point(100, 100);
		Line line2 = new Line(q1,q2);		
		
		//getDirection(line1.getVector2D(),line2.getVector2D());
		
		List<Line> lines = new ArrayList<>();
		lines.add(line1);
		lines.add(line2);
		System.out.println("线段是否在多边形内： "+isSegmentAtPolygon(line1, polygon));
		PaintVector.createAndShowGUI(null, lines, polygonList);
		
	}
	
}
