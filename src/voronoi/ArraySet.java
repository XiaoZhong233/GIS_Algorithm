package voronoi;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * 自定义顺序表，用于维护三角形的三个点
 * @author Administrator
 *
 */
public class ArraySet<E> extends AbstractSet<E> {

	
	private ArrayList<E> items;
	
	public ArraySet() {
		// TODO Auto-generated constructor stub
		this(3);
	}
	
    public ArraySet (int initialCapacity) {
        items  = new ArrayList<E>(initialCapacity);
    }
    
    public ArraySet (Collection<? extends E> collection) {
        items = new ArrayList<E>(collection.size());
        for (E item: collection)
            if (!items.contains(item)) items.add(item);
    }
    
    public E get (int index) throws IndexOutOfBoundsException {
        return items.get(index);
    }
    
    public boolean containsAny (Collection<?> collection) {
        for (Object item: collection)
            if (this.contains(item)) return true;
        return false;
    }
    
    
    /**
     * 不支持重复加入某个元素
     * @param item
     * @return
     */
    @Override
    public boolean add(E item) {
        if (items.contains(item)) return false;
        return items.add(item);
    }
    
    
	
	@Override
	public Iterator<E> iterator() {
		// TODO Auto-generated method stub
		 return items.iterator();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return items.size();
	}

	
}
