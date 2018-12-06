package math;

public class SurfaceTransformationMatrix {
	
	/**	  
	 * 	 |a d g|
	 * T=|b e h|	|a d|																|g|
	 * 	 |c f i|	|b e| �����ͼ�ε�����,��ת,�Գ�,���� ��[c f] �����ͼ�ν���ƽ�Ʊ任	|h| ����ͶӰ�任
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
