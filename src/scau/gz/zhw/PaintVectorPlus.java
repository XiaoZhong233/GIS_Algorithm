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
	 private static String windowTitle = "GIS�㷨";
	 private JRadioButton drawPointButton = new JRadioButton("����");
	 private JRadioButton drawPolyLineButton =  new JRadioButton("����");
	 private JRadioButton drawPolygonButton =  new JRadioButton("����");
	 private JButton clearButton = new JButton("����");
	 private JButton compleateButton = new JButton("��ɻ���");
	 private JCheckBox coorSwitch = new JCheckBox("��ʾ����");
	 private JCheckBox showCircle = new JCheckBox("��ʾ���Բ");
	 private JLabel coordinate = new JLabel("����");
	 private JPanel drawbuttonPanel;
	 private JPanel labelPanel;
	 private JMenuBar menuBar;
	 
	 private Canvas canvas;
	 private int polylineNum = 1; //�����ߵĻ��Ƶ���Ŀ
	 private int polygonNum = 1; //�Ѷ���εĻ��Ƶ���Ŀ
	 private int PointNum = 1; //�ѵ�Ļ��Ƶ���Ŀ
	 
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
		 paintVectorPlus.setSize(1000, 800);               //�����С
		 paintVectorPlus.setTitle(windowTitle);           //����
		 paintVectorPlus.setLayout(new BorderLayout());   //ָ������
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
			//���뵥ѡť
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
			
			//�����ǩ
			labelPanel = new JPanel();
			labelPanel.add(coorSwitch);
			labelPanel.add(new Label("     "));
			labelPanel.add(coordinate);
			this.add(labelPanel, BorderLayout.SOUTH);
			
			//���뻭��
			canvas = new Canvas();
			this.add(canvas,BorderLayout.CENTER);
			
			//����˵�
			menuBar = new JMenuBar();
			JMenu simplifyMenu = new JMenu("����ѹ��");
			JMenu calculateBasicMenu = new JMenu("�ռ��ϵ");
			JMenu voronoiMenu = new JMenu("Voronoi");
			JMenu bufferMenu = new JMenu("������");
			menuBar.add(simplifyMenu);
			menuBar.add(calculateBasicMenu);
			menuBar.add(voronoiMenu);
			menuBar.add(bufferMenu);
			JMenuItem dpItem = new JMenuItem("������˹�տ��㷨(����)");
			JMenuItem lightBarItem = new JMenuItem("����ѹ���㷨(����)");
			simplifyMenu.add(dpItem);
			simplifyMenu.add(lightBarItem);
			JMenuItem voronoiItem = new JMenuItem("����Voronoi�����");
			JMenuItem delaunayItem = new JMenuItem("����Delaunay������");
			voronoiMenu.add(voronoiItem);
			voronoiMenu.add(delaunayItem);
			JMenuItem pointBufferItem = new JMenuItem("�㻺����");
			JMenuItem lineBufferItem = new JMenuItem("�߻�����");
			JMenuItem polygonBufferItem = new JMenuItem("�滺����");
			bufferMenu.add(pointBufferItem);
			bufferMenu.add(lineBufferItem);
			bufferMenu.add(polygonBufferItem);
			this.setJMenuBar(menuBar);
			
			
			
			//ע���¼�����
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
						JOptionPane.showMessageDialog(null, "���ȱ༭��������");
						return;
					}else {
						String msg=JOptionPane.showInternalInputDialog(menuBar,"�Ż����� ������˹�տ��㷨\n��ֵԽС�������ĵ�Խ�࣬"
								+ "��ϡ�̶ȵͣ���ֵԽ��ɾ���ĵ�Խ�࣬��ϡ�̶ȸ�","������ֵ",1);
						double threshold=0;
						if(msg==null) {
							return;
						}
						try {
							threshold = Double.parseDouble(msg);
						} catch (Exception exception) {
							// TODO: handle exception
							JOptionPane.showMessageDialog(null, "����ֻ��Ϊ����");
							return;
						}
						List<Polyline> result = new ArrayList<>();
						System.out.println("������˹�Ż����ߣ�");
						
						for(int i=0;i<polylines.size();i++) {
							Polyline polyline = polylines.get(i);
							Polyline simply = polyline.simplify_Douglas_Peucker(threshold);
							result.add(simply);
							if(debug) {
								int before = polyline.getPoints().size();
								int after = simply.getPoints().size();
								System.out.println("�Ż�ǰ���ߵĵ�����"+before);
								System.out.println("�Ż������ߵĵ�����"+after);
								System.out.println("���ʣ�"+String.format("%.2f", (before-after*1.0)/before*100).toString()+"%");
								System.out.println();
							}
						}
						polylines = result;
						//�ػ�
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
						JOptionPane.showMessageDialog(null, "���ȱ༭��������");
						return;
					}else {
						String msg=JOptionPane.showInternalInputDialog(menuBar,"�������Ż�����\n�ھ�ԽС�������ĵ�Խ�࣬"
								+ "��ϡ�̶ȵͣ��ھ�Խ��ɾ���ĵ�Խ�࣬��ϡ�̶ȸ�","������ֵ",1);
						if(msg==null) {
							return;
						}
						double caliber=0;
						try {
							caliber = Double.parseDouble(msg);
						} catch (Exception exception) {
							// TODO: handle exception
							JOptionPane.showMessageDialog(null, "����ֻ��Ϊ����");
							return;
						}
						
						List<Polyline> result = new ArrayList<>();
						System.out.println("�������Ż����ߣ�");
						for(int i=0;i<polylines.size();i++) {
							Polyline polyline = polylines.get(i);
							Polyline simply = polyline.simplify_LightBar(caliber);
							result.add(simply);
							if(debug) {
								int before = polyline.getPoints().size();
								int after = simply.getPoints().size();
								System.out.println("�Ż�ǰ���ߵĵ�����"+before);
								System.out.println("�Ż������ߵĵ�����"+after);
								System.out.println("���ʣ�"+String.format("%.2f", (before-after*1.0)/before*100).toString()+"%");
								System.out.println();
							}
						}
						polylines = result;
						//�ػ�
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
					JOptionPane.showMessageDialog(null, "��ʼ��ɢ��������\n�༭��ɰ�\"�༭���\"��ť����");
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
					JOptionPane.showMessageDialog(null, "��ʼ��ɢ��������\n�༭��ɰ�\"�༭���\"��ť����");
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
						JOptionPane.showMessageDialog(null, "���ȱ༭��������");
						return;
					}else {
						String msg=JOptionPane.showInternalInputDialog(menuBar,"��״����������","���뻺��뾶",1);
						if(msg==null) {
							return;
						}
						double bufDis=0;
						try {
							bufDis = Double.parseDouble(msg);
						} catch (Exception exception) {
							// TODO: handle exception
							JOptionPane.showMessageDialog(null, "����ֻ��Ϊ����");
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
			//Ĭ�ϻ���
			drawPointButton.setSelected(true);
			curDrawType = drawType.point;
			curOperation = operationType.draw;
		}
	 
		
	public abstract static class MyMouseClickListener extends MouseAdapter{
		private static boolean flag = false;//˫���¼���ִ��Ϊ��
		private static int clickNum = 1;
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			final MouseEvent me = e;
			MyMouseClickListener.flag = false;
			//˫���¼�����
			if(MyMouseClickListener.clickNum==2) {
				this.onDoubleClick(me);
				flag = true;
				clickNum = 1;
				return;
			}
			//�½���ʱ����˫�������Ϊ500ms 
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				//��ʱ��ִ�д���
				int num = 0;
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//˫���¼��Ѿ�ִ�У���ô��ȡ���ôμ�ʱ����
					if(MyMouseClickListener.flag) {
						num = 0;
						MyMouseClickListener.clickNum = 1;
						this.cancel();
						return;
					}
					
					//��ʱ���ٴ�ִ��
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
		//��ɻ���
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
				//��Ϊ�վ͵ü�鵱ǰ������������Ƿ���ڵ�ǰ�ѻ���������
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
				//��Ϊ�վ͵ü�鵱ǰ������������Ƿ���ڵ�ǰ�ѻ���������
				if(polygons.size()==polygonNum) {
					pgList=polygons.get(polygons.size()-1).getPointsList();
					//��Ϊ���˳��ı��ˣ���Ҫ�������ɶ����
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
			//���ڱ༭Voronoi�����״̬
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
			//list���浱ǰ�༭�ĵ�
			List<Point> plList;
			if(polylines.isEmpty()) {
				plList = new ArrayList<Point>();
				plList.add(point);
				polylines.add(new Polyline(plList));
			}
			else {
				//��Ϊ�վ͵ü�鵱ǰ������������Ƿ���ڵ�ǰ�ѻ���������
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
			//list���浱ǰ�༭�ĵ�
			List<Point> pgList;
			if(polygons.isEmpty()) {
				pgList = new ArrayList<Point>();
				pgList.add(point);
				polygons.add(new Polygon(pgList,true));
			}
			else {
				//��Ϊ�վ͵ü�鵱ǰ������������Ƿ���ڵ�ǰ�ѻ���������
				if(polygons.size()==polygonNum) {
					pgList=polygons.get(polygons.size()-1).getPointsList();
					//��Ϊ���˳��ı��ˣ���Ҫ�������ɶ����
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
			System.out.println("��ǰ���������"+points.size());
			System.out.println("��ǰ���ߵ�������"+polylines.size());
			System.out.println("��ǰ����ε�������"+polygons.size());
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
					System.out.println("Voronoi�༭���");	
				}
				drawPolyLineButton.setEnabled(true);
				drawPolygonButton.setEnabled(true);
				curOperation = operationType.draw;
				//Ԥ����һ������εĻ���,����+1
				polygonNum = polygons.size()+1;
			}
			
			if(curOperation == operationType.usingDelaunay) {
				if(debug) {
					System.out.println("Delaunay�༭���");	
				}
				drawbuttonPanel.remove(showCircle);
				drawbuttonPanel.repaint();
				drawPolyLineButton.setEnabled(true);
				drawPolygonButton.setEnabled(true);
				curOperation = operationType.draw;
				//Ԥ����һ������εĻ���,����+1
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
		
		//�����ѡ���ľ��Զ���ɻ���
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
		 * ����
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
		 * ���߶�
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
		 * �������
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
		 * ������
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
		 * ��Բ
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
