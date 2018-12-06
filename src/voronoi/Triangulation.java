package voronoi;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import scau.gz.zhw.PaintVector;



public class Triangulation extends AbstractSet<Triangle>{
	
    private Triangle mostRecent = null;      // Most recently "active" triangle
    private Graph<Triangle> triGraph;        // Holds triangles for navigation ������ͼ,��¼�����ε����ӹ�ϵ
    private Triangle initTriangle;

    
    /**
     * ��ȡ��ʼ�����Σ�Ҳ���ǳ���������
     * @return
     */
    public Triangle getInitTriangle() {
		return initTriangle;
	}
    
	@Override
	public Iterator<Triangle> iterator() {
		// TODO Auto-generated method stub
		return triGraph.nodeSet().iterator();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return triGraph.nodeSet().size();
	}
	
    /**
     * All sites must fall within the initial triangle.
     * @param triangle the initial triangle
     */
    public Triangulation (Triangle triangle) {
        triGraph = new Graph<Triangle>();
        triGraph.add(triangle);
        mostRecent = triangle;
        initTriangle = triangle;
    }

    /**
     * True iff triangle is a member of this triangulation.
     * This method isn't required by AbstractSet, but it improves efficiency.
     * @param triangle the object to check for membership
     */
    public boolean contains (Object triangle) {
        return triGraph.nodeSet().contains(triangle);
    }
    
    /**
     * �����������ϲ�����ĳ����ڽ�������
     * Report neighbor opposite the given vertex of triangle.
     * @param site a vertex of triangle
     * @param triangle we want the neighbor of this triangle
     * @return the neighbor opposite site in triangle; null if none
     * @throws IllegalArgumentException if site is not in this triangle
     */
    public Triangle neighborOpposite (NPoint site, Triangle triangle) {
        if (!triangle.contains(site))
            throw new IllegalArgumentException("Bad vertex; not in triangle");
        for (Triangle neighbor: triGraph.neighbors(triangle)) {
            if (!neighbor.contains(site)) return neighbor;
        }
        return null;
    }

    /**
     * �����ڽ�������
     * Return the set of triangles adjacent to triangle.
     * @param triangle the triangle to check
     * @return the neighbors of triangle
     */
    public Set<Triangle> neighbors(Triangle triangle) {
        return triGraph.neighbors(triangle);
    }
    
    /**
     * ��һ��˳��˳ʱ�����ʱ�룩����ĳ�����ε��ڽ�������
     * Report triangles surrounding site in order (cw or ccw).
     * @param site we want the surrounding triangles for this site
     * @param triangle a "starting" triangle that has site as a vertex
     * @return all triangles surrounding site in order (cw or ccw)
     * @throws IllegalArgumentException if site is not in triangle
     */
    public List<Triangle> surroundingTriangles (NPoint site, Triangle triangle) {
        if (!triangle.contains(site))
            throw new IllegalArgumentException("Site not in triangle");
        List<Triangle> list = new ArrayList<Triangle>();
        Triangle start = triangle;
        NPoint guide = triangle.getVertexButNot(site);        // Affects cw or ccw
        while (true) {
            list.add(triangle);
            Triangle previous = triangle;
            triangle = this.neighborOpposite(guide, triangle); // Next triangle
            guide = previous.getVertexButNot(site, guide);     // Update guide
            if (triangle == start) break;
        }
        return list;
    }
    
    /**
     * ���ݵ���Ѱ��������
     * ��һ�ֳ��Ա����ڽ����������������ε���Χ�������Σ�����ҵ��˾ͺ�
     * ���򣬳��Ա�������
     * Locate the triangle with point inside it or on its boundary.
     * @param point the point to locate
     * @return the triangle that holds point; null if no such triangle
     */
    public Triangle locate (NPoint point) {
        Triangle triangle = mostRecent;
        if (!this.contains(triangle)) triangle = null;

        // Try a directed walk (this works fine in 2D, but can fail in 3D)
        Set<Triangle> visited = new HashSet<Triangle>();
        while (triangle != null) {
            if (visited.contains(triangle)) { // This should never happen
                System.out.println("Warning: Caught in a locate loop");
                break;
            }
            visited.add(triangle);
            // Corner opposite point
            NPoint corner = point.isOutside(triangle.toArray(new NPoint[0]));
            //����null������������������
            if (corner == null) return triangle;
            //����ֱ����Ѱ��һ��������
            triangle = this.neighborOpposite(corner, triangle);
        }
        // No luck; try brute force
        System.out.println("Warning: Checking all triangles for " + point);
        for (Triangle tri: this) {
            if (point.isOutside(tri.toArray(new NPoint[0])) == null) return tri;
        }
        // No such triangle
        System.out.println("Warning: No triangle holds " + point);
        return null;
    }
    
    
    /**
     * ���ķ���
     * ȷ��������Ӱ���������
     * �����������ܱ���Ҫ��ʧ��������
     * ����˼�룺��������ָ����������Σ��жϵ������������Բ�Ĺ�ϵ��
     * �������������Բ�ڣ���������β�Ϊdelaunay�����Σ����Ƴ���
     * �������������Բ�⣺����ڽ����������Ĺ�ϵ��
     * Determine the cavity caused by site.
     * @param site the site causing the cavity
     * @param triangle the triangle containing site
     * @return set of all triangles that have site in their circumcircle
     */
    private Set<Triangle> getCavity (NPoint site, Triangle triangle) {
    	//��¼���Ƴ���������
        Set<Triangle> encroached = new HashSet<Triangle>();
        //��¼�������ܱ�������
        Queue<Triangle> toBeChecked = new LinkedList<Triangle>();
        //��¼�Ѽ����ܱ�������
        Set<Triangle> marked = new HashSet<Triangle>();
        toBeChecked.add(triangle);
        marked.add(triangle);
        while (!toBeChecked.isEmpty()) {
            triangle = toBeChecked.remove();
            if (site.vsCircumcircle(triangle.toArray(new NPoint[0])) == 1)
                continue; // Site outside triangle => triangle not in cavity
            encroached.add(triangle);
            // Check the neighbors
            for (Triangle neighbor: triGraph.neighbors(triangle)){
                if (marked.contains(neighbor)) continue;
                marked.add(neighbor);
                toBeChecked.add(neighbor);
            }
        }
        return encroached;
    }

