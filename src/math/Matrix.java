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
	 * 加法运算
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
	 * 右乘矩阵
	 * @param matrix 右矩阵
	 * @return
	 */
	public Matrix RightMultiMatrix(Matrix matrix) {
		//左矩阵的列数与右矩阵的行数相等才可以乘
		if(column!=matrix.getRow()) {
			System.out.println("左矩阵的列数与右矩阵的行数相等才可以乘");
			return null;
		}
		Matrix result = new Matrix(row,matrix.column);
		for(int i=0;i<row;i++) {
			for(int j=0;j<matrix.column;j++) {
				//计算
				 new Muti(this, matrix, result, i, j).run();
				//new Thread( new Muti(this, matrix, result, i, j)).start();
			}
		}
		return result;
	}
	
	/**
	 * 左乘一个矩阵
	 * @param matrix 左矩阵
	 * @return
	 */
	public Matrix LeftMultiMatrix(Matrix matrix) {
		//左矩阵的列数与右矩阵的行数相等才可以乘
		if(matrix.getColumn()!=row) {
			System.out.println("左矩阵的列数与右矩阵的行数相等才可以乘");
			return null;
		}
		Matrix result = new Matrix(matrix.getColumn(),column);
		for(int i=0;i<matrix.getColumn();i++) {
			for(int j=0;j<column;j++) {
				//计算
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
	 * 行列相乘，利用多线程计算?不好用
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
     * 求(h,v)坐标的位置的余子式
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
     * 求(h,v)坐标的位置的余子式
     */
    public double[][] getConfactor(int h,int v){
    	return getConfactor(this.getMatrix(), h, v);
    }
    
    /**
     * 求伴随矩阵
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
     * 求伴随矩阵
     */
    public Matrix getAdjointMatrix() {
    	return getAdjointMatrix(this);
    }
    
    /*
     * 计算行列式的值
     * 行列式一定是方阵
     */
    public static double getMartrixResult(double[][] data) {
    	
    	if(data.length!=data[0].length) {
    		throw new RuntimeException("行列式必须是方阵");
    	}
    	
    	/**
    	 * 1x1矩阵计算
    	 */
    	if(data.length ==1 && data[0].length==1) {
    		return data[0][0];
    	}
    	
        /*
         * 二维矩阵计算
         */
        if(data.length == 2 && data[0].length == 2) {
        	//System.out.println(String.format("二维矩阵值 %f %f %f %f", data[0][0],data[1][1],data[0][1],data[1][0]));
        	//System.out.println( "结果："+Double.toString(data[0][0]*data[1][1] - data[0][1]*data[1][0]));
            return data[0][0]*data[1][1] - data[0][1]*data[1][0];
        }
        /*
         * 二维以上的矩阵计算
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
     * 计算行列式的值
     * 行列式一定是方阵
     */
    public double getMartrixResult() {
    	if(getColumn()!=getRow()) {
    		throw new RuntimeException("行列式必须是方阵");
    	}
    	return getMartrixResult(this.getMatrix());
    }
    
    /**
     * 求逆矩阵
     * @param matrix
     * @return
     */
	public static Matrix getInverseMatrix(Matrix matrix) {
		Matrix adjointMatrix = getAdjointMatrix(matrix);
		double value = matrix.getMartrixResult();
		return adjointMatrix.divide(value);
	}
    
	/**
	 * 求逆矩阵
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
		
		System.out.println("原矩阵");
		printMatrix(aMatrix);
		printMatrix(bMatrix);
		
		
		//System.out.println("加法测试:\n");
		//printMatrix(aMatrix.add(bMatrix));
//		System.out.println("左乘测试:\n");
//		printMatrix(aMatrix.LeftMultiMatrix(bMatrix));
//		System.out.println("右乘测试:\n");
//		printMatrix(aMatrix.RightMultiMatrix(bMatrix));
		System.out.println("转置测试\n");
		printMatrix(bMatrix.transpose());
		System.out.println("伴随矩阵");
		printMatrix(bMatrix.getAdjointMatrix());
		System.out.println("行列式求值");
		System.out.println(bMatrix.getMartrixResult()+"\n");
		System.out.println("逆矩阵");
		printMatrix(bMatrix.getInverseMatrix());
	}
	
}
