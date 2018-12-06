package scau.gz.zhw;

import java.util.ArrayList;
import java.util.List;

public class Point extends Vector{
	private double x;
	private double y;
	private boolean enable = true;
	double value = 0;
	public Point(double x,double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	//最好使用atan2函数，因为使用atan1函数会导致分子为0时无结果
	public double getAngle() {
		return Math.atan2(y, x);
	}
	
	public boolean isEqual(Point o) {
		if(Double.doubleToLongBits(this.x)==Double.doubleToLongBits(o.getX())
				&&Double.doubleToLongBits(this.y)==Double.doubleToLongBits(o.getY())) {
			return true;
		}
		return false;

	}
	
	/**
	 * 
	 * @return 返回点所在的象限
	 * 0-原点
	 * 1-第一象限
	 * 2-第二象限
	 * 3-第三象限
	 * 4-第四象限
	 * 10-x轴
	 * -10-y轴
	 */
	public int quadrand() {
		if(x>0) {
			if(y>0) {
				return 1;
			}else if(y<0){
				return 4;
			}else {
				return 10;
			}
		}else if(x>0){
			if(y>0) {
				return 2;
			}else if(y<0){
				return 3;
			}else {
				return 10;
			}
		}else {
			if(y==0) {
				return 0;
			}else {
				return -10;
			}
		}
		
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return String.format("(%2f,%2f)", x,y);
	}
	
	@Override
	protected String getType() {
		// TODO Auto-generated method stub
		return "点";
	}
	
	@Override
	public void showGUI() {
		// TODO Auto-generated method stub
		List<Point> points = new ArrayList<>();
		points.add(this);
		PaintVector.createAndShowGUI(points, null, null);
	}
	
}
