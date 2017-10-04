package com.wade.framework.data;

/** 
 * @Description TODO 
 * @ClassName   EasyuiCombox 
 * @Date        2016年2月15日 上午10:29:56 
 * @Author      tengyz 
 */

public class EasyuiCombobox implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    
    private String text;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
}
