package voronoi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * 三角形，包含了三个点，这个点可以是n维的
 * 不过必须保持所有点的维度是一致的
 * @author Administrator
 *
 */
public class Triangle extends ArraySet<NPoint>{
	
	private static int idGenerator = 0;
	private final int id = idGenerator++;
	public static boolean moreInfo = false; 
	//三角形的外接圆圆心，三角形的外心
	private NPoint circumcenter = null; 
	
	public Triangle(NPoint...nPoints) {
		// TODO Auto-generated constructor stub
		this(Arrays.asList(nPoints));
	} 
	
	
	public Triangle(Collection<? extends NPoint> collectionPoints) {
		// TODO Auto-generated constructor stub
		super(collectionPoints);
		if (this.size() != 3)
            throw new IllegalArgumentException("三角形必须只能有三个点");
	}
	
    @Override
    public String toString () {
        if (!moreInfo) return "Triangle" + id;
        return "Triangle" + id + super.toString();
    }
	
    /**
     * 随意获取三角形的顶点，但不能是参数传进来的点(坏点)
     * Get arbitrary vertex of this triangle, but not any of the bad vertices.
     * @param badVertices one or more bad vertices
     * @return a vertex of this triangle, but not one of the bad vertices
     * @throws NoSuchElementException if no vertex found
     */
    public NPoint getVertexButNot (NPoint... badVertices) {
        Collection<NPoint> bad = Arrays.asList(badVertices);
        for (NPoint v: this) if (!bad.contains(v)) return v;
        throw new NoSuchElementException("No vertex found");
    }
    
    /**
     * 判断两个三角形是否相邻
     * @param triangle
     * @return
     */
    public boolean isNeighbor (Triangle triangle) {
        int count = 0;
        for (NPoint vertex: this)
            if (!triangle.contains(vertex)) count++;
        return count == 1;
    }
    
    /**
     * 返回三角形某一顶点的的其他两个顶点
     * @param vertex 三角形上的某一点
     *  @throws IllegalArgumentException 如果点不为三角形顶点抛出
     * @return 
     */
    public ArraySet<NPoint> facetOpposite (NPoint vertex) {
        ArraySet<NPoint> facet = new ArraySet<NPoint>(this);
        if (!facet.remove(vertex))
            throw new IllegalArgumentException("Vertex not in triangle");
        return facet;
    }
    
    /**
     * 获得三角形的外心
     * @return 外心
     */
    public NPoint getCircumcenter () {
        if (circumcenter == null)
            circumcenter = NPoint.circumcenter(this.toArray(new NPoint[0]));
        return circumcenter;
    }
    
    /**
     * 不支持加入点
     */
    @Override
    public boolean add(NPoint item) {
    	// TODO Auto-generated method stub
    	throw new UnsupportedOperationException();
    }
    
    /**
     * 遍历器不支持移除点
     */
    @Override
    public Iterator<NPoint> iterator () {
        return new Iterator<NPoint>() {
            private Iterator<NPoint> it = Triangle.super.iterator();
            public boolean hasNext() {return it.hasNext();}
            public NPoint next() {return it.next();}
            public void remove() {throw new UnsupportedOperationException();}
        };
    }
    
    /*下面两个方法确保每个三角形在内存中都是独一无二的*/
    @Override
    public int hashCode () {
        return (int)(id^(id>>>32));
    }

    @Override
    public boolean equals (Object o) {
        return (this == o);
    }
    
}
