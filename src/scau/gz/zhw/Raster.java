package scau.gz.zhw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scau.gz.zhw.Pixel.type;

//栅格数据结构
public class Raster{
	
		public enum DisType{
			Euclidean,//欧式距离
			CityBlock,//曼哈顿距离
			ChessBoard//棋盘距离
		}
	
		
		
		// 如果列数是3列，那么就是九宫格样式。列数可以是其他值。
		//像元大小为1
		
		private int size = 1;
		private  int COLUMN = 3;
		private int ROW = 3;
		//内部点和边界点
		private List<Pixel> internalPoints;
		private List<Pixel> borderPoints;
		//x,y偏移量用于移动栅格
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
		 * 复制栅格
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
		 * 渲染器，用于渲染二值栅格
		 */
		public void render() {
		// TODO Auto-generated method stub
			String[][] renderData = new String[ROW][COLUMN]; 
			
//			for(int i=0;i<ROW;i++) {
//				for(int j=0;j<COLUMN;j++) {
//					if(Double.doubleToLongBits(data[i][j].getValue())==0) {
//						renderData[i][j]="-";
//					}else if(data[i][j].getValue()==1.0){
//						renderData[i][j]="●";
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
						renderData[i][j]="●";
					}
					else{
						renderData[i][j]=" ";
					}
				}
			}
	 
			// 逐行输出，每行输出COLUMN个数据后换行。
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COLUMN; j++) {
					System.out.print(renderData[i][j] + " ");
					//System.out.print(String.format("%s ", renderData[i][j]));
				}
				
				// 换行符
				System.out.println();
			}
		}
		
		/**
		 * 设置栅格值
		 * 行数列数从1开始
		 * @param row
		 * @param column
		 */
		public void setValue(int row,int column,int value) {
			//修正
			if(row==0) {
				row=1;
			}
			if(column==0) {
				column=1;
			}
			
			if(row>ROW || COLUMN>COLUMN) {
				if(row>ROW) {
					throw new RuntimeException("行数超过栅格范围"+row);
				}
				if(COLUMN>COLUMN) {
					throw new RuntimeException("列数超过栅格范围"+column);
				}
			}

			data[row-1][column-1].setValue(value);
		}
		
		public void setValue(int row,int column) {
			
			setValue(row, column, 1);
		}
		
		/**
		 * 栅格值累加
		 */
		public void addValue(int row,int column,int value) {
			//修正
			if(row==0) {
				row=1;
			}
			if(column==0) {
				column=1;
			}
			
			if(row>ROW || COLUMN>COLUMN) {
				if(row>ROW) {
					throw new RuntimeException("行数超过栅格范围"+row);
				}
				if(COLUMN>COLUMN) {
					throw new RuntimeException("列数超过栅格范围"+column);
				}
			}

			data[row-1][column-1].setValue(data[row-1][column-1].getValue()+value);
		}
		
		
		/**
		 * 求出的坐标是栅格的中心点
		 * @param row	行
		 * @param colum	列
		 * @param xOffset	起始栅格点的x位移量
		 * @param yOffset	起始栅格点的y位移量
		 * @return
		 */
		public Point transformVetorPoint(int row,int colum,double xOffset,double yOffset) {
			
			double x0=size+xOffset+this.xOffset;//栅格起始点坐标
			double y0=ROW+yOffset+this.yOffset;
			
			double x = x0+(colum-0.5)*size;
			double y = y0-(row-0.5)*size;
			Point newPoint=new Point(x, y);
//			newPoint = BasicTransform.transform(newPoint, ROW/2, COLUMN/2);
			return newPoint;
			
		}
		
		/**
		 * 
		 * @param point	带转化的矢量点
		 * @param xOffset 起始栅格点的x位移量
		 * @param yOffset	起始栅格点的y位移量
		 * @return 0-行 1-列
		 */
		public int[] transformRasterPoint(Point point,double xOffset,double yOffset) {
			
			double x0=size+xOffset+this.xOffset;//栅格起始点坐标
			double y0=ROW+yOffset+this.yOffset;
			
			
			int[] rowAndColumn = new int[2];
			//像元大小默认为1
			rowAndColumn[0] = (int) (1+ (Math.floor(Math.abs((y0-point.getY())/size))));
			rowAndColumn[1] = (int)(1+(Math.floor(Math.abs((point.getX()-x0))/size)));
			return rowAndColumn;
		}
		
		//翻转像元值
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
		 * 打印二值的栅格 只有零和一
		 */
		public void paint() {
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[0].length; j++) {
					System.out.print((int)data[i][j].getValue() + " ");
				}
				
				// 换行符
				System.out.println();
			}
		}
	 
		/**
		 * 矢量线的栅格化
		 * @param start 起点
		 * @param end	终点
		 * @param type 0-八方向栅格化，1-全路径栅格化、2-恒密度栅格化
		 */
		public void RasterLine(Point start,Point end,int type) {
			RasterLine(start, end, type,0,0);
		}
		
		public void RasterLine(Point start,Point end,int type,double xOffset,double yOffset) {
			//开始点与结束点的起始坐标
			int[] startPoint = transformRasterPoint(start, 0, 0);
			int[] endPoint = transformRasterPoint(end, 0, 0);
			//
			System.out.println(String.format("开始点：(%d,%d)", startPoint[0],startPoint[1]));
			System.out.println(String.format("结束点：(%d,%d)", endPoint[0],endPoint[1]));
			//涂黑,开始点与终止点（值设为1）
			setValue(startPoint[0], startPoint[1]);
			setValue(endPoint[0], endPoint[1]);
			Line line = new Line(start, end);
			//如果是垂直或者水平者直接涂黑
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
				//行差与列差
				int difOfRow = Math.abs(startPoint[0]-endPoint[0]);
				int difOfColumn = Math.abs(startPoint[1]-endPoint[1]);
				
				//若行差大于列差，则逐行求出本行中心线与过两个端点的直线的交点
				//反之则反之
				if(difOfRow>difOfColumn) {
					//求每行中心线
					int num = Math.min(startPoint[0], endPoint[0]); //用于记录当前行
					while(num<=Math.max(startPoint[0], endPoint[0])) {
						//因为只需要求出行，所以设列为1即可
						Point temPoint=transformVetorPoint(num, 1, 0, 0);
						//中心线
						double y = temPoint.getY();
						//求交点
						double x=line.getXByY(y);
						Point result = new Point(x, y);
						//矢量点转栅格
						int[] resultRasterPoint=transformRasterPoint(result, 0, 0);
						//"涂黑"栅格
						setValue(resultRasterPoint[0], resultRasterPoint[1]);
						num++;
					}
				}else {
					//求每列中心线
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
				
				
				//△x>=△y，则计算列号，反之则计算行号
				double difX = Math.abs(start.getX()-end.getX());
				double difY = Math.abs(start.getY()-end.getY());
				double k = line.getSlope();
				
				if(difX>=difY) {
					//开始点与结束点的起始坐标
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
					//用于记录当前行
					int i = startPoint[0];
					//终止行
					int j = endPoint[0];
					//计算起始列号
					int j0 = (int)Math.floor(((((y0-(i-1)*size-line.getStart().getY())/k)+line.getStart().getX()-x0)/size))+1;
					//计算终止列号
					int j1 = (int)Math.floor((((y0-i*size-line.getStart().getY())/k)+line.getStart().getX()-x0)/size)+1;
				
					//System.out.println(String.format("s%d,%d", j0,j1));
					while(i!=j) {
						//i行从j0-j1涂黑
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
						//本行终止列号等于下一行的起始列号
						j0=j1;
						//计算下一行终止列号
						j1 = (int)Math.floor((((y0-i*size-line.getStart().getY())/k)+line.getStart().getX()-x0)/size)+1;
						//System.out.println(String.format("%d,%d", j0,j1));
						
					}
				}else {
					//System.out.println("第二种");
					//开始点与结束点的起始坐标
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
					//记录当前列
					int js = startPoint[1];
					//记录终止列
					int je = endPoint[1];
					
					//计算起始行号
					int is = (int)Math.floor(((line.getStart().getX()-x0-(js-1)*size)*k+y0-line.getStart().getY())/size)+1;
					
					//计算终止行号
					int ie = (int)Math.floor(((line.getStart().getX()-x0-js*size)*k+y0-line.getStart().getY())/size)+1;
					//System.out.println(String.format("first %d,%d", is,ie));
					while(js!=je) {
						int ia,ib;
						//从is-ie行涂黑
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
		
		//矢量化多边形边界线
		public void RasterLine(Polygon polygon,int type) {
			for(Line line:polygon.getLines()) {
				RasterLine(line, type);
			}
		}
		
		//矢量化多边形(用射线法或转角法填充面,用八方向栅格化或全路径栅格化填充边界)
		/**
		 * 
		 * @param polygon
		 * @param RasterType  0-八方向栅格化 1-全路径栅格化
		 * @param type	0-射线法 1-转角法
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
		
		//矢量化多边形(边际代数法)
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
							//保证p在线段的左边
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
							//保证p在线段的左边
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
		 * ------------------------------------------------------------------------栅格细化部分
		 */
		
		
		
		//判断是边界点，内部点，孤立点
		//前提：栅格已经二值化
		public void setNeighbourhood() {
			internalPoints = new ArrayList<>();
			borderPoints = new ArrayList<>();
			for(int i=0;i<ROW;i++) {
				for(int j=0;j<COLUMN;j++) {
					//假-0 真-1
					boolean up=true,down=true,right=true,left=true;
					//判断点的上部是否为0
					
					
					try {
						
						if(data[i-1][j].getValue()==0) {
							up=false;
						}
					} catch (Exception e) {
						// TODO: handle exception
						up=false;
					}
					
					//判断点的下部是否为0
					try {
						if(data[i+1][j].getValue()==0) {
							down=false;
						}
					} catch (Exception e) {
						// TODO: handle exception
						down=false;
					}
					
					
					//判断点的左边是否为0
					try {
						
						if(data[i][j-1].getValue()==0) {
							left=false;
						}
					} catch (Exception e) {
						// TODO: handle exception
						left=false;
					}
					
					//判断点的右边是否为0
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
		 * 骨架图算法（距离变换法搜索中轴线）
		 * 对内部点集i到边界点集e求最小距离
		 * 实际上就是求目标点到最近背景点的距离
		 * 背景点-值为0的像元  目标点-值为1的像元
		 * 求出距离后 对距离进行分类即可得骨架图
		 */
		public void getMinDis(DisType disType) {
			//快速距离模板计算
			//从左至右，从上到下，顺时针寻找周围是否有边界点
			//如果有，则加入计算
			//如果没有，则扩大搜寻范围,最大范围到数组越界
			//最后得出最小距离
			//当前圈层数
			if(borderPoints==null && internalPoints==null) {
				setNeighbourhood();
			}
			for(Pixel i:internalPoints) {
				List<Double> disList = new ArrayList<>();
				//搜索范围
				int cicleNum = 1;
				//上下左右搜寻边界
				int up,down,left,right;
				int upLimit,downLimt,leftLimit,rightLimit;
				upLimit = 1;
				downLimt = ROW;
				leftLimit = 1;
				rightLimit = COLUMN;
				//搜寻遍历
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
					//记录栅格周边是否有非内部点，没有的话则圈数+1
					boolean flag = false;
					//从最左最上开始遍历
					for(int row=up;row<down;row++) {
						for(int col=left;col<right;col++) {
							//判断是否为中心点,即i点,是就跳过
							if(row==i.getRow() && col==i.getColumn()) {
								continue;
							}
							//判断是否是内部点，如果是内部点就直接跳过
							if(data[row][col].getType()!=type.internal) {
								flag = true;
								//计算最小距离
								double dis = calculateDis(disType, i, data[row][col]);
								disList.add(dis);
							}
						}
					}
					//当前圈数内未发现非内部点
					if(!flag) {
						cicleNum++;
					}else {
						//已经发现了非内部点，循环结束
						break;
					}
					
					
				}
				//当前栅格搜索完毕,获取到最近非内部点的距离
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
		
		//二值化处理 大于等于x变为1 其他为0
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
		
		
		
		//最大值计数法 细化栅格
		//主要思想是利用计算周边的目标点是数量（1值栅格数量）挑选出内部与外部
		//并且可以在已挑选出内部和外部栅格的情况下进一步的挑选其内部与外部
		//非初次细化的前提是挑选前要进行二值化处理，二值阈值为4(因为1值数量最大为4)
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
				//防止越界问题出现
				
				
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
			
				
				//左上角,要避免把当前像元计入计数栅格
				if(up!=row && left!=column) {
					lt=data[up-1][left-1].getValue();
				}
				
				//左下角
				if(down!=row && left!=column) {
					ld=data[down-1][left-1].getValue();
				}
				
				//右上角
				if(up!=row && right!=column ) {
					rt=data[up-1][right-1].getValue();
					
				}
				
				//右下角
				if(down!=row && right!=column) {
					rd=data[down-1][right-1].getValue();
				}
				
				//比较左上，左下，右上，右下灰度最大值
				//1则计数1次，0计数0次
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
//					System.out.println(String.format("行数:%d 列数 %d", row,column));
//					System.out.println(String.format("left:%d right:%d up:%d down:%d", left,right,up,down));
//					System.out.println(String.format("lt:%f ld:%f rt:%f rd:%f", lt,ld,rt,rd));
//					System.out.println("次数: "+num);
//					//System.out.println(data[2][10].getValue());
//					System.out.println();
//				}
				
				
				data[row-1][column-1].setOneValueNum(num);	
				
				
			}
			//二值化处理 因为最大值为4 所以二值化把次数大于等于4的栅格的value值都设为1 其他为0
			this.Binarize(4);
			
		}
		
		
		
		
		/**
		 * 编码------------- 3 2 1
		 * 编码------------- 4 p 0
		 * 编码------------- 5 6 7		
		 * @param neighbor
		 * @return
		 */
		//获取四联通的联通块数,范围3x3
		public static  int getFourConnectedNum(int []neighbor) {
		    int count=neighbor[0]-(neighbor[0]&neighbor[1]&neighbor[2]);
		    count+=neighbor[2]-(neighbor[2]&neighbor[3]&neighbor[4]);
		    count+=neighbor[4]-(neighbor[4]&neighbor[5]&neighbor[6]);
		    count+=neighbor[6]-(neighbor[6]&neighbor[7]&neighbor[0]);
		    return count;
		  
		}
		
		//获取八连通的联通块数,范围3x3
		public static int getEightConnectedNum(int []neighbor) {
			//计算补集x=1-x
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
		 * 剖皮跟踪法细化栅格
		 * 基本思想是跟踪边缘栅格，然后考察栅格的四联通状况予以是否删除
		 * 跟踪结束条件是回到原来的栅格
		 * 剖皮结束条件是如果下一次剖皮跟踪没有要删除的像元，则结束剖皮
		 */
		public void edgeTrackingPeeling() {
			this.setNeighbourhood();
			//先找到一个位于栅格边缘的像元
			//保留一定的内部点 以免失真
			if(internalPoints.isEmpty() || internalPoints.size()<5)
				return;
			
			
			Pixel curPixel = internalPoints.get(0);
			//按照顺时针方向扫描，达到两个目的:1、决定当前像元是否被剖去 2、找到下一个边缘像元
			
			int count = 0;
			while(true) {
			//当前像元的行列
			int row,column;
			//整个栅格的上下左右边界
			int upLimit,downLimt,leftLimit,rightLimit;
			//当前3x3的上下左右边界
			int up,down,left,right;
			upLimit = 1;
			downLimt = ROW;
			leftLimit = 1;
			rightLimit = COLUMN;
			
			row = curPixel.getRow();
			column = curPixel.getColumn();
			//防止越界问题出现
			
			
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
		
			//顺时针跟踪像元 起点(row-1,column)
			//先寻找下一个边缘像元
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
				//需要注意的一点
				//如果只有顺时针遍历这个条件是不够的，还需要是边界点才行
				if(neighbor[i].getValue()==1 &&(neighbor[i].getType()==type.boundary ) ) {
					next = neighbor[i];
				}
			}
			
			
			if(next==p0) {
				//一侧的跟踪结束
				//next=curPixel;
				//System.out.println("无0-1的突变");
			}
			
			if(next==curPixel) {
				break;
			}
			
			//判断当前像元是否被删除
			//删除的像元标记为3 保留标记为2
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
		System.out.println("跟踪次数 "+count++);
//		//二值化
//		//像元值为3的删去，为2的保留
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
		
		/**编码-------------   N  
		 * 编码------------- 5 6 7
		 * 编码------------w 4 p 0 E
		 * 编码------------- 3 2 1
		 * 编码-------------   S 
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
			
			
			//渲染器
			//根据像元类型渲染
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
					
					// 逐行输出，每行输出COLUMN个数据后换行。
					for (int i = 0; i < raster.getROW(); i++) {
						for (int j = 0; j < raster.getCOLUMN(); j++) {
							System.out.print(renderData[i][j] + " ");
							//System.out.print(String.format("%s ", renderData[i][j]));
						}
						
						// 换行符
						System.out.println();
					}
					
				}
			};
			//基于距离变换提取骨架渲染
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
//							renderData[i][j]="●";
//						}
						if(raster.getData()[i][j].getNearDis()>=5)
						renderData[i][j]=Integer.toString((int)raster.getData()[i][j].getNearDis());
						else
							renderData[i][j]=" ";
					}
				}
					
					// 逐行输出，每行输出COLUMN个数据后换行。
					for (int i = 0; i < raster.getROW(); i++) {
						for (int j = 0; j < raster.getCOLUMN(); j++) {
							System.out.print(renderData[i][j] + " ");
							//System.out.print(String.format("%s ", renderData[i][j]));
						}
						
						// 换行符
						System.out.println();
					}
					
				}
			};
			//基于最大值计数渲染 1值数大于等于4
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
//							renderData[i][j]="●";
//						}
						if(raster.getData()[i][j].getOneValueNum()>=4)
						renderData[i][j]=Integer.toString((int)raster.getData()[i][j].getOneValueNum());
						else
							renderData[i][j]=" ";
					}
				}
					
					// 逐行输出，每行输出COLUMN个数据后换行。
					for (int i = 0; i < raster.getROW(); i++) {
						for (int j = 0; j < raster.getCOLUMN(); j++) {
							System.out.print(renderData[i][j] + " ");
							//System.out.print(String.format("%s ", renderData[i][j]));
						}
						
						// 换行符
						System.out.println();
					}
					
				}
			};
			//根据最小距离渲染
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
					
					// 逐行输出，每行输出COLUMN个数据后换行。
					for (int i = 0; i < raster.getROW(); i++) {
						for (int j = 0; j < raster.getCOLUMN(); j++) {
							System.out.print(renderData[i][j] + " ");
							//System.out.print(String.format("%s ", renderData[i][j]));
						}
						
						// 换行符
						System.out.println();
					}
					
				}
			};
			//根据值value渲染
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
					
					// 逐行输出，每行输出COLUMN个数据后换行。
					for (int i = 0; i < raster.getROW(); i++) {
						for (int j = 0; j < raster.getCOLUMN(); j++) {
							System.out.print(renderData[i][j] + " ");
							//System.out.print(String.format("%s ", renderData[i][j]));
						}
						
						// 换行符
						System.out.println();
					}
					
				}
			};
			
			
			//raster.render(valueRender);
			
			//polygon.showGUI();
			//raster.render(valueRender);
			
			
			System.out.println("基于距离变换提取骨架图算法:");
			System.out.println("原始数据");
			Raster raster2 = raster.copy();
			
			raster.render();
			System.out.println();
			raster.getMinDis(DisType.CityBlock);
			Raster raster3 = raster.copy();
			raster.render(myRenderer);
			
			
			
			
			
			System.out.println("基于最大数值计算提取骨架图算法:");
			System.out.println("原始数据");
			
			raster.render();
			System.out.println();
			//raster.paint();
			System.out.println("最大计数1次");
			raster.maxiunm();
			raster.render();
			

			System.out.println("最大计数2次");
			raster.maxiunm();
			raster.render();

			
			System.out.println("最大计数3次");
			raster.maxiunm();
			raster.render();
		
			
			
			System.out.println("边缘跟踪剥皮法: 原始数据:");
			System.out.println("内部点数 "+raster2.getInternalPoints().size());
			System.out.println("边界点数 "+raster2.getBorderPoints().size());
			raster2.render(pixelTypeRender);
			
			for(int num=0;num<5;num++) {
				System.out.println("跟踪剥皮法 第"+(num+1)+"次");
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
			
			System.out.println("边缘跟踪剥皮法: 最终数据:");
			System.out.println("内部点数 "+raster2.getInternalPoints().size());
			System.out.println("边界点数 "+raster2.getBorderPoints().size());
			raster2.render();
			
			
			
			raster3.render(mindisRender);
		}
		
		
		
}

