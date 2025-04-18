package com.FileManipulationDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
	public static Connection getConnection() {
		Connection connection=null;
		try {
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/FileVersionManagementSystemDataBase","root","Sarathi@2003");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return connection;
		}catch (SQLException e) {
			e.printStackTrace();
			System.out.println("connection not found");
		}
		return connection;
	}
}
