package com.myself.database;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;
public class DatabaseCon extends JdbcConnector
{
    // ����ȫ�ֱ���
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
        // ������ݿ�����
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
       // ����SQL���
       String sql = "select * from data where keyword=?"; 

       // ͨ��Connection���󴴽�PrepareStatement����
       try {
    	   pstmt = (PreparedStatement) conn.prepareStatement(sql);
			       // ����SQL���Ĳ���
	       pstmt.setString(1, strName);
	       // ִ�в�ѯ������ѯ�������ResultSet����
	       rs = (ResultSet) pstmt.executeQuery();
	       // ����ָ��
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
    	// ����SQL���
        String sql = "update data set value=? where keyword=?"; 
        try {
        	// ͨ��Connection���󴴽�PrepareStatement����
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
		       // ����SQL���Ĳ���
		    pstmt.setString(1, value);
		    pstmt.setString(2, key);
		     // ִ�в�ѯ������ѯ�������ResultSet����
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