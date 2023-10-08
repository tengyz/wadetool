package com.wade.framework.common.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

/**
 * 树转换工具类
 * @Description 树转换工具类
 * @ClassName   TreeJson 
 * easui中的tree_data.json数据,只能有一个root节点
 * [{   
    "id":1,   
    "text":"Folder1",   
    "iconCls":"icon-save",   
    "children":[{   
        "text":"File1",   
        "checked":true  
    }]   
}] 
 * 提供静态方法formatTree(List<TreeJson> list) 返回结果
 * TreeJson.formatTree(treeJsonlist) ;
 * @author tengyz
 *
 */
public class TreeJson implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPid() {
        return pid;
    }
    
    public void setPid(String pid) {
        this.pid = pid;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getIconCls() {
        return iconCls;
    }
    
    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getChecked() {
        return checked;
    }
    
    public void setChecked(String checked) {
        this.checked = checked;
    }
    
    public JSONObject getAttributes() {
        return attributes;
    }
    
    public void setAttributes(JSONObject attributes) {
        this.attributes = attributes;
    }
    
    public List<TreeJson> getChildren() {
        return children;
    }
    
    public void setChildren(List<TreeJson> children) {
        this.children = children;
    }
    
    private String id;
    
    private String pid;
    
    private String text;
    
    private String iconCls;
    
    private String state;
    
    private String checked;
    
    private JSONObject attributes = new JSONObject();
    
    private List<TreeJson> children = new ArrayList<TreeJson>();
    
    /**
     * 获得easyUI的树data
     * @param List<Map<String, Object>> listData (必须参数：ID,TEXT,PID，可选CHECKED：true,false，STATE：open，closed)
     * @param rootId 跟节点parent_id
     * @return
     * id：节点的 id，它对于加载远程数据很重要。
     * text：要显示的节点文本。
     * state：节点状态，'open' 或 'closed'，默认是 'open'。当设置为 'closed' 时，该节点有子节点，并且将从远程站点加载它们。
     * checked：指示节点是否被选中。
     * attributes：给一个节点添加的自定义属性。
     * children：定义了一些子节点的节点数组。
     */
    public static List<TreeJson> formatTree(List<Map<String, Object>> listData, String rootId) {
        TreeJson root = new TreeJson();
        TreeJson node = new TreeJson();
        // 拼凑好的json格式的数据
        List<TreeJson> treelist = new ArrayList<TreeJson>();
        // parentnodes存放所有的父节点
        List<TreeJson> parentnodes = new ArrayList<TreeJson>();
        boolean flag = false;
        List<TreeJson> list = formatTreeJson(listData);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPid().equals(rootId)) {
                if (flag) {
                    treelist.add(root);
                }
                root = list.get(i);
                flag = true;
            }
            else {
                node = list.get(i);
                if (node.getPid().equals(root.getId())) {
                    //为tree root 增加子节点
                    parentnodes.add(node);
                    root.getChildren().add(node);
                }
                else {//获取root子节点的孩子节点
                    getChildrenNodes(parentnodes, node);
                    parentnodes.add(node);
                }
            }
        }
        treelist.add(root);
        return treelist;
    }
    
    public static List<TreeJson> formatTreeForRole(List<Map<String, Object>> listData, String rootId) {
        TreeJson root = new TreeJson();
        TreeJson node = new TreeJson();
        List<TreeJson> treelist = new ArrayList<TreeJson>();// 拼凑好的json格式的数据
        List<TreeJson> parentnodes = new ArrayList<TreeJson>();// parentnodes存放所有的父节点
        boolean flag = false;
        List<TreeJson> list = formatTreeJson(listData);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPid().equals(rootId)) {
                if (flag) {
                    treelist.add(root);
                }
                root = list.get(i);
                flag = true;
            }
            else {
                node = list.get(i);
                if (node.getPid().equals(root.getId())) {
                    //为tree root 增加子节点
                    parentnodes.add(node);
                    root.getChildren().add(node);
                }
                else {//获取root子节点的孩子节点
                    getChildrenNodesForRole(parentnodes, node);
                    parentnodes.add(node);
                }
            }
        }
        treelist.add(root);
        return treelist;
    }
    
    /** 
     * @Description 组装树 
     * @param @param listData
     * @param @return 参数 
     * @return List<TreeJson> 返回类型  
     * @throws 
     */
    
    private static List<TreeJson> formatTreeJson(List<Map<String, Object>> listData) {
        List<TreeJson> list = new ArrayList<TreeJson>();
        for (int i = 0; i < listData.size(); i++) {
            TreeJson tj = new TreeJson();
            tj.setId(listData.get(i).get("id").toString());
            tj.setText(listData.get(i).get("text").toString());
            tj.setPid(listData.get(i).get("pid").toString());
            listData.get(i).remove("id");
            listData.get(i).remove("text");
            listData.get(i).remove("pid");
            //设置是否选中
            if (!"".equals(listData.get(i).get("checked")) && null != listData.get(i).get("checked")) {
                tj.setChecked(listData.get(i).get("checked").toString());
                listData.get(i).remove("checked");
            }
            //设置打开或者关闭
            if (!"".equals(listData.get(i).get("state")) && null != listData.get(i).get("state")) {
                tj.setState(listData.get(i).get("state").toString());
                listData.get(i).remove("state");
            }
            JSONObject jb = JSONObject.fromObject(listData.get(i));
            tj.setAttributes(jb);
            list.add(tj);
        }
        return list;
    }
    
    private static void getChildrenNodes(List<TreeJson> parentnodes, TreeJson node) {
        //循环遍历所有父节点和node进行匹配，确定父子关系
        for (int i = parentnodes.size() - 1; i >= 0; i--) {
            
            TreeJson pnode = parentnodes.get(i);
            //如果是父子关系，为父节点增加子节点，退出for循环
            if (pnode.getId().equals(node.getPid())) {
                pnode.setState("closed");//关闭二级树
                pnode.getChildren().add(node);
                return;
            }
            else {
                //如果不是父子关系，删除父节点栈里当前的节点，
                //继续此次循环，直到确定父子关系或不存在退出for循环
                parentnodes.remove(i);
            }
        }
    }
    
    private static void getChildrenNodesForRole(List<TreeJson> parentnodes, TreeJson node) {
        //循环遍历所有父节点和node进行匹配，确定父子关系
        for (int i = parentnodes.size() - 1; i >= 0; i--) {
            TreeJson pnode = parentnodes.get(i);
            //如果是父子关系，为父节点增加子节点，退出for循环
            if (pnode.getId().equals(node.getPid())) {
                pnode.setState("closed");//关闭二级树
                pnode.getChildren().add(node);
                return;
            }
        }
    }
    
    public static List<TreeJson> formartTreeAll(List<Map<String, Object>> listData, String rootId) {
        List<TreeJson> list = formatTreeJson(listData);
        List<TreeJson> treelist = new ArrayList<TreeJson>();// 拼凑好的json格式的数据
        Map<String, TreeJson> treeMap = new HashMap<String, TreeJson>();
        //1、把跟节点放入到treeMap中
        for (TreeJson node : list) {
            if (node.getPid().equals(rootId)) {
                treeMap.put(node.getId(), node);
            }
        }
        //2、把其他节点挂在跟下
        //已经挂在跟下的节点
        List<TreeJson> secondLevelAlready = new ArrayList<TreeJson>();
        //没有挂在跟下的节点
        List<TreeJson> secondLevelNotAlready = new ArrayList<TreeJson>();
        for (TreeJson node : list) {
            if (node.getPid().equals(rootId)) {
                continue;
            }
            if (treeMap.containsKey(node.getPid())) {
                treeMap.get(node.getPid()).getChildren().add(node);
                secondLevelAlready.add(node);
            }
            else {
                secondLevelNotAlready.add(node);
            }
        }
        recursion(secondLevelAlready, secondLevelNotAlready);
        //3、只取根节点数据
        for (Entry<String, TreeJson> entry : treeMap.entrySet()) {
            if (entry.getValue().getPid().equals(rootId)) {
                treelist.add(entry.getValue());
            }
        }
        return treelist;
    }
    
    //迭代调用
    private static void recursion(List<TreeJson> already, List<TreeJson> notAlready) {
        //如果没有节点可挂则证明所有的节点都已经形成了树结构
        if (notAlready.size() == 0) {
            return;
        }
        //在没有被挂的节点里面寻找已经挂了的节点的子节点
        Map<String, TreeJson> treeMap = new HashMap<String, TreeJson>();
        for (TreeJson node : already) {
            treeMap.put(node.getId(), node);
        }
        //已经挂了的节点
        List<TreeJson> nodeAlready = new ArrayList<TreeJson>();
        //没有挂的节点
        List<TreeJson> nodeNotAlready = new ArrayList<TreeJson>();
        for (TreeJson node : notAlready) {
            if (treeMap.containsKey(node.getPid())) {
                treeMap.get(node.getPid()).getChildren().add(node);
                nodeAlready.add(node);
            }
            else {
                nodeNotAlready.add(node);
            }
        }
        recursion(nodeAlready, nodeNotAlready);
    }
}
