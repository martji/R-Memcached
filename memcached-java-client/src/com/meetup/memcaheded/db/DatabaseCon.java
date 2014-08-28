package com.meetup.memcaheded.db;

import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSet;

public class DatabaseCon extends JdbcConnector {
	// ����ȫ�ֱ���
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	static DatabaseCon databaseCon;
	private String sqlget = "select * from data where keyword=?";
	private String sqlset = "replace into data(keyword, value) values(?, ?)";

	public static DatabaseCon getInstance() {
		if (databaseCon == null) {
			databaseCon = new DatabaseCon();
		}
		return databaseCon;
	}

	public void start() {
		// ������ݿ�����
		try {
			conn = (Connection) this.getConn();
			sqlget = JdbcConnector.SQLGET;
			sqlset = JdbcConnector.SQLSET;
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

	public String queryKey(String strName) {
		// ����SQL���
		String sql = sqlget;

		// ͨ��Connection���󴴽�PrepareStatement����
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			// ����SQL���Ĳ���
			pstmt.setString(1, strName);
			// ִ�в�ѯ������ѯ�������ResultSet����
			rs = (ResultSet) pstmt.executeQuery();
			// ����ָ��
			if (rs.next()) {
				return rs.getString(2);
				//return rs.getString("value");
			} else {
				System.out.println("database assert, c_id=" + strName);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public boolean setKey(String key, String value) {
		// ����SQL���
		String sql = sqlset;
		try {
			// ͨ��Connection���󴴽�PrepareStatement����
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			// ����SQL���Ĳ���
			pstmt.setString(1, key);
			pstmt.setString(2, value);
			// ִ�в�ѯ������ѯ�������ResultSet����
			if (pstmt.executeUpdate() > 0) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}