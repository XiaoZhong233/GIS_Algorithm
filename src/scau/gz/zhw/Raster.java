package scau.gz.zhw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scau.gz.zhw.Pixel.type;

//դ�����ݽṹ
public class Raster{
	
		public enum DisType{
			Euclidean,//ŷʽ����
			CityBlock,//�����پ���
			ChessBoard//���̾���
		}
	
		
		
		// ���������3�У���ô���ǾŹ�����ʽ����������������ֵ��
		//��Ԫ��СΪ1
		
		private int size = 1;
		private  int COLUMN = 3;
		private int ROW = 3;
		//�ڲ���ͱ߽��
		private List<Pixel> internalPoints;
		private List<Pixel> borderPoints;
		//x,yƫ���������ƶ�դ��
		private int xOffset,yOffset;
	 
		
		private  Pixel[][] data = {{new Pixel(0),new Pixel(0),new Pixel(0)},{new Pixel(0),new Pixel(0),new Pixel(0)},{new Pixel(0),new Pixel(0),new Pixel(0)} };
	 
		public Raster(int row,int column) {
			this.ROW = row;
			this.COLUMN = column;
			this.data = new Pixel[row][column];
			for(int i =0;i<row;i++) {
				for(int j=0;j<column;j++) {
					Pixel pixel = new Pixel(0d);
					pixel.setRow(i+1);
					pixel.setColumn(j+1);
					pixel.setPoint(transformVetorPoint(i, j, 0, 0));
					data[i][j]=pixel;
				}
			}
			
		}
		
		public Raster(Pixel[][] data) {
			this.COLUMN = data[0].length;
			this.ROW = data.length;
			this.data = data;
		}
		
		public int getCOLUMN() {
			return COLUMN;
		}
		
		public int getROW() {
			return ROW;
		}
		
		public List<Pixel> getInternalPoints() {
			return internalPoints;
		}
		
		public List<Pixel> getBorderPoints() {
			return borderPoints;
		}
	 
		public Pixel[][] getData() {
			return data;
		}
		
		
		public int getxOffset() {
			return xOffset;
		}
		
		public int getyOffset() {
			return yOffset;
		}
		
		public void setxOffset(int xOffset) {
			this.xOffset = xOffset;
		}
		
		public void setyOffset(int yOffset) {
			this.yOffset = yOffset;
		}
		
		
		public Pixel getPixel(int row,int column) {
			if((row<1 || row >ROW) || (column<1 || column>COLUMN  ))
				return null;
			return data[row-1][column-1];
		}
		
		
		
		/**
		 * ����դ��
		 * @return
		 */
		public Raster copy() {
			Raster raster = new Raster(ROW,COLUMN);
			for(int i=0;i<ROW;i++) {
				for(int j=0;j<COLUMN;j++) {
					raster.data[i][j].setValue(this.data[i][j].getValue());
					raster.data[i][j].setRow(this.data[i][j].getRow());
					raster.data[i][j].setColumn(this.data[i][j].getColumn());
					raster.data[i][j].setOneValueNum(this.data[i][j].getOneValueNum());
					raster.data[i][j].setNearDis(this.data[i][j].getNearDis());
					raster.data[i][j].setType(this.data[i][j].getType());
					raster.data[i][j].setPoint(this.data[i][j].getPoint());
//					Collections.copy(raster.internalPoints, this.getInternalPoints());
//					Collections.copy(raster.borderPoints, this.getBorderPoints());
					
				}
			}
			raster.setNeighbourhood();
			return raster;
		}
		
		public void render(Renderer renderer) {
			renderer.render(this);
		}
		
		/**
		 * ��Ⱦ����������Ⱦ��ֵդ��
		 */
		public void render() {
		// TODO Auto-generated method stub
			String[][] renderData = new String[ROW][COLUMN]; 
			
//			for(int i=0;i<ROW;i++) {
//				for(int j=0;j<COLUMN;j++) {
//					if(Double.doubleToLongBits(data[i][j].getValue())==0) {
//						renderData[i][j]="-";
//					}else if(data[i][j].getValue()==1.0){
//						renderData[i][j]="��";
//					}
//				}
//			}
				
				
//			for(int i=0;i<ROW;i++) {
//				for(int j=0;j<COLUMN;j++) {
//					if(data[i][j].getType()==type.isolated) {
//						renderData[i][j]="-";
//					}else if(data[i][j].getType()==type.boundary){
//						renderData[i][j]="0";
//					}else {
//						renderData[i][j]="*";
//					}
//				}
//			}
			
			for(int i=0;i<ROW;i++) {
				for(int j=0;j<COLUMN;j++) {
					//renderData[i][j] = Integer.toString((int)data[i][j].getNearDis());
					if(data[i][j].getValue()>=1) {
						renderData[i][j]="��";
					}
					else{
						renderData[i][j]=" ";
					}
				}
			}
	 
			// ���������ÿ�����COLUMN�����ݺ��С�
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COLUMN; j++) {
					System.out.print(renderData[i][j] + " ");
					//System.out.print(String.format("%s ", renderData[i][j]));
				}
				
