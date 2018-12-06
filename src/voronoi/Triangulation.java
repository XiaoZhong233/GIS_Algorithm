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
    private Graph<Triangle> triGraph;        // Holds triangles for navigation 三角形图,记录三角形的连接关系
    private Triangle initTriangle;

    
    /**
     * 获取初始三角形，也就是超级三角形
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
     * 返回三角形上不包含某点的邻近三角形
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
     * 返回邻近三角形
     * Return the set of triangles adjacent to triangle.
     * @param triangle the triangle to check
     * @return the neighbors of triangle
     */
    public Set<Triangle> neighbors(Triangle triangle) {
        return triGraph.neighbors(triangle);
    }
    
    /**
     * 以一定顺序（顺时针或逆时针）返回某三角形的邻近三角形
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
     * 根据点来寻找三角形
     * 第一种尝试遍历邻近三角形最多的三角形的周围的三角形，如果找到了就好
     * 否则，尝试暴力搜索
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
            //返回null，则点在这个三角形内
            if (corner == null) return triangle;
            //否则直接搜寻下一个三角形
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
     * 核心方法
     * 确定新增点影响的三角形
     * 返回新增点周边需要消失的三角形
     * 基本思想：遍历包含指定点的三角形，判断点与三角形外接圆的关系。
     * 点在三角形外接圆内：则该三角形不为delaunay三角形，需移除。
     * 点在三角形外接圆外：检查邻近三角形与点的关系。
     * Determine the cavity caused by site.
     * @param site the site causing the cavity
     * @param triangle the triangle containing site
     * @return set of all triangles that have site in their circumcircle
     */
    private Set<Triangle> getCavity (NPoint site, Triangle triangle) {
    	//记录需移除的三角形
        Set<Triangle> encroached = new HashSet<Triangle>();
        //记录待检查的周边三角形
        Queue<Triangle> toBeChecked = new LinkedList<Triangle>();
        //记录已检查的周边三角形
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
     * 核心方法
     * 移除塌陷三角形，新增新的Delaury三角形
     * Update the triangulation by removing the cavity triangles and then
     * filling the cavity with new triangles.
     * @param site the site that created the cavity 新增点
     * @param cavity the triangles with site in their circumcircle 塌陷三角形
     * @return one of the new triangles 新三角形
     */
    private Triangle update (NPoint site, Set<Triangle> cavity) {
    	//记录构成三角形的两个点的集合,从塌陷三角形区域的边界获得
        Set<Set<NPoint>> boundary = new HashSet<Set<NPoint>>();
        //记录更新后的三角形集合
        Set<Triangle> theTriangles = new HashSet<Triangle>();
        // Find boundary facets and adjacent triangles
        //获取塌陷三角形的边界，这些边界可用于构建新三角形
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
            //与边界的点对构成新三角形
            Triangle tri = new Triangle(vertices);
            triGraph.add(tri);
            newTriangles.add(tri);
        }

        // Update the graph links for each new triangle 
        // 邻近三角形与新增三角形建立图联系
        theTriangles.addAll(newTriangles);    // Adj triangle + new triangles
        for (Triangle triangle: newTriangles)
            for (Triangle other: theTriangles)
                if (triangle.isNeighbor(other))
                    triGraph.add(triangle, other);

        // Return one of the new triangles
        return newTriangles.iterator().next();
    }

    /**
     * 新增点并生成Delaunay三角形
     * 算法思想如下：
     * 以无向图表示三角形之间的邻近关系
     * ①确定塌陷的三角形区域（在新增点的影响下，原三角形变为非Delaunay三角形，确定是否为Delaunay三角形的原则是判断点是否在该三角形的外接圆外）。
     * ②获取以点对的形式表示塌陷区域的边界（遍历塌陷三角形的顶点，获取除该顶点的另外两点，如果集合中已经有这两点了，则去除该点对，这样做的目的是获得凸集边界）
     * ③去除塌陷三角形与周边三角形的图联系
     * ④根据新增点与塌陷边界构建新的三角形
     * ⑤建立新三角形与周边三角形的图联系
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
     * 单元测试
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
