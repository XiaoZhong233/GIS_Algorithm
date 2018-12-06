package scau.gz.zhw;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import buffer.LineBuffer;

public class PaintVectorPlus extends JFrame  implements Runnable, ActionListener, MouseListener,ItemListener,MouseMotionListener {
	
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean debug = true;             // Used for debugging
	 public static int pointRadius = 3;
	 private static String windowTitle = "GIS算法";
	 private JRadioButton drawPointButton = new JRadioButton("画点");
	 private JRadioButton drawPolyLineButton =  new JRadioButton("画线");
	 private JRadioButton drawPolygonButton =  new JRadioButton("画面");
	 private JButton clearButton = new JButton("清屏");
	 private JButton compleateButton = new JButton("完成绘制");
	 private JCheckBox coorSwitch = new JCheckBox("显示坐标");
	 private JCheckBox showCircle = new JCheckBox("显示外接圆");
	 private JLabel coordinate = new JLabel("坐标");
	 private JPanel drawbuttonPanel;
	 private JPanel labelPanel;
	 private JMenuBar menuBar;
	 
	 private Canvas canvas;
	 private int polylineNum = 1; //已折线的绘制的数目
	 private int polygonNum = 1; //已多边形的绘制的数目
	 private int PointNum = 1; //已点的绘制的数目
	 
	 Map<Integer, List<Point>> pointMap = new HashMap<>();
	 List<Point> points = new ArrayList<>();
	 List<Polyline> polylines = new ArrayList<>();
	 List<Polygon> polygons = new ArrayList<>();
	 private drawType curDrawType = drawType.point;
	 private operationType curOperation = operationType.draw;
	 public enum drawType {
			 point,
			 polyline,
			 polygon
	 };
	 public enum operationType{
		 draw,
		 usingDp,
		 usingLightBar,
		 usingVoronoi,
		 usingDelaunay,
		 usingPointBf,
		 usingLineBf,
		 usingPolygonBf
	 }
	     
	 
	 public static void main(String[] args) {
		 PaintVectorPlus paintVectorPlus = new PaintVectorPlus();
		 //JFrame dWindow = new JFrame();
		 paintVectorPlus.setSize(1000, 800);               //窗体大小
		 paintVectorPlus.setTitle(windowTitle);           //标题
		 paintVectorPlus.setLayout(new BorderLayout());   //指定布局
		 paintVectorPlus.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 paintVectorPlus.init();
		 paintVectorPlus.setVisible(true);                
		 
	}
	 
	 public void init () {
	      try {
	    	  SwingUtilities.invokeAndWait(this);
	      }
	      catch (Exception e) {
	    	  System.err.println("Initialization failure");
	      }
	 }

	 
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//加入单选钮
			ButtonGroup group = new ButtonGroup();
			group.add(drawPointButton);
			group.add(drawPolyLineButton);
			group.add(drawPolygonButton);
			
			drawbuttonPanel = new JPanel();
			drawbuttonPanel.add(drawPointButton);
			drawbuttonPanel.add(drawPolyLineButton);
			drawbuttonPanel.add(drawPolygonButton);
			drawbuttonPanel.add(new JLabel("    "));
			drawbuttonPanel.add(compleateButton);
			drawbuttonPanel.add(clearButton);
			drawbuttonPanel.add(new JLabel("          "));
			this.add(drawbuttonPanel,BorderLayout.NORTH);
			
			//加入标签
			labelPanel = new JPanel();
			labelPanel.add(coorSwitch);
			labelPanel.add(new Label("     "));
			labelPanel.add(coordinate);
			this.add(labelPanel, BorderLayout.SOUTH);
			
			//加入画布
			canvas = new Canvas();
			this.add(canvas,BorderLayout.CENTER);
			
