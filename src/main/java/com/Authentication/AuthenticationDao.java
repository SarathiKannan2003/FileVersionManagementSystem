package com.Authentication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.FileManipulationDao.DataBaseConnection;

public class AuthenticationDao {
	Connection  connection;
	AuthenticationDao(){
		connection=DataBaseConnection.getConnection();	
	}
	public boolean[] SignInCheck(String name,String email,String password) {
		boolean errorArray[]=new boolean[3];
		try {
			
			String selectQuery="SELECT * FROM UserInfo";
			Statement statement=connection.createStatement();
			ResultSet resultSet=statement.executeQuery(selectQuery);
			boolean nameError=true;
			boolean emailError=true;
			boolean passwordError=true;
			while(resultSet.next()) {
				if(resultSet.getString(2).equals(name)) {
					nameError=false;
					if(resultSet.getString(3).equals(email)) {
						emailError=false;
						String hashingPassword=AuthenticationMethods.hashPassword(password);
						if(hashingPassword.equals(resultSet.getString(6))) {
							passwordError=false;
				}
			}
			}
			}
			errorArray[0]=nameError;
			errorArray[1]=emailError;
			errorArray[2]=passwordError;
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connection not found");
		}
		return errorArray;
	}
	
	
	public String SignUpCheck(String name,String email,String mobileNumber,String timeZone,String password) {
		try {
			String selectQuery="SELECT * FROM UserInfo";
			Statement statement=connection.createStatement();
			ResultSet resultSet=statement.executeQuery(selectQuery);
			while(resultSet.next()) {
				if(resultSet.getString(2).equals(name)) return "userName";
			    if(resultSet.getString(3).equals(email)) return "email";
			}
			String hashingPassword=AuthenticationMethods.hashPassword(password);
			String insertValueIntoTableQuery="INSERT INTO UserInfo(USER_NAME,EMAIL,MOBILE_NUMBER,TIME_ZONE,USER_PASSWORD) VALUES(?,?,?,?,?);";
			PreparedStatement preparedStatement=connection.prepareStatement(insertValueIntoTableQuery);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, mobileNumber);
			preparedStatement.setString(4, timeZone);
			preparedStatement.setString(5, hashingPassword);
			preparedStatement.executeUpdate();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connection not found");
		}
		return "updated";
	}
}
