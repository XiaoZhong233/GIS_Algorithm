package scau.gz.zhw;

import java.util.ArrayList;
import java.util.List;

public class Delaunay extends Vector{
	private List<Triangle> triangles;
	
	public Delaunay(List<Triangle> triangles) {
		// TODO Auto-generated constructor stub
		this.triangles = triangles;
	}
	
	public List<Triangle> getTriangles() {
		return triangles;
	}
	
	@Override
	public void showGUI() {
		// TODO Auto-generated method stub
		List<Polygon> polygons = new ArrayList<>();
		polygons.addAll(triangles);
		PaintVector.createAndShowGUI(null, null, polygons);
	}
}