			//加入菜单
			menuBar = new JMenuBar();
			JMenu simplifyMenu = new JMenu("数据压缩");
			JMenu calculateBasicMenu = new JMenu("空间关系");
			JMenu voronoiMenu = new JMenu("Voronoi");
			JMenu bufferMenu = new JMenu("缓冲区");
			menuBar.add(simplifyMenu);
			menuBar.add(calculateBasicMenu);
			menuBar.add(voronoiMenu);
			menuBar.add(bufferMenu);
			JMenuItem dpItem = new JMenuItem("道格拉斯普克算法(折线)");
			JMenuItem lightBarItem = new JMenuItem("光栏压缩算法(折线)");
			simplifyMenu.add(dpItem);
			simplifyMenu.add(lightBarItem);
			JMenuItem voronoiItem = new JMenuItem("生成Voronoi多边形");
			JMenuItem delaunayItem = new JMenuItem("生成Delaunay三角网");
			voronoiMenu.add(voronoiItem);
			voronoiMenu.add(delaunayItem);
			JMenuItem pointBufferItem = new JMenuItem("点缓冲区");
			JMenuItem lineBufferItem = new JMenuItem("线缓冲区");
			JMenuItem polygonBufferItem = new JMenuItem("面缓冲区");
			bufferMenu.add(pointBufferItem);
			bufferMenu.add(lineBufferItem);
			bufferMenu.add(polygonBufferItem);
			this.setJMenuBar(menuBar);
			
			
			
