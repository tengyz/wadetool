package com.wade.framework.data;

/**
 * easyui的datagrid向后台传递参数使用的model
 * 
 * @author tengyz
 * 
 */
public class EasyuiDataGrid implements java.io.Serializable {
    
    /**
     */
    private static final long serialVersionUID = 1L;
    
    private int page = 1;// 当前页
    
    private int rows = 10;// 每页显示记录数
    
    private String sort = null;// 排序字段名
    
    private String order = "asc";// 按什么排序(asc,desc)
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getRows() {
        return rows;
    }
    
    public void setRows(int rows) {
        this.rows = rows;
    }
    
    public String getSort() {
        return sort;
    }
    
    public void setSort(String sort) {
        this.sort = sort;
    }
    
    public String getOrder() {
        return order;
    }
    
    public void setOrder(String order) {
        this.order = order;
    }
    
    public int getPageStart() {
        return (page - 1) * rows + 1;
    }
    
    public int getPageEnd() {
        return page * rows;
    }
    
}
