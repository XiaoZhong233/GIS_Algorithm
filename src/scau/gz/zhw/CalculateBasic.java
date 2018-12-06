package scau.gz.zhw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import math.Vector2D;

public class CalculateBasic {
	/**
	 * ����pq����
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
			System.out.println("˳ʱ��");
		}else if (p.crossProduct(q)<0) {
			System.out.println("��ʱ��");
		}else {
			System.out.println("����");
		}
	}
	
	
	/**
	 * �жϵ��Ƿ����߶���
	 * @param p1 �߶ζ˵�
	 * @param p2 �߶ζ˵�
	 * @param q	��Ҫ�жϵĵ�
	 * @return
	 */
	public static boolean isPointAtSegment(Point p1,Point p2,Point q) {
		
		//�ж��Ƿ����߶�Χ�ɵ�������
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
	 * �ж����߶��Ƿ��ཻ
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static boolean isTwoSegmentIntersect(Point a,Point b,Point c,Point d) {
		boolean flag1 = false;
		boolean flag2 = false;
		//�����ų�����
		if(Math.min(a.getY(), b.getY())<=Math.max(c.getY(), d.getY()) && Math.min(c.getX(), d.getX())<=Math.max(a.getX(), b.getX())
				&& Math.min(c.getY(), d.getY())<= Math.max(a.getY(), b.getY()) && Math.min(a.getX(), b.getX())<= Math.max(c.getX(), d.getX())) {
			flag1 = true;
		}
		Vector2D ab = getVector(a, b);
		Vector2D ac = getVector(a, c);
		Vector2D bd = getVector(b, d);
		//��������
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
	 * @param type 0����ʹ�����߷�,1����ʹ��ת�Ƿ�
	 * @return
	 */
	public static boolean isPointAtPolygon(Point[] points,Point p,int type) {
		int crossNum = 0;
		boolean flag = false;
		if(Double.doubleToLongBits(points[points.length-1].getX())==Double.doubleToLongBits(points[0].getX()) && 
				Double.doubleToLongBits(points[points.length-1].getY()) == Double.doubleToLongBits((points[0].getY()))){
			flag = true;
		}
		
		//������һ���㲻���ڵ�һ����
		//�Զ��պ�
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
		//����ÿ����
		if(type==0) {
			for(Line line : lines) {
				//rule#1:�������ϵı߰����俪ʼ��,����������ֹ��
				//rule#2:�������µı߰�������ֹ��,�������俪ʼ��
				//rule#3:ˮƽ�߲����봩Խ����
				//rule#4:���ߺͶ���εıߵĽ�������ϸ��ڵ�p���ұ�
				
				//��������߶�����ֱ���ж�Ϊ�ڶ�����ڲ�
				if(isPointAtSegment(line.getStart(), line.getEnd(), p)) {
					return true;
				}
				
				//rule#3
				if(!line.isHorizontal()) {
					
					//rule#4,��֤p�ڱߵ��ұ�
					if(Double.doubleToLongBits(p.getX()) < Double.doubleToLongBits(line.getXByY(p.getY()))
							&& Double.doubleToLongBits(line.getXByY(p.getY()))>=Double.doubleToLongBits(Math.min(line.getStart().getX(),line.getEnd().getX()))
							&& Double.doubleToLongBits(line.getXByY(p.getY()))<=Double.doubleToLongBits(Math.max(line.getStart().getX(), line.getEnd().getX()))) {
						//rule#1,2
						//��������ߴ���ÿ���ߵĶ˵�ʱ������1,2������
						if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())
								||Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getEnd().getY()) ) {
							if(line.isUp()) {
								//�ж��Ǵ�����ʼ�㻹����ֹ��
								if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())) {
									//����Ϊ�ϣ�������ʼ�㣬����Ч��Խ
									crossNum++;
								}else {
									//����Ϊ�ϣ�������ֹ�㣬����Ч��Խ
								}
							}else {
								//�ж��Ǵ�����ʼ�㻹����ֹ��
								if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())) {
									//����Ϊ�£�������ʼ�㣬����Ч��Խ
								}else {
									//����Ϊ�£�������ֹ�㣬����Ч��Խ
									crossNum++;
								}
							}
						}else {
							//ֱ�Ӽ���
							++crossNum;	
						}
					}
				}
			}
				//�������ڣ�ż������
				//System.out.println("crossNum : " +crossNum);
				return crossNum%2==0?false:true;
		}else {
			//ת�Ƿ���Ҫ�жϴ�p���ҳ�����ˮƽ�������߶η���Ĺ�ϵ����p�Ƿ��ڱߵ����
			//int count = 0;
			for(Line line:lines) {
				//��������߶�����ֱ���ж�Ϊ�ڶ�����ڲ�
				if(isPointAtSegment(line.getStart(), line.getEnd(), p)) {
					return true;
				}
				if(!line.isHorizontal()) {
					//�����p������ˮƽ�������ߣ�rule#3
					Vector2D pVector2d = new Vector2D(p.getX(),0);
					//��֤p�ڱߵ���ߣ�rule#4
					if(Double.doubleToLongBits(p.getX()) < Double.doubleToLongBits(line.getXByY(p.getY()))
							&& Double.doubleToLongBits(line.getXByY(p.getY()))>=Double.doubleToLongBits(Math.min(line.getStart().getX(),line.getEnd().getX()))
							&& Double.doubleToLongBits(line.getXByY(p.getY()))<=Double.doubleToLongBits(Math.max(line.getStart().getX(), line.getEnd().getX()))) {
						//System.out.println("id : "+count++ +" direction: "+line.getDirection());
						
						//������,p�ڱ����->p�����ҳ�����ˮƽ�����ڱߵ�˳ʱ�뷽��
						if(line.isUp()) {
							//rule#1,2
							//�������ֻ�ڵ�����ߴ���ÿ���ߵĲ�����
							if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())
									||Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getEnd().getY()) ) {
								//�ж��Ǵ�����ʼ�㻹����ֹ��
								if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())) {
									//����Ϊ�ϣ�������ʼ�㣬����Ч��Խ
									if(pVector2d.crossProduct(line.getVector2D())>0) {
										crossNum++;
									}else {
										crossNum--;
									}
								}else {
									//����Ϊ�ϣ�������ֹ�㣬����Ч��Խ
								}
							}else {
								//�������˵�����
								if(pVector2d.crossProduct(line.getVector2D())>0) {
									crossNum++;
								}else {
									crossNum--;
								}
							}
							
						}
						//������,p�ڱ����->p�����ҳ�����ˮƽ�����ڱߵ�˳ʱ�뷽��
						if (line.isDown()) {
							//rule#1,2
							//�������ֻ�ڵ�����ߴ���ÿ���ߵĲ�����
							if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())
									||Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getEnd().getY()) ) {
								//�ж��Ǵ�����ʼ�㻹����ֹ��
								if(Double.doubleToLongBits(p.getY()) == Double.doubleToLongBits(line.getStart().getY())) {
									//����Ϊ�£�������ʼ�㣬����Ч��Խ
								}else {
									//����Ϊ�£�������ֹ�㣬����Ч��Խ
									if(pVector2d.crossProduct(line.getVector2D())>0) {
										crossNum++;
									}else {
										crossNum--;
									}	
								}
							}else {
								//�������˵�����
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
		//�ж��߶ε����˵��Ƿ��ڶ�����ڲ�
		if(!isPointAtPolygon(polygon, line.getStart(), 0) || !isPointAtPolygon(polygon, line.getEnd(), 0)) {
			System.out.println("�˵㲻�ڶ�����ڲ�");
			return false;
		}
		//���㼯
		List<Point> pointSet = new ArrayList<>();
		//�ж϶���εĸ��������߶β��ڽ�
		for(Line bian : polygon.getLines()) {
			//�ж��߶ε����˵��Ƿ��ڶ������
			if(isPointAtSegment(bian, line.getStart()) || isPointAtSegment(bian, line.getEnd())) {
				if(isPointAtSegment(bian, line.getStart())) {
					pointSet.add(line.getStart());
				}
				if(isPointAtSegment(bian, line.getEnd())) {
					pointSet.add(line.getEnd());
				}
			//�ж϶���εıߵ�ĳ���˵��Ƿ����߶���
			}else if (isPointAtSegment(line, bian.getStart())||isPointAtSegment(line, bian.getEnd())) {
				if(isPointAtSegment(line, bian.getStart())) {
					pointSet.add(bian.getStart());
				}
				if(isPointAtSegment(line, bian.getEnd())) {
					pointSet.add(bian.getEnd());
				}
			//�ж��Ƿ��ཻ�����������д˴��жϣ���˵���������ж϶������㣬��˵���߶�����ڽ�
			}else if (isTwoSegmentIntersect(bian, line)) {
				System.out.println("�߶��������ڽ�");
				return false;
			}
		}
		
		//�Խ��㼯���а���x��y����Ϊ�˸�����ıȽϸ�������е��Ƿ��ڶ������
		Comparator<Point> XYcomparator = new Comparator<Point>() {
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
		
		Collections.sort(pointSet, XYcomparator);
		//һ���ж�ÿ�������ڵ���е��Ƿ��ڶ������
		for(int i=0;i<pointSet.size();i++) {
			//��������һ���㣬ѭ������
			if(i==pointSet.size()-1) {
				break;
			}
			Point a = pointSet.get(i);
			Point b = pointSet.get(i+1);
			Point center = new Point((a.getX()+b.getX())/2.0,(b.getY()+a.getY())/2.0);
			if(!isPointAtPolygon(polygon, center, 0)) {
				System.out.println("�е㲻�ڶ�����ڲ�");
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
		System.out.println("�����߶��ϣ�"+isPointAtSegment(aPoint, bPoint, cPoint));
		System.out.println("���߶��Ƿ��ཻ��"+isTwoSegmentIntersect(aPoint, bPoint, cPoint, dPoint));
		
		
		Point[] points = new Point[] {
				aPoint,bPoint,cPoint,dPoint,ePoint,fPoint,gPoint
		};
		Polygon polygon = new Polygon(points, true);
		Point p = new Point(40, 60);
		List<Point> pointList = new ArrayList<>();
		List<Polygon> polygonList = new ArrayList<>();
		pointList.add(p);
		polygonList.add(polygon);
		
		System.out.println("���߷�:"+isPointAtPolygon(polygon, p,0));
		System.out.println("ת�Ƿ�"+isPointAtPolygon(points, p,1));
		
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
//						renderData[i][j]="��";
//					}
//				}
//			}
//				
//				// ���������ÿ�����COLUMN�����ݺ��С�
//				for (int i = 0; i < raster.getROW(); i++) {
//					for (int j = 0; j < raster.getCOLUMN(); j++) {
//						System.out.print(renderData[i][j] + " ");
//						//System.out.print(String.format("%s ", renderData[i][j]));
//					}
//					
//					// ���з�
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
		System.out.println("�߶��Ƿ��ڶ�����ڣ� "+isSegmentAtPolygon(line1, polygon));
		PaintVector.createAndShowGUI(null, lines, polygonList);
		
	}
	
}