			//注册事件监听
			drawPointButton.addActionListener(this);
			drawPolyLineButton.addActionListener(this);
			drawPolygonButton.addActionListener(this);
			clearButton.addActionListener(this);
			compleateButton.addActionListener(this);
			coorSwitch.addActionListener(this);
			drawPointButton.addItemListener(this);
			drawPolyLineButton.addItemListener(this);
			drawPolygonButton.addItemListener(this);
			showCircle.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if(debug) {
						System.out.println(((AbstractButton)e.getSource()).getText());
					}
					canvas.repaint();
				}
			});
			canvas.addMouseMotionListener(new MouseMotionListener() {
				
				@Override
				public void mouseMoved(MouseEvent e) {
					// TODO Auto-generated method stub
					int x = e.getX()-canvas.getWidth()/2;
					int y = canvas.getHeight()/2-e.getY();
					coordinate.setText(x/canvas.xscale+","+y/Math.abs(canvas.yscale)); 
				}
				
				@Override
				public void mouseDragged(MouseEvent e) {
					// TODO Auto-generated method stub
					coordinate.setText(e.getX()+","+e.getY()); 
				}
			});
			canvas.addMouseMotionListener(this);
			canvas.addMouseListener(this);
			
			
			dpItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					curOperation = operationType.usingDp;
					if(polylines.isEmpty()) {
						JOptionPane.showMessageDialog(null, "请先编辑折线数据");
						return;
					}else {
						String msg=JOptionPane.showInternalInputDialog(menuBar,"概化折线 道格拉斯普克算法\n阈值越小，保留的点越多，"
								+ "抽稀程度低；阈值越大，删除的点越多，抽稀程度高","输入阈值",1);
						double threshold=0;
						if(msg==null) {
							return;
						}
						try {
							threshold = Double.parseDouble(msg);
						} catch (Exception exception) {
							// TODO: handle exception
							JOptionPane.showMessageDialog(null, "数据只能为数字");
							return;
						}
						List<Polyline> result = new ArrayList<>();
						System.out.println("道格拉斯概化折线：");
						
						for(int i=0;i<polylines.size();i++) {
							Polyline polyline = polylines.get(i);
							Polyline simply = polyline.simplify_Douglas_Peucker(threshold);
							result.add(simply);
							if(debug) {
								int before = polyline.getPoints().size();
								int after = simply.getPoints().size();
								System.out.println("概化前折线的点数："+before);
								System.out.println("概化后折线的点数："+after);
								System.out.println("简化率："+String.format("%.2f", (before-after*1.0)/before*100).toString()+"%");
								System.out.println();
							}
						}
						polylines = result;
						//重绘
						canvas.repaint();
						curOperation = operationType.draw;
						//PaintVector.createAndShowGUIPlus(null, result, null);
					}
					
				}
			});
			lightBarItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					curOperation = operationType.usingLightBar;
					if(polylines.isEmpty()) {
						JOptionPane.showMessageDialog(null, "请先编辑折线数据");
						return;
					}else {
						String msg=JOptionPane.showInternalInputDialog(menuBar,"光栏法概化折线\n口径越小，保留的点越多，"
								+ "抽稀程度低；口径越大，删除的点越多，抽稀程度高","输入阈值",1);
						if(msg==null) {
							return;
						}
						double caliber=0;
						try {
							caliber = Double.parseDouble(msg);
						} catch (Exception exception) {
							// TODO: handle exception
							JOptionPane.showMessageDialog(null, "数据只能为数字");
							return;
						}
						
						List<Polyline> result = new ArrayList<>();
						System.out.println("光栏法概化折线：");
						for(int i=0;i<polylines.size();i++) {
							Polyline polyline = polylines.get(i);
							Polyline simply = polyline.simplify_LightBar(caliber);
							result.add(simply);
							if(debug) {
								int before = polyline.getPoints().size();
								int after = simply.getPoints().size();
								System.out.println("概化前折线的点数："+before);
								System.out.println("概化后折线的点数："+after);
								System.out.println("简化率："+String.format("%.2f", (before-after*1.0)/before*100).toString()+"%");
								System.out.println();
							}
						}
						polylines = result;
						//重绘
						canvas.repaint();
						curOperation = operationType.draw;
					}
				}
			});
			voronoiItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					curOperation = operationType.usingVoronoi;
					curDrawType = drawType.point;
					drawPointButton.setSelected(true);
					drawPolyLineButton.setEnabled(false);
					drawPolygonButton.setEnabled(false);
					JOptionPane.showMessageDialog(null, "开始离散点线数据\n编辑完成按\"编辑完成\"按钮即可");
					PaintVectorPlus.this.polygons.clear();
					polygonNum=1;
					
					if(points.isEmpty()) {
						return;
					}else {
						Voronoi voronoi = new Voronoi(points.toArray(new Point[points.size()]));
						polygons = voronoi.getVoronois();
						canvas.repaint();
					}
					
				}
			});
			delaunayItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					curOperation = operationType.usingDelaunay;
					curDrawType = drawType.point;
					drawPointButton.setSelected(true);
					drawPolyLineButton.setEnabled(false);
					drawPolygonButton.setEnabled(false);
					JOptionPane.showMessageDialog(null, "开始离散点线数据\n编辑完成按\"编辑完成\"按钮即可");
					PaintVectorPlus.this.polygons.clear();
					polygonNum=1;
					PaintVectorPlus.this.drawbuttonPanel.add(showCircle,BorderLayout.NORTH);
					
					if(points.isEmpty()) {
						return;
					}else {
						Voronoi voronoi = new Voronoi(points.toArray(new Point[points.size()]));
						List<Triangle> triangles= voronoi.getDelaunay2().getTriangles();
						List<Polygon> polygons = new ArrayList<>();
						for(Triangle triangle : triangles) {
							polygons.add(triangle);
						}
						PaintVectorPlus.this.polygons = polygons;
						canvas.repaint();
					}
				}
			});
			lineBufferItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					curOperation = operationType.usingLineBf;
					curDrawType= drawType.polyline;
					drawPointButton.setSelected(false);
					drawPolyLineButton.setEnabled(true);
					drawPolygonButton.setEnabled(false);
					if(polylines.isEmpty()) {
						JOptionPane.showMessageDialog(null, "请先编辑折线数据");
						return;
					}else {
						String msg=JOptionPane.showInternalInputDialog(menuBar,"线状缓冲区生成","输入缓冲半径",1);
						if(msg==null) {
							return;
						}
						double bufDis=0;
						try {
							bufDis = Double.parseDouble(msg);
						} catch (Exception exception) {
							// TODO: handle exception
							JOptionPane.showMessageDialog(null, "数据只能为数字");
							return;
						}
						List<Polygon> polygons = new ArrayList<>();
						for(Polyline polyline:polylines) {
							List<Point> buf = new LineBuffer(polyline).generateBuffer(bufDis, 0);
							Polygon polygon = new Polygon(buf, true);
							polygons.add(polygon);
						}
						PaintVectorPlus.this.polygons = polygons;
						canvas.repaint();
						curOperation = operationType.draw;
						
					}
				}
			});
			//默认画点
			drawPointButton.setSelected(true);
			curDrawType = drawType.point;
			curOperation = operationType.draw;
		}
	 
		
	public abstract static class MyMouseClickListener extends MouseAdapter{
		private static boolean flag = false;//双击事件以执行为真
		private static int clickNum = 1;
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			final MouseEvent me = e;
			MyMouseClickListener.flag = false;
			//双击事件监听
			if(MyMouseClickListener.clickNum==2) {
				this.onDoubleClick(me);
				flag = true;
				clickNum = 1;
				return;
			}
			//新建定时器，双击检测间隔为500ms 
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				//计时器执行次数
				int num = 0;
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//双击事件已经执行，那么就取消该次计时任务
					if(MyMouseClickListener.flag) {
						num = 0;
						MyMouseClickListener.clickNum = 1;
						this.cancel();
						return;
					}
					
					//计时器再次执行
					if(num==1) {
						onClick(me);
						MyMouseClickListener.flag = true;
						MyMouseClickListener.clickNum = 1;
						num=0;
						this.cancel();
						return;
					}
					clickNum++;
					num++;
				}
			}, new Date(),200);
			
		}
		
		
		
		protected abstract void onDoubleClick(MouseEvent e);
		protected abstract void onClick(MouseEvent e);
	}
		
		
	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
		//points.clear();
		//polylines.clear();
		//polygons.clear();
		//完成绘制
		canvas.repaint();
	}
	
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = (e.getX()-canvas.getWidth()/2)/canvas.xscale;
		int y = canvas.getHeight()/2-e.getY()/Math.abs(canvas.yscale);
		Point point = new Point(x, y);		
		if(curDrawType==drawType.polyline) {	
			compleateButton.setEnabled(true);
			List<Point> plList;
			if(polylines.isEmpty()) {
				plList = new ArrayList<Point>();
				plList.add(point);
				polylines.add(new Polyline(plList));
			}
			else {
				//不为空就得检查当前保存的折线数是否等于当前已画的折线数
				if(polylines.size()==polylineNum) {
					plList=polylines.get(polylines.size()-1).getPoints();
					plList.add(point);
				}else {
					plList = new ArrayList<Point>();
					plList.add(point);
					polylines.add(new Polyline(plList));
				}
			}
			canvas.repaint();
		}
		if(curDrawType==drawType.polygon) {
			List<Point> pgList;
			if(polygons.isEmpty()) {
				pgList = new ArrayList<Point>();
				pgList.add(point);
				polygons.add(new Polygon(pgList,true));
			}
			else {
				//不为空就得检查当前保存的折线数是否等于当前已画的折线数
				if(polygons.size()==polygonNum) {
					pgList=polygons.get(polygons.size()-1).getPointsList();
					//因为点的顺序改变了，需要重新生成多边形
					polygons.remove(polygons.size()-1);
					pgList.add(point);
					polygons.add(new Polygon(pgList, true));
				}else {
					pgList = new ArrayList<Point>();
					pgList.add(point);
					polygons.add(new Polygon(pgList,false));
				}
			}
			canvas.repaint();
		}
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		int x = (e.getX()-canvas.getWidth()/2)/canvas.xscale;
		int y = canvas.getHeight()/2-e.getY()/Math.abs(canvas.yscale);
		Point point = new Point(x, y);
		
		compleateButton.setEnabled(true);
		//if(debug) System.out.println(point);

		switch (curDrawType) {
		case point:
			points.add(point);
			//正在编辑Voronoi多边形状态
			if(curOperation==operationType.usingVoronoi && !points.isEmpty()) {
				curDrawType = drawType.point;
				Voronoi voronoi = new Voronoi(points.toArray(new Point[points.size()]));
				polygons = voronoi.getVoronois();
			}
			if(curOperation==operationType.usingDelaunay && points.size()>=3) {
				curDrawType = drawType.point;
				Voronoi voronoi = new Voronoi(points.toArray(new Point[points.size()]));
				List<Triangle> triangles= voronoi.getDelaunay2().getTriangles();
				List<Polygon> polygons = new ArrayList<>();
				for(Triangle triangle : triangles) {
					polygons.add(triangle);
				}
				PaintVectorPlus.this.polygons = polygons;
			}
			canvas.repaint();
			break;
		case polyline:
			//points.add(point);
			//list保存当前编辑的点
			List<Point> plList;
			if(polylines.isEmpty()) {
				plList = new ArrayList<Point>();
				plList.add(point);
				polylines.add(new Polyline(plList));
			}
			else {
				//不为空就得检查当前保存的折线数是否等于当前已画的折线数
				if(polylines.size()==polylineNum) {
					plList=polylines.get(polylines.size()-1).getPoints();
					plList.add(point);
				}else {
					plList = new ArrayList<Point>();
					plList.add(point);
					polylines.add(new Polyline(plList));
				}
			}
			canvas.repaint();
			break;
		case polygon:
			//list保存当前编辑的点
			List<Point> pgList;
			if(polygons.isEmpty()) {
				pgList = new ArrayList<Point>();
				pgList.add(point);
				polygons.add(new Polygon(pgList,true));
			}
			else {
				//不为空就得检查当前保存的折线数是否等于当前已画的折线数
				if(polygons.size()==polygonNum) {
					pgList=polygons.get(polygons.size()-1).getPointsList();
					//因为点的顺序改变了，需要重新生成多边形
					polygons.remove(polygons.size()-1);
					pgList.add(point);
					polygons.add(new Polygon(pgList, true));
				}else {
					pgList = new ArrayList<Point>();
					pgList.add(point);
					polygons.add(new Polygon(pgList,false));
				}
			}
			canvas.repaint();
			break;
		default:
			
			break;
		}
		if(debug) {
			System.out.println();
			System.out.println("当前点的数量："+points.size());
			System.out.println("当前折线的数量："+polylines.size());
			System.out.println("当前多边形的数量："+polygons.size());
			System.out.println();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(debug) {
			System.out.println(((AbstractButton)e.getSource()).getText());
		}
		if(e.getSource()==clearButton) {
			points.clear();
			polylines.clear();
			polygons.clear();
			polygonNum=1;
			PointNum=1;
			polylineNum=1;
			canvas.repaint();
			
		}
		if (e.getSource()==compleateButton) {
			if(curOperation == operationType.usingVoronoi) {
				if(debug) {
					System.out.println("Voronoi编辑完成");	
				}
				drawPolyLineButton.setEnabled(true);
				drawPolygonButton.setEnabled(true);
				curOperation = operationType.draw;
				//预备下一个多边形的绘制,所以+1
				polygonNum = polygons.size()+1;
			}
			
			if(curOperation == operationType.usingDelaunay) {
				if(debug) {
					System.out.println("Delaunay编辑完成");	
				}
				drawbuttonPanel.remove(showCircle);
				drawbuttonPanel.repaint();
				drawPolyLineButton.setEnabled(true);
				drawPolygonButton.setEnabled(true);
				curOperation = operationType.draw;
				//预备下一个多边形的绘制,所以+1
				polygonNum = polygons.size()+1;
			}
			
			switch (curDrawType) {
			case point:
				PointNum++;
				break;
			case polyline:
				polylineNum++;
				break;
			case polygon:
				polygonNum++;
				break;
			default:
				break;
			}
			compleateButton.setEnabled(false);
		}
		
		//如果点选项到别的就自动完成绘制
		if(e.getSource()!=curDrawType ) {
			switch (curDrawType) {
			case point:
				if(PointNum==points.size())
				PointNum++;
				break;
			case polyline:
				if(polylineNum==polylines.size())
				polylineNum++;
				break;
			case polygon:
				if(polygonNum==polygons.size())
				polygonNum++;
				break;
			default:
				break;
			}
		}
		
		if(e.getSource()==drawPointButton) {
			curDrawType = drawType.point;
		}
		if(e.getSource()==drawPolyLineButton) {
			curDrawType = drawType.polyline;
		}
		if(e.getSource()==drawPolygonButton) {
			curDrawType = drawType.polygon;
		}


		
		
	}
	


	public boolean isDrawPoint() {
		return drawPointButton.isSelected();
	}
	
	public boolean isDrawPolyline() {
		return drawPolyLineButton.isSelected();
	}
	
	public boolean isDrawPolygon() {
		return drawPolygonButton.isSelected();
	}
	
	public boolean isShowCircle() {
		return showCircle.isSelected();
	}




	class Canvas extends JPanel{
		private static final long serialVersionUID = 1L;
		private int xscale =1;
		private int yscale =-1;
		private Map<Object, Color> colorTable;
		private Random random = new Random();
		private Graphics g;  
		
		
		public Canvas() {
			// TODO Auto-generated constructor stub
			colorTable = new HashMap<Object,Color>();
		}
		
		/**
		 * 画点
		 * @param point
		 */
		public void draw(Point point,Color color) {
			Color tem = g.getColor();
			g.setColor(color);
			int r = pointRadius;
	        int x = (int) point.getX();
	        int y = (int) point.getY();
	        g.fillOval(x-r, y-r, r+r, r+r);
	        g.setColor(tem);
		}
		
		/**
		 * 画线段
		 * @param line
		 */
		public void draw(Line line,Color color) {
			Color tem = g.getColor();
			g.setColor(color);
			Point start = line.getStart();
			Point end = line.getEnd();
			g.drawLine((int)start.getX(), (int)start.getY(), (int)end.getX(), (int)end.getY());
			g.setColor(tem);
		}
		
		
		/**
		 * 画多边形
		 * @param polygon
		 */
		public void draw(Polygon polygon,Color fillcolor) {
			List<Line> lines = polygon.getLines();
			Color temColor = g.getColor();
	        g.setColor(fillcolor);
			for(Line line:lines) {
				draw(line,Color.BLACK);
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
			g.fillPolygon(xPoints, yPoints, points.length);
			g.setColor(temColor);
//			for(Point p:points) {
//				draw(p, color);
//			}
		}
		
		
		/**
		 * 画折线
		 * @param polyline
		 */
		public void draw(Polyline polyline,Color color) {
			Color tem = g.getColor();
			for(Point p:polyline.getPoints()) {
				draw(p, color);
			}
			for(Line line:polyline.getLines()) {
				draw(line,color);
			}
			g.setColor(tem);
		}
		
		/**
		 * 画圆
		 * @param point
		 * @param radius
		 */
		public void draw(Point point,double radius,Color color) {
			Color tem = g.getColor();
			g.setColor(color);
	        int x = (int) point.getX();
	        int y = (int) point.getY();
	        int r = (int) radius;
	        g.drawOval(x-r, y-r, r+r, r+r);
	        g.setColor(tem);
		}
		
		
	    private Color getColor (Object item) {
	        if (colorTable.containsKey(item)) return colorTable.get(item);
	        Color color = new Color(Color.HSBtoRGB(random.nextFloat(), 1.0f, 1.0f));
	        colorTable.put(item, color);
	        return color;
	    }
	    
	    private Color getPolygonColor(Object item) {
	    	if (colorTable.containsKey(item)) return colorTable.get(item);
	    	int red=(int)(Math.random()*255);
	        int gree=(int)(Math.random()*255);
	        int blue=(int)(Math.random()*255);
	        Color color = new Color(red,gree,blue,150); 
	        colorTable.put(item, color);
	        return color;
	    }
		
		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			this.g = g;
	        Graphics2D g2d = (Graphics2D)g;
	        g2d.setStroke(new BasicStroke(1.5f));
	        g2d.translate(getWidth() / 2, getHeight() / 2);
	        g2d.scale(xscale, yscale);
	        g2d.drawLine(-1000, 0, 1000, 0);
	        g2d.drawLine(0, -1000, 0, 1000);
	        drawAll();
		}
		
		private void drawAll() {
			for(Polygon polygon:polygons) {
				draw(polygon,getPolygonColor(polygon));
			}
			for(Polyline polyline:polylines) {
				draw(polyline, getColor(polyline));
			}
			for(Point point:points) {
				draw(point, Color.BLACK);
			}
			if(isShowCircle()&&curOperation==operationType.usingDelaunay && points.size()>=3) {
				for(Polygon polygon:polygons) {
					Triangle triangle = (Triangle)polygon;
					draw(triangle.getOutside(), triangle.getOutsideRadius(),Color.BLACK);
				}
			}
		}
		
	}
	
}
