package com.wade.framework.common.cache.param.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.exceptions.BizExceptionEnum;
import com.wade.framework.exceptions.Thrower;

public class ReadOnlyDataset implements IDataList {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5371318769538071668L;
    
    private IDataList ds = null;
    
    protected ReadOnlyDataset() {
        this(new DataArrayList(), false);
    }
    
    public ReadOnlyDataset(IDataList ds) {
        this(ds, true);
    }
    
    public ReadOnlyDataset(IDataList srcDs, boolean needPack) {
        if (srcDs == null)
            srcDs = new DataArrayList();
        
        if (!needPack) {
            this.ds = srcDs;
            return;
        }
        this.ds = new DataArrayList();
        if (this.ds == null)
            return;
        
        for (int i = 0, size = srcDs.size(); i < size; i++) {
            IDataMap data = srcDs.getData(i);
            if (data instanceof ReadOnlyData) {
                this.ds.add(data);
            }
            else
                this.ds.add(new ReadOnlyData(data));
        }
    }
    
    @Override
    public IDataMap first() {
        return ds.first();
    }
    
    @Override
    public Object get(int index) {
        return ds.get(index);
    }
    
    @Override
    public Object get(int index, String key) {
        return ds.get(index, key);
    }
    
    @Override
    public Object get(int index, String key, Object defVal) {
        return ds.get(index, key, defVal);
    }
    
    @Override
    public IDataMap getData(int index) {
        return ds.getData(index);
    }
    
    @Override
    public IDataList getDataset(int index) {
        return ds.getDataset(index);
    }
    
    @Override
    public String[] getNames() {
        return ds.getNames();
    }
    
    @Override
    public IDataMap toData() {
        return ds.toData();
    }
    
    @Override
    public boolean add(Object val) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "add");
        
        return false;
    }
    
    @Override
    public void add(int index, Object val) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "add");
    }
    
    @Override
    public boolean addAll(Collection<? extends Object> collection) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "addAll");
        return false;
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends Object> collection) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "addAll");
        return false;
    }
    
    @Override
    public void clear() {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "clear");
    }
    
    @Override
    public boolean contains(Object obj) {
        return ds.contains(obj);
    }
    
    @Override
    public boolean containsAll(Collection<?> collection) {
        return ds.containsAll(collection);
    }
    
    @Override
    public int indexOf(Object obj) {
        return ds.indexOf(obj);
    }
    
    @Override
    public boolean isEmpty() {
        return ds.isEmpty();
    }
    
    @Override
    public Iterator<Object> iterator() {
        return ds.iterator();
    }
    
    @Override
    public int lastIndexOf(Object obj) {
        return ds.lastIndexOf(obj);
    }
    
    @Override
    public ListIterator<Object> listIterator() {
        return ds.listIterator();
    }
    
    @Override
    public ListIterator<Object> listIterator(int index) {
        return ds.listIterator(index);
    }
    
    @Override
    public boolean remove(Object arg0) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "remove");
        return false;
    }
    
    @Override
    public Object remove(int index) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "remove");
        return null;
    }
    
    @Override
    public boolean removeAll(Collection<?> collection) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "removeAll");
        return false;
    }
    
    @Override
    public boolean retainAll(Collection<?> collection) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "retainAll");
        return false;
    }
    
    @Override
    public Object set(int index, Object obj) {
        Thrower.throwException(BizExceptionEnum.ERROR_MSG, this.getClass(), "set");
        return null;
    }
    
    @Override
    public int size() {
        return ds.size();
    }
    
    @Override
    public List<Object> subList(int startIndex, int endIndex) {
        return ds.subList(startIndex, endIndex);
    }
    
    @Override
    public Object[] toArray() {
        return ds.toArray();
    }
    
    @Override
    public <T> T[] toArray(T[] arr) {
        return ds.toArray(arr);
    }
    
    @Override
    public String toString() {
        return ds.toString();
    }
    
}
