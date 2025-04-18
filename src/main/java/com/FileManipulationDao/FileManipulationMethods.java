package com.FileManipulationDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileManipulationMethods {
	
	public static String convertFileSizeToHumanReadableFormat(long fileSize) {
		 String[] units = {"B", "KB", "MB", "GB", "TB"};
	        int index = 0;
	        double sizeValue = fileSize;
	        while (sizeValue >= 1024 && index < units.length - 1) {
	            sizeValue /= 1024;
	            index++;
	        }
	        return String.format("%.2f %s", sizeValue, units[index]);
	}
	
	
	public static boolean isFileLocked(int fileId,float version) {
		try {
			    Connection  connection=DataBaseConnection.getConnection();
				String getLockDetails="Select FILE_LOCK_STATUS From FileVersionsDetails WHERE FILE_ID=? AND VERSION=?" ;
			    PreparedStatement preparedStatementForGetLockDetails=connection.prepareStatement(getLockDetails);
			    preparedStatementForGetLockDetails.setInt(1, fileId);
			    preparedStatementForGetLockDetails.setFloat(2, version);
				ResultSet resultSetForGetLockDetails=preparedStatementForGetLockDetails.executeQuery();
				resultSetForGetLockDetails.next();
				if(resultSetForGetLockDetails.getInt(1)==1) return true;
	        }
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("connection not found");
		  }
		return false;
		}
	
	
	
	public static long getFileSize(int fileId,float topVersion) {
		long fileSize=0;
	    try {
		    Connection  connection=DataBaseConnection.getConnection();
			String getFileSize="Select FILE_SIZE_IN_BYTES From FileVersionsDetails WHERE FILE_ID=? AND VERSION=?";
		    PreparedStatement preparedStatementForGetFileSize=connection.prepareStatement(getFileSize);
		    preparedStatementForGetFileSize.setInt(1, fileId);
		    preparedStatementForGetFileSize.setFloat(2, topVersion);
			ResultSet resultSetForGetFileSize=preparedStatementForGetFileSize.executeQuery();
			resultSetForGetFileSize.next();
			fileSize=resultSetForGetFileSize.getLong(1);
			return fileSize;
        }
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connection not found");
	  }
	return fileSize;
	}
	
	
	
	public static String getTimeZone(int userId) {
		try {
			Connection  connection=DataBaseConnection.getConnection();
			String selectQuery="SELECT TIME_ZONE FROM UserInfo WHERE USER_ID=?";
			PreparedStatement preparedStatement=connection.prepareStatement(selectQuery);
			preparedStatement.setInt(1, userId);
			ResultSet resultSet=preparedStatement.executeQuery();
			resultSet.next();
			String timeZone=resultSet.getString(1);
			connection.close();
			return timeZone;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connection not found");
		}	
		return "";
	}
	
	
	
	
	public static void lockTheFile(int fileId,float version) {
		try {
			    Connection  connection=DataBaseConnection.getConnection();		
				String deleteVersion="UPDATE FileVersionsDetails SET FILE_LOCK_STATUS=1 WHERE FILE_ID=? AND VERSION=?";
			    PreparedStatement preparedStatementForDeleteVersion=connection.prepareStatement(deleteVersion);
			    preparedStatementForDeleteVersion.setInt(1, fileId);
			    preparedStatementForDeleteVersion.setFloat(2, version);
				preparedStatementForDeleteVersion.executeUpdate();
	            
	        }
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("connection not found");
		  }
		}
	
	
	
	
	public static float getTopVersion(int fileId) {
		float topVersion=0.0f;
		try {
			Connection  connection=DataBaseConnection.getConnection();
		    
		    String getTopVersion="Select MAX(VERSION) From FileVersionsDetails WHERE FILE_ID=?";
			PreparedStatement preparedStatementForGetTopVersion=connection.prepareStatement(getTopVersion);
			preparedStatementForGetTopVersion.setInt(1, fileId);
			ResultSet resultSetForGetTopVersion=preparedStatementForGetTopVersion.executeQuery();
		    resultSetForGetTopVersion.next();
		    topVersion=resultSetForGetTopVersion.getFloat(1);
		    return topVersion;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connection not found");
		}return topVersion;
	}
	
}
