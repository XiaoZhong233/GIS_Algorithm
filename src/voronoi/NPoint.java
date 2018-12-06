package voronoi;

/**
 * Nάŷ����ÿռ�ĵ�
 * @author Administrator
 *
 */
public class NPoint {
	private double[] coordinates; 
	
	public NPoint(double...coord) {
		// TODO Auto-generated constructor stub
		/**
		 * Ϊ�˲�ʹcoordinates��ֵ�����ı�
		 * ��ʹ��ֵ����
		 */
		coordinates = new double[coord.length];
		System.arraycopy(coord, 0, coordinates, 0, coord.length);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		 if (coordinates.length == 0) return "Point()";
	        String result = "Point(" + coordinates[0];
	        for (int i = 1; i < coordinates.length; i++)
	            result = result + "," + coordinates[i];
	        result = result + ")";
	        return result;
	}
	
	@Override
	public boolean equals(Object other) {
		// TODO Auto-generated method stub
	    if (!(other instanceof NPoint)) return false;
        NPoint p = (NPoint) other;
        if (this.coordinates.length != p.coordinates.length) return false;
        for (int i = 0; i < this.coordinates.length; i++)
            if (this.coordinates[i] != p.coordinates[i]) return false;
        return true;
	}
	
    @Override
    public int hashCode () {
        int hash = 0;
        for (double c: this.coordinates) {
            long bits = Double.doubleToLongBits(c);
            hash = (31*hash) ^ (int)(bits ^ (bits >> 32));
        }
        return hash;
    }
    
    /**
     * ��ȡĳά�ȵ�ֵ
     * @param i
     * @return ά�ȵ�ֵ
     */
    public double coord (int i) {
        return this.coordinates[i];
    }
	
    /**
     * ��ȡ���ά��ֵ
     * @return
     */
    public int dimension () {
        return coordinates.length;
    }
    
    /**
     * ��֤ά���Ƿ�һ��
     * @param p
     * @return
     */
    public int dimCheck (NPoint p) {
        int len = this.coordinates.length;
        if (len != p.coordinates.length)
            throw new IllegalArgumentException("ά�Ȳ�һ��");
        return len;
    }
    
    /**
     * ��չ���ά�ȣ��㴮����
     * @param coords
     * @return
     */
    public NPoint extend (double... coords) {
        double[] result = new double[coordinates.length + coords.length];
        System.arraycopy(coordinates, 0, result, 0, coordinates.length);
        System.arraycopy(coords, 0, result, coordinates.length, coords.length);
        return new NPoint(result);
    }
    
    /**
     * �������ĵ��
     * @param p
     * @return
     */
    public double dot (NPoint p) {
        int len = dimCheck(p);
        double sum = 0;
        for (int i = 0; i < len; i++)
            sum += this.coordinates[i] * p.coordinates[i];
        return sum;
    }
    
    /**
     * �������ľ���
     * @return
     */
    public double magnitude () {
        return Math.sqrt(this.dot(this));
    }
    
