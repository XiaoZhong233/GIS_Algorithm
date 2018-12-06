package math;

public class RotateMatrix extends SurfaceTransformationMatrix{

	private Matrix matrix;
	
	public RotateMatrix(double angle) {
		// TODO Auto-generated constructor stub
		super(new double[][] {{Math.cos(Math.toRadians(angle)),Math.sin(Math.toRadians(angle)),0},
			{-Math.sin(Math.toRadians(angle)),Math.cos(Math.toRadians(angle)),0},
			{0,0,1}});
		this.matrix = super.getMatrix();
	}
	
	public Matrix getRotateMatrix() {
		return matrix;
	}
}
