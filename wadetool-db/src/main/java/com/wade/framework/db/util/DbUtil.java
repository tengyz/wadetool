package com.wade.framework.db.util;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wade.framework.data.IDataList;
import com.wade.framework.data.IDataMap;
import com.wade.framework.data.impl.DataArrayList;
import com.wade.framework.data.impl.DataHashMap;

public class DbUtil {
    private static final Logger log = LogManager.getLogger(DbUtil.class);
    
    public static IDataList queryList(String sql) {
        Db db = new Db();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = db.getConnection();
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();
            return resultSetToList(rs);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("jdbc DbUtil queryList异常", e);
            return null;
        }
        finally {
            colseResource(conn, pstm, rs);
        }
    }
    
    public static IDataList queryList(String sql, String dataSrc) {
        Db db = new Db(dataSrc);
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = db.getConnection();
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();
            return resultSetToList(rs);
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("sql=：" + sql + "jdbc DbUtil queryList异常", e);
            return null;
        }
        finally {
            colseResource(conn, pstm, rs);
        }
    }
    
    public static IDataList resultSetToList(ResultSet rs) throws java.sql.SQLException {
        if (rs == null) {
            return new DataArrayList();
        }
        //得到结果集(rs)的结构信息，比如字段数、字段名等
        ResultSetMetaData md = (ResultSetMetaData)rs.getMetaData();
        //返回此 ResultSet 对象中的列数   
        int columnCount = md.getColumnCount();
        IDataList outList = new DataArrayList();
        while (rs.next()) {
            IDataMap rowData = new DataHashMap();
            for (int i = 1; i <= columnCount; i++) {
                rowData.put((md.getColumnName(i)).toUpperCase(), rs.getObject(i));
            }
            outList.add(rowData);
        }
        return outList;
    }
    
    /**
     * 释放资源
     * @param conn
     * @param st
     * @param rs
     */
    public static void colseResource(Connection conn, PreparedStatement st, ResultSet rs) {
        closeResultSet(rs);
        closeStatement(st);
        closeConnection(conn);
    }
    
    /**
     * 释放连接 Connection
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if (null != conn) {
            try {
                conn.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
                log.error("缓存queryList closeConnection关闭异常", e);
            }
        }
        //等待垃圾回收
        conn = null;
    }
    
    /**
     * 释放语句执行者 Statement
     * @param st
     */
    public static void closeStatement(PreparedStatement st) {
        if (null != st) {
            try {
                st.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
                log.error("缓存queryList closeStatement关闭异常", e);
            }
        }
        //等待垃圾回收
        st = null;
    }
    
    /**
     * 释放结果集 ResultSet
     * @param rs
     */
    public static void closeResultSet(ResultSet rs) {
        if (null != rs) {
            try {
                rs.close();
            }
            catch (SQLException e) {
                e.printStackTrace();
                log.error("缓存queryList closeResultSet关闭异常", e);
            }
        }
        //等待垃圾回收
        rs = null;
    }
    
    public static void main(String[] args) throws ParseException {
        IDataList getList = DbUtil.queryList("select date_format(now(),'%Y-%c-%d %H:%i:%s') as nowtimes from dual");
        IDataMap getData = getList.first();
        String sysdate = getData.getString("NOWTIMES");
        log.info("直接jdbc获取数据库时间=:" + sysdate);
        System.out.println("直接jdbc获取数据库时间=:" + sysdate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long offset = System.currentTimeMillis() - format.parse(sysdate).getTime();
        System.out.println("结果=：" + Long.valueOf(offset));
    }
    
}
