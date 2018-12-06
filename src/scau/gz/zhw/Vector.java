package scau.gz.zhw;

import java.util.ArrayList;

import java.util.List;

//ʸ�����ݽṹ
public class Vector {
	//idΪ��ţ��Զ�����
	private final int id = num++;
	private static int num = 0;
	//����ʸ��ʵ�� �㣬�ߣ���
	private List<? extends Vector> data;
	
	public Vector() {
		// TODO Auto-generated constructor stub
		data = new ArrayList<>();
	}
	
	public Vector(List<? extends Vector> data) {
		this.data = data;
	}
	
	
	public int getId() {
		return id;
	}
	
	protected String getType() {
		return "ʸ�����ݽṹ";
	}
	
	public List<? extends Vector> getData() {
		return data;
	}
	
	
	public  void showGUI() {
		if(data.isEmpty())
			return;
		List<Point> points = new ArrayList<>();
		List<Polyline> polylines = new ArrayList<>();
		List<Polygon> polygons = new ArrayList<>();
		for(Vector vector :data) {
			if(vector instanceof Point) {
				points.add((Point)vector);
			}else if (vector instanceof Polyline) {
				polylines.add((Polyline)vector);
			}else if (vector instanceof Polygon) {
				polygons.add((Polygon)vector);
			}
		}
		PaintVector.createAndShowGUIPlus(points, polylines, polygons);
	}
}
