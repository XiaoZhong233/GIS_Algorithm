package math;

public class SurfaceTransformationMatrix {
	
	/**	  
	 * 	 |a d g|
	 * T=|b e h|	|a d|																|g|
	 * 	 |c f i|	|b e| 负责对图形的缩放,旋转,对称,错切 。[c f] 负责对图形进行平移变换	|h| 负责投影变换
	 */	
	
	private double a,b,c,d,e,f,g,h,i;
	private double[][]  data= {{a,d,g},{b,e,h},{c,f,i}};
	private Matrix matrix;
	
	public SurfaceTransformationMatrix() {
		this.matrix = new Matrix(data);
	}

	public SurfaceTransformationMatrix(double[][] data) {
		this.matrix = new Matrix(data);
	}
	
	public Matrix getMatrix() {
		return matrix;
	}
	
}
