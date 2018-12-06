package scau.gz.zhw;

public class Pixel {
	private double value;
	private int row;
	private int column;
	private Point point;
	private double nearDis;
	//周围的1值数量（左上 左下 右上 右下）
	private int oneValueNum;
	
	public enum type{
		isolated, //孤立点
		internal, //内部点
		boundary  //外部点
	}
	private type type;
	
	
	
	public Pixel(double value) {
		this.value = value;
		point = new Point(0, 0);
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public type getType() {
		return type;
	}

	public void setType(type type) {
		this.type = type;
	}

	public double getNearDis() {
		return nearDis;
	}

	public void setNearDis(double nearDis) {
		this.nearDis = nearDis;
	}

	public int getOneValueNum() {
		return oneValueNum;
	}

	public void setOneValueNum(int oneValueNum) {
		this.oneValueNum = oneValueNum;
	}

	

	
	
}
