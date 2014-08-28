package com.meetup.memcaheded.db;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
public class JdbcConnector 
{
  // 定义数据库连接常量
    private final static String DRIVER = "com.mysql.jdbc.Driver";
    private static String URL = "jdbc:mysql://192.168.3.218:3306/tpcw";
    private static String DBNAME = "root";
    private static String DBPASS = "123";
    public static String SQLGET = "";
    public static String SQLSET = "";
    
    /**
     * 得到数据库连接
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public Connection getConn()throws ClassNotFoundException,SQLException 
    {
    	try {
    		File f = new File(System.getProperty("user.dir"));
    		String path = f.getPath() + File.separator;
	    	Properties properties = new Properties();
			properties.load(new FileInputStream(path + "dbcfg.properties"));
			URL = properties.getProperty("url").toString();
			DBNAME = properties.getProperty("username").toString();
			DBPASS = properties.getProperty("password").toString();
			SQLGET = properties.getProperty("sqlget").toString();
			SQLSET = properties.getProperty("sqlset").toString();
    	} catch (Exception e){
    		e.printStackTrace();
    	}
       // 加载驱动
       Class.forName(DRIVER);
       // 通过DriverManager对象得到连接
       Connection conn = DriverManager.getConnection(URL,DBNAME,DBPASS);
       // 返回数据库连接
       return conn;
    }
}
