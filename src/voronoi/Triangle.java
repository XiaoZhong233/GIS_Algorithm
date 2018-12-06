package voronoi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * �����Σ������������㣬����������nά��
 * �������뱣�����е��ά����һ�µ�
 * @author Administrator
 *
 */
public class Triangle extends ArraySet<NPoint>{
	
	private static int idGenerator = 0;
	private final int id = idGenerator++;
	public static boolean moreInfo = false; 
	//�����ε����ԲԲ�ģ������ε�����
	private NPoint circumcenter = null; 
	
	public Triangle(NPoint...nPoints) {
		// TODO Auto-generated constructor stub
		this(Arrays.asList(nPoints));
	} 
	
	
	public Triangle(Collection<? extends NPoint> collectionPoints) {
		// TODO Auto-generated constructor stub
		super(collectionPoints);
		if (this.size() != 3)
            throw new IllegalArgumentException("�����α���ֻ����������");
	}
	
    @Override
    public String toString () {
        if (!moreInfo) return "Triangle" + id;
        return "Triangle" + id + super.toString();
    }
	
    /**
     * �����ȡ�����εĶ��㣬�������ǲ����������ĵ�(����)
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
     * �ж������������Ƿ�����
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
     * ����������ĳһ����ĵ�������������
     * @param vertex �������ϵ�ĳһ��
     *  @throws IllegalArgumentException ����㲻Ϊ�����ζ����׳�
     * @return 
     */
    public ArraySet<NPoint> facetOpposite (NPoint vertex) {
        ArraySet<NPoint> facet = new ArraySet<NPoint>(this);
        if (!facet.remove(vertex))
            throw new IllegalArgumentException("Vertex not in triangle");
        return facet;
    }
    
    /**
     * ��������ε�����
     * @return ����
     */
    public NPoint getCircumcenter () {
        if (circumcenter == null)
            circumcenter = NPoint.circumcenter(this.toArray(new NPoint[0]));
        return circumcenter;
    }
    
    /**
     * ��֧�ּ����
     */
    @Override
    public boolean add(NPoint item) {
    	// TODO Auto-generated method stub
    	throw new UnsupportedOperationException();
    }
    
    /**
     * ��������֧���Ƴ���
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
    
    /*������������ȷ��ÿ�����������ڴ��ж��Ƕ�һ�޶���*/
    @Override
    public int hashCode () {
        return (int)(id^(id>>>32));
    }

    @Override
    public boolean equals (Object o) {
        return (this == o);
    }
    
}
