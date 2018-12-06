package math;

public class ScaleMtrix extends SurfaceTransformationMatrix{
	private Matrix matrix;
	public ScaleMtrix(double Sx,double Sy) {
		// TODO Auto-generated constructor stub
		super(new double[][]{{Sx,0,0},{0,Sy,0},{0,0,1}});
		this.matrix = super.getMatrix();
	}
	
	public Matrix getScaleMatrix() {
		return matrix;
	}
}
