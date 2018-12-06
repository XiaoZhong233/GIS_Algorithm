package math;


public class Matrix {
	private int row,column;
	private double matrix[][];
	
	public Matrix(double[][] data) {
		this.row = data.length;
		this.column = data[0].length;
		this.matrix = data;
	}
	
	public Matrix(int row,int column) {
		this.row = row;
		this.column = column;
		matrix = new double[row][column];
	}
	
	public double[][] getMatrix() {
		return matrix;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
	
	public static Matrix transpose(Matrix matrix) {
		int column = matrix.getRow();
		int row = matrix.getColumn();
		Matrix matrix2 = new Matrix(row,column);
		for(int i=0;i<matrix.getRow();i++) {
			for(int j=0;j<matrix.getColumn();j++) {
				matrix2.getMatrix()[j][i] = matrix.getMatrix()[i][j];
			}
		}
		return matrix2;
	}
	
	public Matrix transpose() {
		return transpose(this);
	}
	
	/**
	 * �ӷ�����
	 * @param matrix
	 * @return
	 */
	public Matrix add(Matrix matrix) {
		if(matrix.getRow()!=row || matrix.getColumn()!=column) {
			return null;
		}
		Matrix result = new Matrix(row, column);
		for(int i=0;i<row;i++) {
			for(int j=0;j<column;j++) {
				result.getMatrix()[i][j]=this.getMatrix()[i][j]+matrix.getMatrix()[i][j];
			}
		}
		return result;
	}
	
	/**
	 * �ҳ˾���
	 * @param matrix �Ҿ���
	 * @return
	 */
	public Matrix RightMultiMatrix(Matrix matrix) {
		//�������������Ҿ����������Ȳſ��Գ�
		if(column!=matrix.getRow()) {
			System.out.println("�������������Ҿ����������Ȳſ��Գ�");
			return null;
		}
		Matrix result = new Matrix(row,matrix.column);
		for(int i=0;i<row;i++) {
			for(int j=0;j<matrix.column;j++) {
				//����
				 new Muti(this, matrix, result, i, j).run();
				//new Thread( new Muti(this, matrix, result, i, j)).start();
			}
		}
		return result;
	}
	
	/**
	 * ���һ������
	 * @param matrix �����
	 * @return
	 */
	public Matrix LeftMultiMatrix(Matrix matrix) {
		//�������������Ҿ����������Ȳſ��Գ�
		if(matrix.getColumn()!=row) {
			System.out.println("�������������Ҿ����������Ȳſ��Գ�");
			return null;
		}
		Matrix result = new Matrix(matrix.getColumn(),column);
		for(int i=0;i<matrix.getColumn();i++) {
			for(int j=0;j<column;j++) {
				//����
				new Muti(matrix, this, result, i, j).run();
				//new Thread(new Muti(matrix, this, result, i, j)).start();
			}
		}
		return result;
	}
	
	
	public Matrix divide(double value) {
		Matrix result = new Matrix(row,column);
		for(int i=0;i<row;i++) {
			for(int j=0;j<column;j++) {
				result.getMatrix()[i][j]=this.getMatrix()[i][j]/value;
			}
		}
		return result;
	}
	
	
	public static void printMatrix(Matrix matrix) {
		int column = matrix.getColumn();
		int row = matrix.getRow();
		
		for(int i=0;i<row;i++) {
			for(int j=0;j<column;j++) {
				System.out.print(String.format("%f ",matrix.getMatrix()[i][j]));
			}
			System.out.println();
		}
		System.out.println(" ");
	}
	
	/**
	 * ������ˣ����ö��̼߳���?������
	 * @author Administrator
	 *
	 */
	class Muti  {
		Matrix mtx1,mtx2,result;
		int m,n;
		public Muti(Matrix mtx1,Matrix mtx2,Matrix result,int i,int j) {
			// TODO Auto-generated constructor stub
			this.mtx1 = mtx1;
			this.mtx2 = mtx2;
			this.result = result;
			m=i;n=j;
		}
		
		public void run() {
			// TODO Auto-generated method stub
			double temp=0;
			int count = mtx1.getColumn();
			for(int i=0;i<count;i++) {
				temp+=mtx1.getMatrix()[m][i]*mtx2.getMatrix()[i][n];
				
			}
			result.getMatrix()[m][n]=temp;
			
					
			
		}
	
	}

	/*
     * ��(h,v)�����λ�õ�����ʽ
     */
    public static double[][] getConfactor(double[][] data, int h, int v) {
    	
    	
        int H = data.length;
        int V = data[0].length;
        double[][] newdata = new double[H-1][V-1];
        for(int i=0; i<newdata.length; i++) {
            if(i < h-1) {
                for(int j=0; j<newdata[i].length; j++) {
                	
                    if(j < v-1) {
                        newdata[i][j] = data[i][j];
                    }else {
                        newdata[i][j] = data[i][j+1];
                    }
                }
            }else {
                for(int j=0; j<newdata[i].length; j++) {
                    if(j < v-1) {
                        newdata[i][j] = data[i+1][j];
                    }else {
                        newdata[i][j] = data[i+1][j+1];
                    }
                }
            }
        }
        
        return newdata;
    }

    /*
     * ��(h,v)�����λ�õ�����ʽ
     */
    public double[][] getConfactor(int h,int v){
    	return getConfactor(this.getMatrix(), h, v);
    }
    
    /**
     * ��������
     */
    public static Matrix getAdjointMatrix(Matrix matrix){
    	int row = matrix.getRow();
    	int column = matrix.getColumn();
    	double[][] result = new double[row][column];
    	for(int i=0;i<row;i++) {
    		for(int j=0;j<column;j++) {
    			//System.out.println(getConfactor(matrix.getMatrix(), i+1, j+1)[0][0]);
    			result[i][j]=getMartrixResult(getConfactor(matrix.getMatrix(), i+1, j+1));
    			int x = i+j+2;
    			if(x%2==0) {
    				x=1;
    			}else {
					x=-1;
				}
    			result[i][j]*=x;
    		}
    	}
    	
    	return new Matrix(result).transpose();
    }
    
    /**
     * ��������
     */
    public Matrix getAdjointMatrix() {
    	return getAdjointMatrix(this);
    }
    
    /*
     * ��������ʽ��ֵ
     * ����ʽһ���Ƿ���
     */
    public static double getMartrixResult(double[][] data) {
    	
    	if(data.length!=data[0].length) {
    		throw new RuntimeException("����ʽ�����Ƿ���");
    	}
    	
    	/**
    	 * 1x1�������
    	 */
    	if(data.length ==1 && data[0].length==1) {
    		return data[0][0];
    	}
    	
        /*
         * ��ά�������
         */
        if(data.length == 2 && data[0].length == 2) {
        	//System.out.println(String.format("��ά����ֵ %f %f %f %f", data[0][0],data[1][1],data[0][1],data[1][0]));
        	//System.out.println( "�����"+Double.toString(data[0][0]*data[1][1] - data[0][1]*data[1][0]));
            return data[0][0]*data[1][1] - data[0][1]*data[1][0];
        }
        /*
         * ��ά���ϵľ������
         */
        double result = 0;
        int num = data.length;
        double[] nums = new double[num];
        for(int i=0; i<data.length; i++) {
            if(i%2 == 0) {
                nums[i] = data[0][i] * getMartrixResult(getConfactor(data, 1, i+1));
            }else {
                nums[i] = -data[0][i] * getMartrixResult(getConfactor(data, 1, i+1));
            }
        }
        for(int i=0; i<data.length; i++) {
            result += nums[i];
        }

//      System.out.println(result);
        return result;
    }

    /*
     * ��������ʽ��ֵ
     * ����ʽһ���Ƿ���
     */
    public double getMartrixResult() {
    	if(getColumn()!=getRow()) {
    		throw new RuntimeException("����ʽ�����Ƿ���");
    	}
    	return getMartrixResult(this.getMatrix());
    }
    
    /**
     * �������
     * @param matrix
     * @return
     */
	public static Matrix getInverseMatrix(Matrix matrix) {
		Matrix adjointMatrix = getAdjointMatrix(matrix);
		double value = matrix.getMartrixResult();
		return adjointMatrix.divide(value);
	}
    
	/**
	 * �������
	 * @return
	 */
	public Matrix getInverseMatrix() {
		return getInverseMatrix(this);
	}
    
	public static void main(String[] args) {
		double [][] a = {{1,2},{3,4}};
		double [][] b = {{1,2,-1},{0,5,-3},{-1,2,4}};
		
		Matrix aMatrix = new Matrix(a);
		Matrix bMatrix = new Matrix(b);
		
		System.out.println("ԭ����");
		printMatrix(aMatrix);
		printMatrix(bMatrix);
		
		
		//System.out.println("�ӷ�����:\n");
		//printMatrix(aMatrix.add(bMatrix));
//		System.out.println("��˲���:\n");
//		printMatrix(aMatrix.LeftMultiMatrix(bMatrix));
//		System.out.println("�ҳ˲���:\n");
//		printMatrix(aMatrix.RightMultiMatrix(bMatrix));
		System.out.println("ת�ò���\n");
		printMatrix(bMatrix.transpose());
		System.out.println("�������");
		printMatrix(bMatrix.getAdjointMatrix());
		System.out.println("����ʽ��ֵ");
		System.out.println(bMatrix.getMartrixResult()+"\n");
		System.out.println("�����");
		printMatrix(bMatrix.getInverseMatrix());
	}
	
}
