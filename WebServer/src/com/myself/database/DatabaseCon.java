package com.myself.database;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;
public class DatabaseCon extends JdbcConnector
{
    // 定义全局变量
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null; 
    static DatabaseCon databaseCon;
    
    public static DatabaseCon getInstance()
    {
		if (databaseCon == null)
		{
			databaseCon = new DatabaseCon();
		}
		return databaseCon;
	}       
    
    public void start() 
    {
        // 获得数据库连接
        try {
			conn = (Connection) this.getConn();
			if (conn != null) {
				
				System.out.println("connected db successful");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}    
    
    public String queryKey(String strName) 
    {
       // 创建SQL语句
       String sql = "select * from data where keyword=?"; 

       // 通过Connection对象创建PrepareStatement对象
       try {
    	   pstmt = (PreparedStatement) conn.prepareStatement(sql);
			       // 设置SQL语句的参数
	       pstmt.setString(1, strName);
	       // 执行查询，将查询结果赋给ResultSet对象
	       rs = (ResultSet) pstmt.executeQuery();
	       // 遍历指针
	       if (rs.next())
	       {
	    	   return rs.getString("value");
	       }
	       else 
	       {
	    	   System.out.println("database assert");
	       }		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       return null;
    }
    
    public boolean setKey(String key, String value)
    {
    	// 创建SQL语句
        String sql = "update data set value=? where keyword=?"; 
        try {
        	// 通过Connection对象创建PrepareStatement对象
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
		       // 设置SQL语句的参数
		    pstmt.setString(1, value);
		    pstmt.setString(2, key);
		     // 执行查询，将查询结果赋给ResultSet对象
		    if (pstmt.executeUpdate()>0) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
	}
}