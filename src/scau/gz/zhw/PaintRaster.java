package scau.gz.zhw;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import scau.gz.zhw.Raster.DisType;

public class PaintRaster extends JPanel{
	
	public interface RenderFliter{
		boolean renderPredicate(Raster raster,int row,int column);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//值越大，栅格越大
	private int interval = 10;
	private int ROW = 0;
	private int COLUMN = 0;
	private List<Raster> rasters = new ArrayList<>();
	private int xscale =1;
	private int yscale =1;
	private RenderFliter renderFliter;
	private Graphics2D g2d;
	
	public PaintRaster(Raster...raster) {
		// TODO Auto-generated constructor stub
		rasters.addAll(Arrays.asList(raster));
	}
	

	public void setRenderFliter(RenderFliter renderFliter) {
		this.renderFliter = renderFliter;
	}
	
	public Graphics2D getG2d() {
		return g2d;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		//参数设置
		Graphics2D g2d = (Graphics2D)g;
		this.g2d = g2d;
		g2d.scale(xscale, yscale);
        g2d.setStroke(new BasicStroke(1f));
        int red=(int)(Math.random()*255);
        int gree=(int)(Math.random()*255);
        int blue=(int)(Math.random()*255);
        g2d.setColor(new Color(red,gree,blue,100));
        int width = getWidth();
        int height = getHeight();
        int count=0;
        //画出网格
        System.out.println(String.format("width=%d height=%d", width,height));
        for(int i=0;i<height;i+=interval) {
        	for(int j=0;j<width;j+=interval) {
        		g2d.drawRect(j, i, interval, interval);
        		count++;
        	}
        }
        ROW=height/interval;
        COLUMN = width/interval;
        System.out.println(String.format("行=%d 列=%d", ROW,COLUMN));
        System.out.println(count);
        
        //提供自定义渲染模式和默认渲染(二值渲染)
        if(renderFliter==null) {
	        //填充栅格
	        if(!rasters.isEmpty()) {
	        	for(Raster raster:rasters) {
		        	Pixel[][] data = raster.getData();
		        	for(int i=0;i<raster.getROW();i++) {
		        		for(int j=0;j<raster.getCOLUMN();j++) {
		        			if(Double.doubleToLongBits(data[i][j].getValue())==0) {
		        				
		        			}else {
								setValue(i+1, j+1, g2d);
							}
		        		}
		        	}
	        	}
	        }
        }else {
	        	for(Raster raster:rasters) {
		        	for(int i=0;i<raster.getROW();i++) {
		        		for(int j=0;j<raster.getCOLUMN();j++) {
		        			if(renderFliter.renderPredicate(raster,i,j)) {
		        				setValue(i+1, j+1, g2d);
		        			}
		        		}
	        	}
			}
        }
        
        
        
	}
    
	
	/**
	 * 涂黑某个像元
	 */
	public void setValue(int row,int column,Graphics2D g2d) {
		if(row<=0||column<=0) {
			return;
		}
		int x = (row-1)*interval;
		int y = (column-1)*interval;
		g2d.fillRect(y, x, interval+1, interval+1);
		
	}
	
	public void setValue(int row,int column) {
		if(row<=0||column<=0) {
			return;
		}
		int x = (row-1)*interval;
		int y = (column-1)*interval;
		this.g2d.fillRect(y, x, interval+1, interval+1);
		
	}
	
	public void setString(int row,int column,Graphics2D g2d,String value) {
		if(row<=0||column<=0) {
			return;
		}
		int x = (row-1)*interval;
		int y = (column-1)*interval;
		Font font = new Font("宋体", Font.BOLD, 12); // 创建字体对象
        g2d.setFont(font); 
		g2d.drawString(value, y, x);
	}
	
	 public static void createAndShowGUI(RenderFliter renderFliter,Raster...rasters) {
	        JFrame frame = new JFrame();
	        JLabel lb = new JLabel("此处显示鼠标右键点击后的坐标");
	        JPanel jp = new JPanel();
	        jp.add(lb);
	        
	        // Add your component.
	        PaintRaster paintRaster = new PaintRaster(rasters);
	        paintRaster.setRenderFliter(renderFliter);
	        paintRaster.addMouseMotionListener(new MouseMotionListener() {
				
				@Override
				public void mouseMoved(MouseEvent e) {
					// TODO Auto-generated method stub
					int x = e.getX();
					int y = e.getY();
					lb.setText(String.format("%d,%d",x,y)); 
				}
				
				@Override
				public void mouseDragged(MouseEvent e) {
					// TODO Auto-generated method stub
					lb.setText(e.getX()+","+e.getY()); 
				}
			});
	        
	        //frame.setContentPane(paintVector);
	        paintRaster.setPreferredSize(new Dimension(800, 700));
	        frame.add(paintRaster,BorderLayout.NORTH);
	        frame.add(jp,BorderLayout.SOUTH);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        //frame.setSize(800, 800);
	        frame.pack();
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
	    }
	
	 public static void main(String[] args) {
			Point d,e,r,t,y,o,q;
			o=new Point(3,9);
			d=new Point(9,3);
			e=new Point(18,18);
			r=new Point(12,18);
			t=new Point(21,3);
			y=new Point(27,9);
			q=new Point(15, 30);
			
			Point[] points = new Point[] {o,d,e,r,t,y,q};
			Polygon polygon = new Polygon(points,true);
			Raster raster = new Raster(30,30);
			raster.RasterPolygon1(polygon,1,1);
			polygon.printPoint();
			
			Raster raster2 = raster.copy();
			raster2.setNeighbourhood();
			raster2.getMinDis(DisType.CityBlock);
			createAndShowGUI(new RenderFliter() {
				
				@Override
				public boolean renderPredicate(Raster raster, int row, int column) {
					// TODO Auto-generated method stub
					if(raster.getData()[row][column].getNearDis()>4) {
						return true;
					}
					return false;
				}
			}, raster2);
			
			createAndShowGUI(null,raster);
	}
}