				// ���з�
				System.out.println();
			}
		}
		
		/**
		 * ����դ��ֵ
		 * ����������1��ʼ
		 * @param row
		 * @param column
		 */
		public void setValue(int row,int column,int value) {
			//����
			if(row==0) {
				row=1;
			}
			if(column==0) {
				column=1;
			}
			
			if(row>ROW || COLUMN>COLUMN) {
				if(row>ROW) {
					throw new RuntimeException("��������դ��Χ"+row);
				}
				if(COLUMN>COLUMN) {
					throw new RuntimeException("��������դ��Χ"+column);
				}
			}

			data[row-1][column-1].setValue(value);
		}
		
		public void setValue(int row,int column) {
			
			setValue(row, column, 1);
		}
		
		/**
		 * դ��ֵ�ۼ�
		 */
		public void addValue(int row,int column,int value) {
			//����
			if(row==0) {
				row=1;
			}
			if(column==0) {
				column=1;
			}
			
			if(row>ROW || COLUMN>COLUMN) {
				if(row>ROW) {
					throw new RuntimeException("��������դ��Χ"+row);
				}
				if(COLUMN>COLUMN) {
					throw new RuntimeException("��������դ��Χ"+column);
				}
			}

			data[row-1][column-1].setValue(data[row-1][column-1].getValue()+value);
		}
		
		
		/**
		 * �����������դ������ĵ�
		 * @param row	��
		 * @param colum	��
		 * @param xOffset	��ʼդ����xλ����
		 * @param yOffset	��ʼդ����yλ����
		 * @return
		 */
		public Point transformVetorPoint(int row,int colum,double xOffset,double yOffset) {
			
			double x0=size+xOffset+this.xOffset;//դ����ʼ������
			double y0=ROW+yOffset+this.yOffset;
			
			double x = x0+(colum-0.5)*size;
			double y = y0-(row-0.5)*size;
			Point newPoint=new Point(x, y);
//			newPoint = BasicTransform.transform(newPoint, ROW/2, COLUMN/2);
			return newPoint;
			
		}
		
		/**
		 * 
		 * @param point	��ת����ʸ����
		 * @param xOffset ��ʼդ����xλ����
		 * @param yOffset	��ʼդ����yλ����
		 * @return 0-�� 1-��
		 */
		public int[] transformRasterPoint(Point point,double xOffset,double yOffset) {
			
			double x0=size+xOffset+this.xOffset;//դ����ʼ������
			double y0=ROW+yOffset+this.yOffset;
			
			
			int[] rowAndColumn = new int[2];
			//��Ԫ��СĬ��Ϊ1
			rowAndColumn[0] = (int) (1+ (Math.floor(Math.abs((y0-point.getY())/size))));
			rowAndColumn[1] = (int)(1+(Math.floor(Math.abs((point.getX()-x0))/size)));
			return rowAndColumn;
		}
		
		//��ת��Ԫֵ
		public void reverse() {
			for(int i=0;i<data.length;i++) {
				for(int j=0;j<data[0].length;j++) {
					if(Double.doubleToLongBits(data[i][j].getValue())==1) {
						data[i][j].setValue(0d);;
					}else {
						data[i][j].setValue(1d);;
					}
				}
			}
		}
		
		
		/**
		 * ��ӡ��ֵ��դ�� ֻ�����һ
		 */
		public void paint() {
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[0].length; j++) {
					System.out.print((int)data[i][j].getValue() + " ");
				}
				
				// ���з�
				System.out.println();
			}
		}
	 
		/**
		 * ʸ���ߵ�դ��
		 * @param start ���
		 * @param end	�յ�
		 * @param type 0-�˷���դ�񻯣�1-ȫ·��դ�񻯡�2-���ܶ�դ��
		 */
		public void RasterLine(Point start,Point end,int type) {
			RasterLine(start, end, type,0,0);
		}
		
		public void RasterLine(Point start,Point end,int type,double xOffset,double yOffset) {
			//��ʼ������������ʼ����
			int[] startPoint = transformRasterPoint(start, 0, 0);
			int[] endPoint = transformRasterPoint(end, 0, 0);
			//
			System.out.println(String.format("��ʼ�㣺(%d,%d)", startPoint[0],startPoint[1]));
			System.out.println(String.format("�����㣺(%d,%d)", endPoint[0],endPoint[1]));
			//Ϳ��,��ʼ������ֹ�㣨ֵ��Ϊ1��
			setValue(startPoint[0], startPoint[1]);
			setValue(endPoint[0], endPoint[1]);
			Line line = new Line(start, end);
			//����Ǵ�ֱ����ˮƽ��ֱ��Ϳ��
			if(line.isHorizontal() || Double.doubleToLongBits(start.getX())==Double.doubleToLongBits(end.getX())) {
				if(line.isHorizontal()) {
					int i = startPoint[0];
					int j0 = Math.min(startPoint[1], endPoint[1]);
					int j1 = Math.max(startPoint[1], endPoint[1]);
					while(j0!=j1) {
						setValue(i, j0);
						j0++;
					}
				}else {
					int i = startPoint[1];
					int j0 = Math.min(startPoint[0], endPoint[0]);
					int j1 = Math.max(startPoint[0], endPoint[0]);
					while(j0!=j1) {
						setValue(j0, i);
						j0++;
					}
				}
				return;
			}
			switch (type) {
			case 0:
				//�в����в�
				int difOfRow = Math.abs(startPoint[0]-endPoint[0]);
				int difOfColumn = Math.abs(startPoint[1]-endPoint[1]);
				
				//���в�����в���������������������������˵��ֱ�ߵĽ���
				//��֮��֮
				if(difOfRow>difOfColumn) {
					//��ÿ��������
					int num = Math.min(startPoint[0], endPoint[0]); //���ڼ�¼��ǰ��
					while(num<=Math.max(startPoint[0], endPoint[0])) {
						//��Ϊֻ��Ҫ����У���������Ϊ1����
						Point temPoint=transformVetorPoint(num, 1, 0, 0);
						//������
						double y = temPoint.getY();
						//�󽻵�
						double x=line.getXByY(y);
						Point result = new Point(x, y);
						//ʸ����תդ��
						int[] resultRasterPoint=transformRasterPoint(result, 0, 0);
						//"Ϳ��"դ��
						setValue(resultRasterPoint[0], resultRasterPoint[1]);
						num++;
					}
				}else {
					//��ÿ��������
					int num = Math.min(startPoint[1], endPoint[1]);
					while(num<Math.max(startPoint[1], endPoint[1])) {
						Point temPoint = transformVetorPoint(1, num, 0, 0);
						double x = temPoint.getX();
						double y = line.getYByX(x);
						Point result = new Point(x, y);
						int[] resultRasterPoint = transformRasterPoint(result, 0, 0);
						setValue(resultRasterPoint[0], resultRasterPoint[1]);
						num++;
					}
				}
				
				break;
			case 1:
				
				double x0=1+xOffset+this.xOffset;
				double y0=ROW+yOffset+this.yOffset;
				
				
				//��x>=��y��������кţ���֮������к�
				double difX = Math.abs(start.getX()-end.getX());
				double difY = Math.abs(start.getY()-end.getY());
				double k = line.getSlope();
				
				if(difX>=difY) {
					//��ʼ������������ʼ����
					if(Double.doubleToLongBits(end.getY())<Double.doubleToLongBits(start.getY())) {
						startPoint = transformRasterPoint(start, 0, 0);
						endPoint = transformRasterPoint(end, 0, 0);
						line = new Line(start,end);
					}else {
						startPoint = transformRasterPoint(end, 0, 0);
						endPoint = transformRasterPoint(start, 0, 0);
						line = new Line(end,start);
					}
					k = line.getSlope();
					//System.out.println(k);
					//���ڼ�¼��ǰ��
					int i = startPoint[0];
					//��ֹ��
					int j = endPoint[0];
					//������ʼ�к�
					int j0 = (int)Math.floor(((((y0-(i-1)*size-line.getStart().getY())/k)+line.getStart().getX()-x0)/size))+1;
					//������ֹ�к�
					int j1 = (int)Math.floor((((y0-i*size-line.getStart().getY())/k)+line.getStart().getX()-x0)/size)+1;
				
					//System.out.println(String.format("s%d,%d", j0,j1));
					while(i!=j) {
						//i�д�j0-j1Ϳ��
						int ja,jb;
						ja=j0;
						jb=j1;
						System.out.println(String.format("%d,%d", j0,j1));
						while(ja!=jb) {
							
							if(ja<jb) {
								//System.out.println(String.format("i=%d ja=%d", i,ja));
								setValue(i, ja);
								ja++;
							}
							else {
								//System.out.println(String.format("i=%d jb=%d", i,jb));
								setValue(i, jb);
								jb++;
							}
						}
						i++;
						//������ֹ�кŵ�����һ�е���ʼ�к�
						j0=j1;
						//������һ����ֹ�к�
						j1 = (int)Math.floor((((y0-i*size-line.getStart().getY())/k)+line.getStart().getX()-x0)/size)+1;
						//System.out.println(String.format("%d,%d", j0,j1));
						
					}
				}else {
					//System.out.println("�ڶ���");
					//��ʼ������������ʼ����
					if(Double.doubleToLongBits(end.getX())>Double.doubleToLongBits(start.getX())) {
						startPoint = transformRasterPoint(start, 0, 0);
						endPoint = transformRasterPoint(end, 0, 0);
						line = new Line(start,end);
					}else {
						startPoint = transformRasterPoint(end, 0, 0);
						endPoint = transformRasterPoint(start, 0, 0);
						line = new Line(end,start);
					}
					k = line.getSlope();
					//System.out.println(k);
					//��¼��ǰ��
					int js = startPoint[1];
					//��¼��ֹ��
					int je = endPoint[1];
					
					//������ʼ�к�
					int is = (int)Math.floor(((line.getStart().getX()-x0-(js-1)*size)*k+y0-line.getStart().getY())/size)+1;
					
					//������ֹ�к�
					int ie = (int)Math.floor(((line.getStart().getX()-x0-js*size)*k+y0-line.getStart().getY())/size)+1;
					//System.out.println(String.format("first %d,%d", is,ie));
					while(js!=je) {
						int ia,ib;
						//��is-ie��Ϳ��
						ia=is;
						ib=ie;
						while(ia!=ib) {
							
							if(ia<ib) {
								//System.out.println(ia);
								setValue(ia, js);
								ia++;
								
							}
							else {
								//System.out.println(ib);
								setValue(ib, js);
								ib++;
							}
						}
						js++;
						is=ie;
						ie = (int)Math.floor(((line.getStart().getX()-x0-js*size)*k+y0-line.getStart().getY())/size)+1;
						//System.out.println(String.format("%d,%d", is,ie));
					}
					
				}
				break;
			case 2:
				break;
			default:
				break;
			}
			
		}
		
		public void RasterLine(Line line,int type) {
			RasterLine(line.getStart(), line.getEnd(), type);
		}
		
		//ʸ��������α߽���
		public void RasterLine(Polygon polygon,int type) {
			for(Line line:polygon.getLines()) {
				RasterLine(line, type);
			}
		}
		
		//ʸ���������(�����߷���ת�Ƿ������,�ð˷���դ�񻯻�ȫ·��դ�����߽�)
		/**
		 * 
		 * @param polygon
		 * @param RasterType  0-�˷���դ�� 1-ȫ·��դ��
		 * @param type	0-���߷� 1-ת�Ƿ�
		 */
		public void RasterPolygon1(Polygon polygon,int RasterType,int type) {
			RasterLine(polygon, RasterType);
			
			for(int i=0;i<=ROW;i++) {
				for(int j=0;j<=COLUMN;j++) {
					//count++;
					//System.out.println(String.format("%d %d", i,j));
					if(CalculateBasic.isPointAtPolygon(polygon, transformVetorPoint(i, j, 0, 0), type)) {
						setValue(i, j);
					}
				}
			}
			//System.out.println("counter "+count);
		}
		
		//ʸ���������(�߼ʴ�����)
		public void RasterPolygon2(Polygon polygon) {
			List<Line> lines = polygon.getLines();
			//RasterLine(polygon, 1);
			
			for(Line line : lines) {
				Point start = line.getStart();
				Point end = line.getEnd();
				
				int[] startPoint = transformRasterPoint(start, 0, 0);
				int[] endPoint = transformRasterPoint(end, 0, 0);
				
				int startRow = Math.max(startPoint[0], endPoint[0]);
				int endRow = Math.min(startPoint[0], endPoint[0]);
				
				
				if(line.isUp()) {
					while(startRow>=endRow) {
						int column = 0;
						while(column<=Math.max(startPoint[1], endPoint[1])) {
							Point p = transformVetorPoint(endRow, column, 0, 0);
							//��֤p���߶ε����
							if(Double.doubleToLongBits(p.getX()) < Double.doubleToLongBits(line.getXByY(p.getY()))
									&& Double.doubleToLongBits(line.getXByY(p.getY()))>=Double.doubleToLongBits(Math.min(line.getStart().getX(),line.getEnd().getX()))
									&& Double.doubleToLongBits(line.getXByY(p.getY()))<=Double.doubleToLongBits(Math.max(line.getStart().getX(), line.getEnd().getX()))) {
								addValue(endRow, column, -1);
							}
							column++;
						}
						endRow++;
					}
				}else if(line.isDown()) {
					while(startRow>=endRow) {
						int column = 0;
						while(column<=Math.max(startPoint[1], endPoint[1])) {
							Point p = transformVetorPoint(endRow, column, 0, 0);
							//��֤p���߶ε����
							if(Double.doubleToLongBits(p.getX()) < Double.doubleToLongBits(line.getXByY(p.getY()))
									&& Double.doubleToLongBits(line.getXByY(p.getY()))>=Double.doubleToLongBits(Math.min(line.getStart().getX(),line.getEnd().getX()))
									&& Double.doubleToLongBits(line.getXByY(p.getY()))<=Double.doubleToLongBits(Math.max(line.getStart().getX(), line.getEnd().getX()))) {
								addValue(endRow, column, 1);
							}
							column++;
						}
						endRow++;
					}
				}
				
			}
		}
		
		
		/**
		 * ------------------------------------------------------------------------դ��ϸ������
		 */
		
		
		
		//�ж��Ǳ߽�㣬�ڲ��㣬������
		//ǰ�᣺դ���Ѿ���ֵ��
		public void setNeighbourhood() {
			internalPoints = new ArrayList<>();
			borderPoints = new ArrayList<>();
			for(int i=0;i<ROW;i++) {
				for(int j=0;j<COLUMN;j++) {
					//��-0 ��-1
					boolean up=true,down=true,right=true,left=true;
					//�жϵ���ϲ��Ƿ�Ϊ0
					
					
					try {
						
						if(data[i-1][j].getValue()==0) {
							up=false;
						}
					} catch (Exception e) {
						// TODO: handle exception
						up=false;
					}
					
					//�жϵ���²��Ƿ�Ϊ0
					try {
						if(data[i+1][j].getValue()==0) {
							down=false;
						}
					} catch (Exception e) {
						// TODO: handle exception
						down=false;
					}
					
					
					//�жϵ������Ƿ�Ϊ0
					try {
						
						if(data[i][j-1].getValue()==0) {
							left=false;
						}
					} catch (Exception e) {
						// TODO: handle exception
						left=false;
					}
					
					//�жϵ���ұ��Ƿ�Ϊ0
					try {
						
						if(data[i][j+1].getValue()==0) {
							right=false;
						}
					} catch (Exception e) {
						// TODO: handle exception
						right=false;
					}
					
					if(!up && !down && !left && !right) {
						data[i][j].setType(type.isolated);
						
					}else if(up && down && left && right) {
						data[i][j].setType(type.internal);
						internalPoints.add(data[i][j]);
					}else {
						data[i][j].setType(type.boundary);
						borderPoints.add(data[i][j]);
					}
					
					
				}
			}
			
		}
			
		/**
		 * �Ǽ�ͼ�㷨������任�����������ߣ�
		 * ���ڲ��㼯i���߽�㼯e����С����
		 * ʵ���Ͼ�����Ŀ��㵽���������ľ���
		 * ������-ֵΪ0����Ԫ  Ŀ���-ֵΪ1����Ԫ
		 * �������� �Ծ�����з��༴�ɵùǼ�ͼ
		 */
		public void getMinDis(DisType disType) {
			//���پ���ģ�����
			//�������ң����ϵ��£�˳ʱ��Ѱ����Χ�Ƿ��б߽��
			//����У���������
			//���û�У���������Ѱ��Χ,���Χ������Խ��
			//���ó���С����
			//��ǰȦ����
			if(borderPoints==null && internalPoints==null) {
				setNeighbourhood();
			}
			for(Pixel i:internalPoints) {
				List<Double> disList = new ArrayList<>();
				//������Χ
				int cicleNum = 1;
				//����������Ѱ�߽�
				int up,down,left,right;
				int upLimit,downLimt,leftLimit,rightLimit;
				upLimit = 1;
				downLimt = ROW;
				leftLimit = 1;
				rightLimit = COLUMN;
				//��Ѱ����
				for(int curCir=0;curCir<cicleNum;curCir++) {
					try {	
						up = i.getRow()-cicleNum;
						if(up<upLimit) {
							up=upLimit;
						}
					} catch (Exception e) {
						// TODO: handle exception
						up = i.getRow();
					}
					
					try {
						down = i.getRow()+cicleNum;
						if(down>downLimt) {
							down = downLimt;
						}
					} catch (Exception e) {
						// TODO: handle exception
						down = i.getRow();
					}
					
					try {
						left = i.getColumn()-cicleNum;
						if(left<leftLimit) {
							left = leftLimit;
						}
					} catch (Exception e) {
						// TODO: handle exception
						left = i.getColumn();
					}
					
					try {
						right = i.getColumn()+cicleNum;
						if(right>rightLimit) {
							right=rightLimit;
						}
					} catch (Exception e) {
						// TODO: handle exception
						right = i.getColumn();
					}
					//��¼դ���ܱ��Ƿ��з��ڲ��㣬û�еĻ���Ȧ��+1
					boolean flag = false;
					//���������Ͽ�ʼ����
					for(int row=up;row<down;row++) {
						for(int col=left;col<right;col++) {
							//�ж��Ƿ�Ϊ���ĵ�,��i��,�Ǿ�����
							if(row==i.getRow() && col==i.getColumn()) {
								continue;
							}
							//�ж��Ƿ����ڲ��㣬������ڲ����ֱ������
							if(data[row][col].getType()!=type.internal) {
								flag = true;
								//������С����
								double dis = calculateDis(disType, i, data[row][col]);
								disList.add(dis);
							}
						}
					}
					//��ǰȦ����δ���ַ��ڲ���
					if(!flag) {
						cicleNum++;
					}else {
						//�Ѿ������˷��ڲ��㣬ѭ������
						break;
					}
					
					
				}
				//��ǰդ���������,��ȡ��������ڲ���ľ���
				if(!disList.isEmpty()) {
					double min = Collections.min(disList);
					i.setNearDis(min);
				}
			}
		}
		
		
		
		private double calculateDis(DisType disType,Pixel s1,Pixel s2) {
			double dis = 0;
			switch (disType) {
			case Euclidean:
				dis = Math.sqrt(Math.pow(s1.getRow()-s2.getRow(), 2)+Math.pow(s1.getColumn()-s2.getColumn(), 2))*size;
				break;
			case CityBlock:
				dis = Math.abs(s1.getRow()-s2.getRow())+Math.abs(s1.getColumn()-s2.getColumn())*size;
				break;
			case ChessBoard:
				dis = Math.max(Math.abs(s1.getRow()-s2.getRow()), Math.abs(s1.getColumn()-s2.getColumn()))*size;
				break;
			default:
				dis = Math.sqrt(Math.pow(s1.getRow()-s2.getRow(), 2)+Math.pow(s1.getColumn()-s2.getColumn(), 2))*size;
				break;
			}
			return dis;
		}
		
		//��ֵ������ ���ڵ���x��Ϊ1 ����Ϊ0
		private void Binarize(double x) {
			for(int i=0;i<ROW;i++) {
				for(int j=0;j<COLUMN;j++) {
					if(data[i][j].getOneValueNum()>=x) {
						data[i][j].setValue(1);
					}else {
						data[i][j].setValue(0);
					}
				}
			}
		}
		
		
		
		//���ֵ������ ϸ��դ��
		//��Ҫ˼�������ü����ܱߵ�Ŀ�����������1ֵդ����������ѡ���ڲ����ⲿ
		//���ҿ���������ѡ���ڲ����ⲿդ�������½�һ������ѡ���ڲ����ⲿ
		//�ǳ���ϸ����ǰ������ѡǰҪ���ж�ֵ��������ֵ��ֵΪ4(��Ϊ1ֵ�������Ϊ4)
		public void maxiunm() {
			
			if(borderPoints==null && internalPoints==null) {
				setNeighbourhood();
			}
			List<Pixel> tartgetPoint = new ArrayList<>();
			tartgetPoint.addAll(borderPoints);
			tartgetPoint.addAll(internalPoints);
			
//			for(int i=0;i<ROW;i++) {
//				for(int j=0;j<COLUMN;j++) {
//					tartgetPoint.add(data[i][j]);		
//				}
//			}
			
			
			
			for(Pixel pixel : tartgetPoint) {
				
				
				//System.out.println(data[2][10].getValue());
				
				double lt=0,ld=0,rt=0,rd=0;
				int row,column;
				int upLimit,downLimt,leftLimit,rightLimit;
				int up,down,left,right;
				upLimit = 1;
				downLimt = ROW;
				leftLimit = 1;
				rightLimit = COLUMN;
				
				row = pixel.getRow();
				column = pixel.getColumn();
				//��ֹԽ���������
				
				
				try {
					up = row-1;
					if(up<upLimit) {
						up = upLimit;
					}
				} catch (Exception e) {
					// TODO: handle exception
					up = row;
				}
				
				
				try {
					down = row+1;
					if(down>downLimt) {
						down = downLimt;
					}
				} catch (Exception e) {
					// TODO: handle exception
					down = row;
				}
				
				try {
					left = column-1;
					if(left<leftLimit) {
						left = leftLimit;
					}
				} catch (Exception e) {
					// TODO: handle exception
					left = column;
				}
				
				try {
					right = column+1;
					if(right>rightLimit) {
						right=rightLimit;
					}
				} catch (Exception e) {
					// TODO: handle exception
					right = column;
				}
			
				
				//���Ͻ�,Ҫ����ѵ�ǰ��Ԫ�������դ��
				if(up!=row && left!=column) {
					lt=data[up-1][left-1].getValue();
				}
				
				//���½�
				if(down!=row && left!=column) {
					ld=data[down-1][left-1].getValue();
				}
				
				//���Ͻ�
				if(up!=row && right!=column ) {
					rt=data[up-1][right-1].getValue();
					
				}
				
				//���½�
				if(down!=row && right!=column) {
					rd=data[down-1][right-1].getValue();
				}
				
				//�Ƚ����ϣ����£����ϣ����»Ҷ����ֵ
				//1�����1�Σ�0����0��
				int num=0;
				if(lt>=1) {
					num++;
				}
				if(ld>=1) {
					num++;
				}
				if(rt>=1) {
					num++;
				}
				if(rd>=1) {
					num++;
				}
				
				
				
//				if(flag>0) {
//					flag--;
//					System.out.println(String.format("����:%d ���� %d", row,column));
//					System.out.println(String.format("left:%d right:%d up:%d down:%d", left,right,up,down));
//					System.out.println(String.format("lt:%f ld:%f rt:%f rd:%f", lt,ld,rt,rd));
//					System.out.println("����: "+num);
//					//System.out.println(data[2][10].getValue());
//					System.out.println();
//				}
				
				
				data[row-1][column-1].setOneValueNum(num);	
				
				
			}
			//��ֵ������ ��Ϊ���ֵΪ4 ���Զ�ֵ���Ѵ������ڵ���4��դ���valueֵ����Ϊ1 ����Ϊ0
			this.Binarize(4);
			
		}
		
		
		
		
		/**
		 * ����------------- 3 2 1
		 * ����------------- 4 p 0
		 * ����------------- 5 6 7		
		 * @param neighbor
		 * @return
		 */
		//��ȡ����ͨ����ͨ����,��Χ3x3
		public static  int getFourConnectedNum(int []neighbor) {
		    int count=neighbor[0]-(neighbor[0]&neighbor[1]&neighbor[2]);
		    count+=neighbor[2]-(neighbor[2]&neighbor[3]&neighbor[4]);
		    count+=neighbor[4]-(neighbor[4]&neighbor[5]&neighbor[6]);
		    count+=neighbor[6]-(neighbor[6]&neighbor[7]&neighbor[0]);
		    return count;
		  
		}
		
		//��ȡ����ͨ����ͨ����,��Χ3x3
		public static int getEightConnectedNum(int []neighbor) {
			//���㲹��x=1-x
		    for(int i=0;i<8;i++)

		    {
		       neighbor[i]=neighbor[i]==0?1:0;
		    } 
		    
		    int count=neighbor[0]-(neighbor[0]&neighbor[1]&neighbor[2]);
		    count+=neighbor[2]-(neighbor[2]&neighbor[3]&neighbor[4]);
		    count+=neighbor[4]-(neighbor[4]&neighbor[5]&neighbor[6]);
		    count+=neighbor[6]-(neighbor[6]&neighbor[7]&neighbor[0]);
		    return count;
		}
		
		/**
		 * ��Ƥ���ٷ�ϸ��դ��
		 * ����˼���Ǹ��ٱ�Եդ��Ȼ�󿼲�դ�������ͨ״�������Ƿ�ɾ��
		 * ���ٽ��������ǻص�ԭ����դ��
		 * ��Ƥ���������������һ����Ƥ����û��Ҫɾ������Ԫ���������Ƥ
		 */
		public void edgeTrackingPeeling() {
			this.setNeighbourhood();
			//���ҵ�һ��λ��դ���Ե����Ԫ
			//����һ�����ڲ��� ����ʧ��
			if(internalPoints.isEmpty() || internalPoints.size()<5)
				return;
			
			
			Pixel curPixel = internalPoints.get(0);
			//����˳ʱ�뷽��ɨ�裬�ﵽ����Ŀ��:1��������ǰ��Ԫ�Ƿ���ȥ 2���ҵ���һ����Ե��Ԫ
			
			int count = 0;
			while(true) {
			//��ǰ��Ԫ������
			int row,column;
			//����դ����������ұ߽�
			int upLimit,downLimt,leftLimit,rightLimit;
			//��ǰ3x3���������ұ߽�
			int up,down,left,right;
			upLimit = 1;
			downLimt = ROW;
			leftLimit = 1;
			rightLimit = COLUMN;
			
			row = curPixel.getRow();
			column = curPixel.getColumn();
			//��ֹԽ���������
			
			
			try {
				up = row-1;
				if(up<upLimit) {
					up = upLimit;
				}
			} catch (Exception e) {
				// TODO: handle exception
				up = row;
			}
			
			
			try {
				down = row+1;
				if(down>downLimt) {
					down = downLimt;
				}
			} catch (Exception e) {
				// TODO: handle exception
				down = row;
			}
			
			try {
				left = column-1;
				if(left<leftLimit) {
					left = leftLimit;
				}
			} catch (Exception e) {
				// TODO: handle exception
				left = column;
			}
			
			try {
				right = column+1;
				if(right>rightLimit) {
					right=rightLimit;
				}
			} catch (Exception e) {
				// TODO: handle exception
				right = column;
			}
		
			//˳ʱ�������Ԫ ���(row-1,column)
			//��Ѱ����һ����Ե��Ԫ
			/**
			 * 7 0 1
			 * 6 p 2 
			 * 5 4 3
			 */
			
			
			Pixel p0 = this.getPixel(up, column);
			Pixel p1 = this.getPixel(up, right);
			Pixel p2 = this.getPixel(row, right);
			Pixel p3 = this.getPixel(down, right);
			Pixel p4 = this.getPixel(down, column);
			Pixel p5 = this.getPixel(down,left);
			Pixel p6 = this.getPixel(row,left);
			Pixel p7 = this.getPixel(up, left);
			
			
			Pixel[] neighbor = {p0,p1,p2,p3,p4,p5,p6,p7};
			
			Pixel next = p0;
			for(int i=1;i<neighbor.length;i++) {
				//��Ҫע���һ��
				//���ֻ��˳ʱ�������������ǲ����ģ�����Ҫ�Ǳ߽�����
				if(neighbor[i].getValue()==1 &&(neighbor[i].getType()==type.boundary ) ) {
					next = neighbor[i];
				}
			}
			
			
			if(next==p0) {
				//һ��ĸ��ٽ���
				//next=curPixel;
				//System.out.println("��0-1��ͻ��");
			}
			
			if(next==curPixel) {
				break;
			}
			
			//�жϵ�ǰ��Ԫ�Ƿ�ɾ��
			//ɾ������Ԫ���Ϊ3 �������Ϊ2
			int [] neighbors = {(int)p2.getValue(),(int)p3.getValue(),
					(int)p4.getValue(),(int)p5.getValue(),(int)p6.getValue(),
					(int)p7.getValue(),(int)p0.getValue(),(int)p1.getValue()};
			int num=getFourConnectedNum(neighbors);
			if(num<2) {
				if(curPixel.getType()!=type.isolated)
					curPixel.setValue(3);
			}else {
				if(curPixel.getType()!=type.isolated)
					curPixel.setValue(2);
			}
			
			curPixel=next;
			count++;
		}
		System.out.println("���ٴ��� "+count++);
//		//��ֵ��
//		//��ԪֵΪ3��ɾȥ��Ϊ2�ı���
//		for(int i=0;i<ROW;i++) {
//			for(int j=0;j<COLUMN;j++) {
//				if(data[i][j].getValue()==3) {
//					data[i][j].setValue(0);
//				}else if(data[i][j].getValue()==2){
//					data[i][j].setValue(1);
//				}
//			}
//		}
	}
		
		/**����-------------   N  
		 * ����------------- 5 6 7
		 * ����------------w 4 p 0 E
		 * ����------------- 3 2 1
		 * ����-------------   S 
		 * @return
		 */
		public Pixel[] simplify_freeMan() {
			return null;
		}
		
		
		
		
		
		public static void main(String[] args) {
			Raster raster = new Raster(30,30);
			Point p = new Point(.5, .5);
			System.out.println(p);
			int[] a = raster.transformRasterPoint(p, 0, 0);
			System.out.println(String.format("I=%d,J=%d", a[0],a[1]));
			
			
			Point b = raster.transformVetorPoint(12, 25, 0, 0);
			System.out.println(b);
			a = raster.transformRasterPoint(b, 0, 0);
			System.out.println(String.format("I=%d,J=%d", a[0],a[1]));
			
			
			
			p=new Point(2, 30);
			b=new Point(15, 15) ;
			Point d,e,r,t,y,o,q;
//			o=new Point(10, 22);
//			d=new Point(12, 25);
//			e=new Point(20, 25);
//			r=new Point(22, 18);
//			t=new Point(13, 18);
//			y=new Point(16, 10);

			o=new Point(3,9);
			d=new Point(9,3);
			e=new Point(18,18);
			r=new Point(12,18);
			t=new Point(21,3);
			y=new Point(27,9);
			q=new Point(15, 30);
			
			
			Point[] points = new Point[] {o,d,e,r,t,y,q};
			Polygon polygon = new Polygon(points,true);
			System.out.println("startPoint "+p.toString());
			System.out.println("endPoint "+b.toString());
			
			//DrawCircle drawCircle = new DrawCircle(new Point(0, 0), 10);
			//List<Point> pList = drawCircle.draw();
			//polygon = new Polygon(pList, true);
			raster.RasterPolygon1(polygon,1,1);
			//raster.RasterPolygon2(polygon);
			//Line line = new Line(o,q);
			//raster.RasterLine(line, 1);
			
			
			//��Ⱦ��
			//������Ԫ������Ⱦ
			Renderer pixelTypeRender = new Renderer() {
				@Override
				public void render(Raster raster) {
					// TODO Auto-generated method stub
					String[][] renderData = new String[raster.getROW()][raster.getCOLUMN()];		
					for(int i=0;i<raster.getROW();i++) {
					for(int j=0;j<raster.getCOLUMN();j++) {
						if(raster.data[i][j].getType()==type.isolated) {
							renderData[i][j]=" ";
						}else if(raster.data[i][j].getType()==type.boundary){
							renderData[i][j]="B";
						}else {
							renderData[i][j]="I";
						}
						
					}
				}
					
					// ���������ÿ�����COLUMN�����ݺ��С�
					for (int i = 0; i < raster.getROW(); i++) {
						for (int j = 0; j < raster.getCOLUMN(); j++) {
							System.out.print(renderData[i][j] + " ");
							//System.out.print(String.format("%s ", renderData[i][j]));
						}
						
						// ���з�
						System.out.println();
					}
					
				}
			};
			//���ھ���任��ȡ�Ǽ���Ⱦ
			Renderer myRenderer = new Renderer() {
				@Override
				public void render(Raster raster) {
					// TODO Auto-generated method stub
					String[][] renderData = new String[raster.getROW()][raster.getCOLUMN()];		
					for(int i=0;i<raster.getROW();i++) {
					for(int j=0;j<raster.getCOLUMN();j++) {
//						if(Double.doubleToLongBits(raster.data[i][j].getValue())==0) {
//							renderData[i][j]="-";
//						}else if(raster.data[i][j].getValue()==1.0){
//							renderData[i][j]="��";
//						}
						if(raster.getData()[i][j].getNearDis()>=5)
						renderData[i][j]=Integer.toString((int)raster.getData()[i][j].getNearDis());
						else
							renderData[i][j]=" ";
					}
				}
					
					// ���������ÿ�����COLUMN�����ݺ��С�
					for (int i = 0; i < raster.getROW(); i++) {
						for (int j = 0; j < raster.getCOLUMN(); j++) {
							System.out.print(renderData[i][j] + " ");
							//System.out.print(String.format("%s ", renderData[i][j]));
						}
						
						// ���з�
						System.out.println();
					}
					
				}
			};
			//�������ֵ������Ⱦ 1ֵ�����ڵ���4
			@SuppressWarnings("unused")
			Renderer oneValueNumRender = new Renderer() {
				@Override
				public void render(Raster raster) {
					// TODO Auto-generated method stub
					String[][] renderData = new String[raster.getROW()][raster.getCOLUMN()];		
					for(int i=0;i<raster.getROW();i++) {
					for(int j=0;j<raster.getCOLUMN();j++) {
//						if(Double.doubleToLongBits(raster.data[i][j].getValue())==0) {
//							renderData[i][j]="-";
//						}else if(raster.data[i][j].getValue()==1.0){
//							renderData[i][j]="��";
//						}
						if(raster.getData()[i][j].getOneValueNum()>=4)
						renderData[i][j]=Integer.toString((int)raster.getData()[i][j].getOneValueNum());
						else
							renderData[i][j]=" ";
					}
				}
					
					// ���������ÿ�����COLUMN�����ݺ��С�
					for (int i = 0; i < raster.getROW(); i++) {
						for (int j = 0; j < raster.getCOLUMN(); j++) {
							System.out.print(renderData[i][j] + " ");
							//System.out.print(String.format("%s ", renderData[i][j]));
						}
						
						// ���з�
						System.out.println();
					}
					
				}
			};
			//������С������Ⱦ
			@SuppressWarnings("unused")
			Renderer mindisRender = new Renderer() {


				@Override
				public void render(Raster raster) {
					// TODO Auto-generated method stub
					String[][] renderData = new String[raster.getROW()][raster.getCOLUMN()];		
					for(int i=0;i<raster.getROW();i++) {
					for(int j=0;j<raster.getCOLUMN();j++) {
						if(raster.getData()[i][j].getNearDis()!=0) {
							renderData[i][j]=Integer.toString((int)raster.getData()[i][j].getNearDis());
						}else {
							renderData[i][j]=" ";
						}
					}
				}
					
					// ���������ÿ�����COLUMN�����ݺ��С�
					for (int i = 0; i < raster.getROW(); i++) {
						for (int j = 0; j < raster.getCOLUMN(); j++) {
							System.out.print(renderData[i][j] + " ");
							//System.out.print(String.format("%s ", renderData[i][j]));
						}
						
						// ���з�
						System.out.println();
					}
					
				}
			};
			//����ֵvalue��Ⱦ
			Renderer valueRender = new Renderer() {


				@Override
				public void render(Raster raster) {
					// TODO Auto-generated method stub
					String[][] renderData = new String[raster.getROW()][raster.getCOLUMN()];		
					for(int i=0;i<raster.getROW();i++) {
					for(int j=0;j<raster.getCOLUMN();j++) {
						//renderData[i][j]=Integer.toString((int)raster.getData()[i][j].getValue());
						if(raster.getData()[i][j].getValue()!=0)
							renderData[i][j]=Integer.toString((int)raster.getData()[i][j].getValue());
							else
								renderData[i][j]=" ";
					}
				}
					
					// ���������ÿ�����COLUMN�����ݺ��С�
					for (int i = 0; i < raster.getROW(); i++) {
						for (int j = 0; j < raster.getCOLUMN(); j++) {
							System.out.print(renderData[i][j] + " ");
							//System.out.print(String.format("%s ", renderData[i][j]));
						}
						
						// ���з�
						System.out.println();
					}
					
				}
			};
			
			
			//raster.render(valueRender);
			
			//polygon.showGUI();
			//raster.render(valueRender);
			
			
			System.out.println("���ھ���任��ȡ�Ǽ�ͼ�㷨:");
			System.out.println("ԭʼ����");
			Raster raster2 = raster.copy();
			
			raster.render();
			System.out.println();
			raster.getMinDis(DisType.CityBlock);
			Raster raster3 = raster.copy();
			raster.render(myRenderer);
			
			
			
			
			
			System.out.println("���������ֵ������ȡ�Ǽ�ͼ�㷨:");
			System.out.println("ԭʼ����");
			
			raster.render();
			System.out.println();
			//raster.paint();
			System.out.println("������1��");
			raster.maxiunm();
			raster.render();
			

			System.out.println("������2��");
			raster.maxiunm();
			raster.render();

			
			System.out.println("������3��");
			raster.maxiunm();
			raster.render();
		
			
			
			System.out.println("��Ե���ٰ�Ƥ��: ԭʼ����:");
			System.out.println("�ڲ����� "+raster2.getInternalPoints().size());
			System.out.println("�߽���� "+raster2.getBorderPoints().size());
			raster2.render(pixelTypeRender);
			
			for(int num=0;num<5;num++) {
				System.out.println("���ٰ�Ƥ�� ��"+(num+1)+"��");
				raster2.edgeTrackingPeeling();
				raster2.render(valueRender);
				for(int i=0;i<raster2.getROW();i++) {
					for(int j=0;j<raster2.getCOLUMN();j++) {
						if(raster2.data[i][j].getValue()==3) {
							raster2.data[i][j].setValue(0);
						}else if(raster2.data[i][j].getValue()==2){
							raster2.data[i][j].setValue(1);
						}
					}
				}
				
			}
			
			System.out.println("��Ե���ٰ�Ƥ��: ��������:");
			System.out.println("�ڲ����� "+raster2.getInternalPoints().size());
			System.out.println("�߽���� "+raster2.getBorderPoints().size());
			raster2.render();
			
			
			
			raster3.render(mindisRender);
		}
		
		
		
}

