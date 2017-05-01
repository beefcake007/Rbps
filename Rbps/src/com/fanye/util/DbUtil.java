package com.fanye.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbUtil {

	private String dbUrl="jdbc:mysql://localhost:3306/db_rbps";
	private String dbUserName="root";
	private String dbPassWord="123456";
	private String jdbcName="com.mysql.jdbc.Driver";
	
	public Connection getCon()throws Exception{
		Class.forName(jdbcName);
		Connection con=DriverManager.getConnection(dbUrl,dbUserName,dbPassWord);
		return con;
	}
	
	public void closeCon(Connection con)throws Exception{
		
		if(con!=null){
			con.close();
		}
		
	}
	
	public static void main(String[] args) {
		DbUtil dbUtil=new DbUtil();
		try{
			dbUtil.getCon();
			System.out.println("数据库连接成功！");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("数据库连接失败！");
		}
	}
}
