package com.meetup.memcaheded.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class JdbcConnector 
{
  // �������ݿ����ӳ���
    private final static String DRIVER = "com.mysql.jdbc.Driver";
    private final static String URL = "jdbc:mysql://127.0.0.1:3306/user";
    private final static String DBNAME = "root";
    private final static String DBPASS = "sdp123";
    
    /**
     * �õ����ݿ�����
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConn()throws ClassNotFoundException,SQLException 
    {
       // ��������
       Class.forName(DRIVER);
       // ͨ��DriverManager����õ�����
       Connection conn = DriverManager.getConnection(URL,DBNAME,DBPASS);
       // �������ݿ�����
       return conn;
    }
}
