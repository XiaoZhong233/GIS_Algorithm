package scau.gz.zhw;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import math.TransformMatrix;
import scau.gz.zhw.BasicTransform.SymmetryType;
import voronoi.NPoint;
import voronoi.Triangle;

public class PaintVector extends JPanel{
	
		/**
	 * 
	 */
	private List<Point> points = new ArrayList<Point>();
	private List<Line> lines = new ArrayList<Line>();
	private List<Polygon> polygons = new ArrayList<Polygon>();
	
	private static final long serialVersionUID = 1L;
		private int xscale =2;
		private int yscale =-2;
		
		private void drawPoint(Point point,Graphics2D g2d) {
			g2d.setColor(Color.BLACK);
			//g2d.fillOval((int)point.getX()-1, (int)point.getY()-1, 2, 2);
			//Rectangle rectangle = new Rectangle((int)point.getX()-1, (int)point.getY()-1, 2, 2);
			g2d.fillRect((int)point.getX()-1, (int)point.getY()-1, 2, 2);
		}
		
		private void drawLine(Line line,Graphics2D g2d) {
			Point start = line.getStart();
			Point end = line.getEnd();
			g2d.drawLine((int)start.getX(), (int)start.getY(), (int)end.getX(), (int)end.getY());
		}
	
		private void drawPolygon(Polygon polygon,Graphics2D g2d) {
			List<Line> lines = polygon.getLines();
			int red=(int)(Math.random()*255);
	        int gree=(int)(Math.random()*255);
	        int blue=(int)(Math.random()*255);
	        g2d.setColor(new Color(red,gree,blue,150));
			for(Line line:lines) {
				drawLine(line, g2d);
			}
			Point[] points = polygon.getPoints();
			int[] xPoints = new int[points.length];
			int[] yPoints = new int[points.length];
			for(int i=0;i<points.length;i++) {
				int x = (int)points[i].getX();
				int y = (int)points[i].getY();
				xPoints[i]=x;
				yPoints[i]=y;
			}
			g2d.fillPolygon(xPoints, yPoints, points.length);
		}
		

		
		
		public void setPoints(List<Point> points) {
			if(points!=null)
			this.points = points;
		}
		
		public void setLines(List<Line> lines) {
			if(lines!=null)
			this.lines = lines;
		}
		
		public void setPolygons(List<Polygon> polygons) {
			if(polygons!=null)
			this.polygons = polygons;
		}
		
		@Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        
	        Graphics2D g2d = (Graphics2D)g;
	        g2d.setStroke(new BasicStroke(.2f));
	        g2d.translate(getWidth() / 2, getHeight() / 2);
	        g2d.scale(xscale, yscale);
	        
	        g2d.drawLine(-600, 0, 600, 0);
	        g2d.drawLine(0, -600, 0, 600);
			
	        g2d.setStroke(new BasicStroke(.8f));
	        for(Polygon polygon:polygons) {
				drawPolygon(polygon, g2d);
			}
	        
			for(Line line:lines) {
				g2d.setColor(Color.BLACK);
				drawLine(line, g2d);
			}
			
