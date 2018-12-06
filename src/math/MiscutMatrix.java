package math;

public class MiscutMatrix extends SurfaceTransformationMatrix{
	private Matrix matrix;
	
	public MiscutMatrix(double d,double b) {
		// TODO Auto-generated constructor stub
		super(new double[][] {{1,d,0},{b,1,0},{0,0,1}});
		this.matrix = super.getMatrix();
	}
	
	public Matrix getMiscutMatrix(){
		return matrix;
	}
}
