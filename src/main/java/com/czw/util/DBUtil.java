package com.czw.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtil {
	
	private static Properties props;
	
	static {
		try {
			InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("application.properties");
			props = new Properties();
			props.load(in);
			in.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConn() throws Exception{
		String url = "jdbc:mysql://localhost:3306/seckill?serverTimezone=UTC";
		String username = "root";
		String password = "root";
		String driver = "com.alibaba.druid.pool.DruidDataSource";
		Class.forName(driver);
		return DriverManager.getConnection(url,username, password);
	}
}