	        for(Point point:points) {
	        	drawPoint(point, g2d);
	        }
			//drawPolygon(polygon, g2d);
			//drawLine(line, g2d);
	    }
	 
	    public  static  void createAndShowGUI(List<Point> points,List<Line> lines,List<Polygon> polygons) {
	        JFrame frame = new JFrame();
	        JLabel lb = new JLabel("此处显示鼠标右键点击后的坐标");
	        JPanel jp = new JPanel();
	        jp.add(lb);
	        
	        // Add your component.
	        PaintVector paintVector = new PaintVector();
	        paintVector.setPoints(points);
	        paintVector.setLines(lines);
	        paintVector.setPolygons(polygons);
	        
	        paintVector.addMouseMotionListener(new MouseMotionListener() {
				
				@Override
				public void mouseMoved(MouseEvent e) {
					// TODO Auto-generated method stub
					int x = e.getX()-paintVector.getWidth()/2;
					int y = paintVector.getHeight()/2-e.getY();
					lb.setText(x/paintVector.xscale+","+y/Math.abs(paintVector.yscale)); 
				}
				
				@Override
				public void mouseDragged(MouseEvent e) {
					// TODO Auto-generated method stub
					lb.setText(e.getX()+","+e.getY()); 
				}
			});
	        
	        //frame.setContentPane(paintVector);
	        frame.add(paintVector,BorderLayout.CENTER);
	        frame.add(jp,BorderLayout.SOUTH);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setSize(800, 800);
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
	    }
	 
	    public static void createAndShowGUIPlus(List<Point> points,List<Polyline> polylines,List<Polygon>polygons) {
	    	List<Line> lines = new ArrayList<>();
	    	if(polylines!=null)
	    	for(Polyline polyline :polylines) {
	    		lines.addAll(polyline.getLines());
	    	}
	    	createAndShowGUI(points, lines, polygons);
	    }
	    
	  //画函数图像
	    public static void createAndShowGUI(Function<Double, Double> function) {
	    	   
	        List<Point> list = new ArrayList<>();   
	        for(int x=-100;x<=100;x++)  
	        {  
	            list.add(new Point(x, function.apply((double)x)));  
	        } 
	        
	        Polyline polyline = new Polyline(list);
	        polyline.showGUI();
	    }
	    
	    public static void createAndShowGUIDrawDelauary(Set<Triangle> triangles) {
	    	List<Polygon> tList = new ArrayList<>();
			List<Point> points = new ArrayList<>();
			for(Triangle triangle:triangles) {
				NPoint p0 = triangle.get(0);
				NPoint p1 = triangle.get(1);
				NPoint p2 = triangle.get(2);
				Point pp0 = new Point(p0.coord(0), p0.coord(1));
				Point pp1 = new Point(p1.coord(0), p1.coord(1));
				Point pp2 = new Point(p2.coord(0), p2.coord(1));
				points.add(pp0);
				points.add(pp1);
				points.add(pp2);
				scau.gz.zhw.Triangle triangle2 = new scau.gz.zhw.Triangle(pp0,pp1,pp2);
				tList.add(triangle2);
			}
			createAndShowGUI(points, null, tList);
	    }
	    
	    public static void createAndShowGUIDrawDelauary(Delaunay delaunay) {
	    	List<scau.gz.zhw.Triangle> triangles = new ArrayList<>();
	    	triangles = delaunay.getTriangles();
	    	List<Point> points = new ArrayList<>();
	    	List<Polygon> polygons = new ArrayList<>();
	    	for(scau.gz.zhw.Triangle triangle :triangles) {
	    		points.addAll(Arrays.asList(triangle.getPoints()));
	    		polygons.add(triangle);
	    	}
	    	createAndShowGUI(points, null, polygons);
	    	
	    }
	    
	    
	    
	    public static void main(String[] args) throws IOException {
	    	
	    	
	    	
			Point d,e,r,t,y,o;
			o=new Point(10, 22);
			d=new Point(12, 25);
			e=new Point(20, 25);
			r=new Point(24, 18);
			t=new Point(15, 18);
			y=new Point(16, 10);
			Point[] points = new Point[] {o,d,e,r,t,y};
			Polygon polygon = new Polygon(points,true);
			List<Polygon> polygons = new ArrayList<>();
			List<Point> points2 = new ArrayList<>();
			points2.addAll(Arrays.asList(points));
			polygons.add(polygon);
			
			Polygon transformPolygon = BasicTransform.transform(polygon, 50,0);
			System.out.println("平移变换");
			transformPolygon.printPoint();
			polygons.add(transformPolygon);
			
			Polygon scalePolygon = BasicTransform.scale(polygon, 3, 3);
			System.out.println("比例变换");
			scalePolygon.printPoint();
			//polygons.add(scalePolygon);
			
			Polygon symmetryPolygon = BasicTransform.symmetry(scalePolygon, SymmetryType.yAxis);
			System.out.println("对称变换(关于y轴)");
			symmetryPolygon.printPoint();
			//polygons.add(symmetryPolygon);
			
			Polygon rotatePolygon = BasicTransform.rotate(polygon,180);
			System.out.println("旋转变换 (180°)");
			rotatePolygon.printPoint();
			//polygons.add(rotatePolygon);
			
			Polygon miscutPolygon = BasicTransform.miscut(scalePolygon, .3, .3);
			System.out.println("错切变换");
			miscutPolygon.printPoint();
			//polygons.add(miscutPolygon);
			
			TransformMatrix aMatrix = new TransformMatrix(-10, -20);
			TransformMatrix bMatrix = new TransformMatrix(-23, 50);
			Polygon complexTransformPolygon = BasicTransform.complexTransform(miscutPolygon, aMatrix,bMatrix);
			System.out.println("复合平移变换");
			complexTransformPolygon.printPoint();
			//polygons.add(complexTransformPolygon);
			
			Polygon rotateAroundPolygon = BasicTransform.rotateAround(scalePolygon, scalePolygon.getCenterPoint(), 60);
			System.out.println("围绕某点进行旋转(60°)");
			System.out.println("中心点 :"+scalePolygon.getCenterPoint());
			rotateAroundPolygon.printPoint();
			//polygons.add(rotateAroundPolygon);
			
	        createAndShowGUI(points2,null,polygons);
	    }
}