    /**
     * ����
     * @param p
     * @return
     */
    public NPoint subtract (NPoint p) {
        int len = dimCheck(p);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++)
            coords[i] = this.coordinates[i] - p.coordinates[i];
        return new NPoint(coords);
    }

    /**
     * �ӷ�
     * @param p
     * @return
     */
    public NPoint add (NPoint p) {
        int len = dimCheck(p);
        double[] coords = new double[len];
        for (int i = 0; i < len; i++)
            coords[i] = this.coordinates[i] + p.coordinates[i];
        return new NPoint(coords);
    }

    /**
     * ���������ļнǣ����ȱ�ʾ��
     * @param p
     * @return
     */
    public double angle (NPoint p) {
        return Math.acos(this.dot(p) / (this.magnitude() * p.magnitude()));
    }
    
    /**
     * ����Ĵ�ֱƽ���ߣ����κ�ά���ж�����
     * @param point ��һͬγ�ȵĵ�
     * @return	��ֱƽ���ߵ�ϵ���������磨Ax+By+C=0�����أ�A��B��C��
     */
    public NPoint bisector (NPoint point) {
        dimCheck(point);
        NPoint diff = this.subtract(point);
        NPoint sum = this.add(point);
        double dot = diff.dot(sum);
        return diff.extend(-dot / 2);
    }
    
    /**
     * �Ծ�����ʽ��ӡ�ĵ�
     * һ��NPoint�ʹ���ľ����е�һ��
     * @param matrix
     * @return
     */
    public static String toString (NPoint[] matrix) {
        StringBuilder buf = new StringBuilder("{");
        for (NPoint row: matrix) buf.append(" " + row);
        buf.append(" }");
        return buf.toString();
    }
    
    /**
     * ������������ʽ
     * �����ڵ�γ��
     * ��γ�ȴ˷�����Ч
     * @param matrix
     * @return
     */
    public static double determinant (NPoint[] matrix) {
        if (matrix.length != matrix[0].dimension())
            throw new IllegalArgumentException("Matrix is not square");
        boolean[] columns = new boolean[matrix.length];
        for (int i = 0; i < matrix.length; i++) columns[i] = true;
        try {return determinant(matrix, 0, columns);}
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Matrix is wrong shape");
        }
    }
    
    private static double determinant(NPoint[] matrix, int row, boolean[] columns){
        if (row == matrix.length) return 1;
        double sum = 0;
        int sign = 1;
        for (int col = 0; col < columns.length; col++) {
            if (!columns[col]) continue;
            columns[col] = false;
            sum += sign * matrix[row].coordinates[col] *
                   determinant(matrix, row+1, columns);
            columns[col] = true;
            sign = -sign;
        }
        return sum;
    }
    
    /**
     * �������Ĺ��彻���
     * ��������Ǵ�ֱ�����о���Ԫ�ع��ɵ�nάƽ�������
     * �ʺ��ڵ�γ�ȣ���γ�ȵ�Ч
     * @param matrix 
     * @return ��ֱ�����о���Ԫ�ع��ɵ�nάƽ�������
     */
    public static NPoint cross (NPoint[] matrix) {
        int len = matrix.length + 1;
        if (len != matrix[0].dimension())
            throw new IllegalArgumentException("Dimension mismatch");
        boolean[] columns = new boolean[len];
        for (int i = 0; i < len; i++) columns[i] = true;
        double[] result = new double[len];
        int sign = 1;
        try {
            for (int i = 0; i < len; i++) {
                columns[i] = false;
                result[i] = sign * determinant(matrix, 0, columns);
                columns[i] = true;
                sign = -sign;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Matrix is wrong shape");
        }
        return new NPoint(result);
    }
    
    
    /* Pnts as simplices */

    /**
     * Determine the signed content (i.e., area or volume, etc.) of a simplex.
     * @param simplex the simplex (as an array of NPoint)
     * @return the signed content of the simplex
     */
    public static double content (NPoint[] simplex) {
    	NPoint[] matrix = new NPoint[simplex.length];
        for (int i = 0; i < matrix.length; i++)
            matrix[i] = simplex[i].extend(1);
        int fact = 1;
        for (int i = 1; i < matrix.length; i++) fact = fact*i;
        return determinant(matrix) / fact;
    }
    
    
    /**
     * Relation between this NPoint and a simplex (represented as an array of
     * NPoints). Result is an array of signs, one for each vertex of the simplex,
     * indicating the relation between the vertex, the vertex's opposite facet,
     * and this NPoint.
     *
     * <pre>
     *   -1 means NPoint is on same side of facet
     *    0 means NPoint is on the facet
     *   +1 means NPoint is on opposite side of facet
     * </pre>
     *
     * @param simplex an array of NPoints representing a simplex
     * @return an array of signs showing relation between this NPoint and simplex
     * @throws IllegalArgumentExcpetion if the simplex is degenerate
     */
    public int[] relation (NPoint[] simplex) {
        /* In 2D, we compute the cross of this matrix:
         *    1   1   1   1
         *    p0  a0  b0  c0
         *    p1  a1  b1  c1
         * where (a, b, c) is the simplex and p is this NPoint. The result is a
         * vector in which the first coordinate is the signed area (all signed
         * areas are off by the same constant factor) of the simplex and the
         * remaining coordinates are the *negated* signed areas for the
         * simplices in which p is substituted for each of the vertices.
         * Analogous results occur in higher dimensions.
         */
        int dim = simplex.length - 1;
        if (this.dimension() != dim)
            throw new IllegalArgumentException("Dimension mismatch");

        /* Create and load the matrix */
        NPoint[] matrix = new NPoint[dim+1];
        /* First row */
        double[] coords = new double[dim+2];
        for (int j = 0; j < coords.length; j++) coords[j] = 1;
        matrix[0] = new NPoint(coords);
        /* Other rows */
        for (int i = 0; i < dim; i++) {
            coords[0] = this.coordinates[i];
            for (int j = 0; j < simplex.length; j++)
                coords[j+1] = simplex[j].coordinates[i];
            matrix[i+1] = new NPoint(coords);
        }

        /* Compute and analyze the vector of areas/volumes/contents */
        NPoint vector = cross(matrix);
        double content = vector.coordinates[0];
        int[] result = new int[dim+1];
        for (int i = 0; i < result.length; i++) {
            double value = vector.coordinates[i+1];
            if (Math.abs(value) <= 1.0e-6 * Math.abs(content)) result[i] = 0;
            else if (value < 0) result[i] = -1;
            else result[i] = 1;
        }
        if (content < 0) {
            for (int i = 0; i < result.length; i++)
                result[i] = -result[i];
        }
        if (content == 0) {
            for (int i = 0; i < result.length; i++)
                result[i] = Math.abs(result[i]);
        }
        return result;
    }

    
    /**
     * Test if this NPoint is outside of simplex.
     * @param simplex the simplex (an array of NPoints)
     * @return simplex NPoint that "witnesses" outsideness (or null if not outside)
     */
    public NPoint isOutside (NPoint[] simplex) {
        int[] result = this.relation(simplex);
        for (int i = 0; i < result.length; i++) {
            if (result[i] > 0) return simplex[i];
        }
        return null;
    }
    
    
    /**
     * Test if this NPoint is on a simplex.
     * @param simplex the simplex (an array of NPoints)
     * @return the simplex NPoint that "witnesses" on-ness (or null if not on)
     */
    public NPoint isOn (NPoint[] simplex) {
        int[] result = this.relation(simplex);
        NPoint witness = null;
        for (int i = 0; i < result.length; i++) {
            if (result[i] == 0) witness = simplex[i];
            else if (result[i] > 0) return null;
        }
        return witness;
    }

    /**
     * Test if this NPoint is inside a simplex.
     * @param simplex the simplex (an arary of NPoints)
     * @return true iff this NPoint is inside simplex.
     */
    public boolean isInside (NPoint[] simplex) {
        int[] result = this.relation(simplex);
        for (int r: result) if (r >= 0) return false;
        return true;
    }

    /**
     * �鿴�����ε����Բ�Ĺ�ϵ
     * Test relation between this NPoint and circumcircle of a simplex.
     * @param simplex the simplex (as an array of NPoints)
     * @return -1, 0, or +1 for inside, on, or outside of circumcircle
     */
    public int vsCircumcircle (NPoint[] simplex) {
        NPoint[] matrix = new NPoint[simplex.length + 1];
        for (int i = 0; i < simplex.length; i++)
            matrix[i] = simplex[i].extend(1, simplex[i].dot(simplex[i]));
        matrix[simplex.length] = this.extend(1, this.dot(this));
        double d = determinant(matrix);
        int result = (d < 0)? -1 : ((d > 0)? +1 : 0);
        if (content(simplex) < 0) result = - result;
        return result;
    }
    

    /**
     * ������Բ��2άΪԲ��3άΪ�򣩵�Բ��
     * Circumcenter of a simplex.
     * @param simplex the simplex (as an array of Pnts)
     * @return the circumcenter (a Pnt) of simplex
     */
    public static NPoint circumcenter (NPoint[] simplex) {
        int dim = simplex[0].dimension();
        if (simplex.length - 1 != dim)
            throw new IllegalArgumentException("Dimension mismatch");
        NPoint[] matrix = new NPoint[dim];
        for (int i = 0; i < dim; i++)
            matrix[i] = simplex[i].bisector(simplex[i+1]);
        NPoint hCenter = cross(matrix);      // Center in homogeneous coordinates
        double last = hCenter.coordinates[dim];
        double[] result = new double[dim];
        for (int i = 0; i < dim; i++) result[i] = hCenter.coordinates[i] / last;
        return new NPoint(result);
    }
    
    public static void main(String[] args) {
		NPoint p = new NPoint(1,2,3);
		System.out.println("��p��"+p.toString());
		NPoint[] matrix1 = {new NPoint(1,2), new NPoint(3,4)};
		NPoint[] matrix2 = {new NPoint(7,0,5), new NPoint(2,4,6), new NPoint(3,8,1)};
		System.out.print("��������ʽ:");
		System.out.println(determinant(matrix1)+","+determinant(matrix2));
		
	}
}
