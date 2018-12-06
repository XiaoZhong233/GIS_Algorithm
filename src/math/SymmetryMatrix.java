package math;

/**
 * ∂‘≥∆±‰ªªæÿ’Û
 * @author Administrator
 *
 */
public class SymmetryMatrix extends SurfaceTransformationMatrix{
	private Matrix matrix;
	
	public SymmetryMatrix(double a,double b,double d,double e) {
		// TODO Auto-generated constructor stub
		super(new double[][] {{a,d,0},{b,e,0},{0,0,1}});
		this.matrix = super.getMatrix();
	}

	public Matrix getSymmetryMatrix() {
		return matrix;
	}
}