    /**
     * ���ķ���
     * �Ƴ����������Σ������µ�Delaury������
     * Update the triangulation by removing the cavity triangles and then
     * filling the cavity with new triangles.
     * @param site the site that created the cavity ������
     * @param cavity the triangles with site in their circumcircle ����������
     * @return one of the new triangles ��������
     */
    private Triangle update (NPoint site, Set<Triangle> cavity) {
    	//��¼���������ε�������ļ���,����������������ı߽���
        Set<Set<NPoint>> boundary = new HashSet<Set<NPoint>>();
        //��¼���º�������μ���
        Set<Triangle> theTriangles = new HashSet<Triangle>();
        // Find boundary facets and adjacent triangles
        //��ȡ���������εı߽磬��Щ�߽�����ڹ�����������
        for (Triangle triangle: cavity) {
            theTriangles.addAll(neighbors(triangle));
            for (NPoint vertex: triangle) {
                Set<NPoint> facet = triangle.facetOpposite(vertex);
                if (boundary.contains(facet)) boundary.remove(facet);
                else boundary.add(facet);
            }
        }
        theTriangles.removeAll(cavity);        // Adj triangles only

        // Remove the cavity triangles from the triangulation
        for (Triangle triangle: cavity) triGraph.remove(triangle);

        // Build each new triangle and add it to the triangulation
        Set<Triangle> newTriangles = new HashSet<Triangle>();
        for (Set<NPoint> vertices: boundary) {
            vertices.add(site);
            //��߽�ĵ�Թ�����������
            Triangle tri = new Triangle(vertices);
            triGraph.add(tri);
            newTriangles.add(tri);
        }

        // Update the graph links for each new triangle 
        // �ڽ������������������ν���ͼ��ϵ
        theTriangles.addAll(newTriangles);    // Adj triangle + new triangles
        for (Triangle triangle: newTriangles)
            for (Triangle other: theTriangles)
                if (triangle.isNeighbor(other))
                    triGraph.add(triangle, other);

        // Return one of the new triangles
        return newTriangles.iterator().next();
    }

    /**
     * �����㲢����Delaunay������
     * �㷨˼�����£�
     * ������ͼ��ʾ������֮����ڽ���ϵ
     * ��ȷ�����ݵ��������������������Ӱ���£�ԭ�����α�Ϊ��Delaunay�����Σ�ȷ���Ƿ�ΪDelaunay�����ε�ԭ�����жϵ��Ƿ��ڸ������ε����Բ�⣩��
     * �ڻ�ȡ�Ե�Ե���ʽ��ʾ��������ı߽磨�������������εĶ��㣬��ȡ���ö�����������㣬����������Ѿ����������ˣ���ȥ���õ�ԣ���������Ŀ���ǻ��͹���߽磩
     * ��ȥ���������������ܱ������ε�ͼ��ϵ
     * �ܸ��������������ݱ߽繹���µ�������
     * �ݽ��������������ܱ������ε�ͼ��ϵ
     * Place a new site into the DT.
     * Nothing happens if the site matches an existing DT vertex.
     * @param site the new Pnt
     * @throws IllegalArgumentException if site does not lie in any triangle
     */
    public void delaunayPlace (NPoint site) {
        // Uses straightforward scheme rather than best asymptotic time

        // Locate containing triangle
        Triangle triangle = locate(site);
        // Give up if no containing triangle or if site is already in DT
        if (triangle == null)
            throw new IllegalArgumentException("No containing triangle");
        if (triangle.contains(site)) return;

        // Determine the cavity and update the triangulation
        Set<Triangle> cavity = getCavity(site, triangle);
        mostRecent = update(site, cavity);
    }
    
    /**
     * ��Ԫ����
     * Main program; used for testing.
     */
    public static void main (String[] args) {
        Triangle tri =
            new Triangle(new NPoint(-100,100), new NPoint(100,100), new NPoint(0,-100));
        System.out.println("Triangle created: " + tri);
        Triangulation dt = new Triangulation(tri);
        System.out.println("DelaunayTriangulation created: " + dt);
        dt.delaunayPlace(new NPoint(0,0));
        dt.delaunayPlace(new NPoint(10,0));
        dt.delaunayPlace(new NPoint(0,10));
        System.out.println("After adding 3 points, we have a " + dt);
        Triangle.moreInfo = true;
        System.out.println("Triangles: " + dt.triGraph.nodeSet());
        
        PaintVector.createAndShowGUIDrawDelauary(dt.triGraph.nodeSet());
        
    }
    
}
