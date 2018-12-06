package math;

public class TransformMatrix extends SurfaceTransformationMatrix{
	
	private Matrix matrix;
	
	public TransformMatrix(double Tx,double Ty) {
		super(new double[][]{{1,0,0},{0,1,0},{Tx,Ty,1}});
		this.matrix = super.getMatrix();
	}
	
	public Matrix getTransformMatrix() {
		return matrix;
	}
	
	
}
