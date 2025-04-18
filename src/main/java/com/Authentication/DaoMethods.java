package com.Authentication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.FileManipulationDao.DataBaseConnection;

public class DaoMethods {
	Connection  connection=DataBaseConnection.getConnection();

	public int getUserId(String userName) {
		int userId=0;
		try {
	              String queryForGetUserId="Select USER_ID From UserInfo WHERE USER_NAME=?";
	              PreparedStatement preparedStatementForGetUserId=connection.prepareStatement(queryForGetUserId);
	              preparedStatementForGetUserId.setString(1,userName);
	              ResultSet resultSetForGetUserId=preparedStatementForGetUserId.executeQuery();
                  resultSetForGetUserId.next();
                  userId=resultSetForGetUserId.getInt(1);
	        }catch (SQLException e) {
		         // TODO Auto-generated catch block
	           	 e.printStackTrace();
		         System.out.println("connection not found");
	         }
		return userId;
	}
	
	public static String getFileNameFromSql(int fileID) {
		String fileName="";
		try { 
			    Connection connection=DataBaseConnection.getConnection();
			    String getFileName="Select FILE_NAME From StoredFilesDetails WHERE FILE_ID=?";
			    PreparedStatement preparedStatementForGetFileName=connection.prepareStatement(getFileName);
			    preparedStatementForGetFileName.setInt(1, fileID);
				ResultSet resultSetForGetFileID=preparedStatementForGetFileName.executeQuery();
				resultSetForGetFileID.next();
				fileName=resultSetForGetFileID.getString(1);
	            
	        }
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("connection not found");
		  }
		return fileName;
	}
	
	public long getUserStorage(int userId) {
		long userStorage=0;
		try {
	              String queryForGetUserStorage="Select SUM(FILE_STORAGE_IN_BYTES) From StoredFilesDetails WHERE USER_ID=?";
	              PreparedStatement preparedStatementForGetUserStorage=connection.prepareStatement(queryForGetUserStorage);
	              preparedStatementForGetUserStorage.setInt(1,userId);
	              ResultSet resultSetForGetUserStorage=preparedStatementForGetUserStorage.executeQuery();
                  resultSetForGetUserStorage.next();
                  userStorage=resultSetForGetUserStorage.getLong(1);
	        }catch (SQLException e) {
		         // TODO Auto-generated catch block
	           	 e.printStackTrace();
		         System.out.println("connection not found");
	         }
		return userStorage;
	}
	
	public long getOverAllFileSize(int userId) {
		long overAllFileSize=0;
		try {		
				String getAllFileId="Select FILE_ID From StoredFilesDetails WHERE USER_ID=?";
			    PreparedStatement preparedStatementForGetAllFileId=connection.prepareStatement(getAllFileId);
			    preparedStatementForGetAllFileId.setInt(1, userId);
				ResultSet resultSetForGetAllFileId=preparedStatementForGetAllFileId.executeQuery();
				float topVersion;
				long fileSize;
				while(resultSetForGetAllFileId.next()) {
					String getTopVersion="Select MAX(VERSION) From FileVersionsDetails WHERE FILE_ID=?";
				    PreparedStatement preparedStatementForGetTopVersion=connection.prepareStatement(getTopVersion);
				    preparedStatementForGetTopVersion.setInt(1,resultSetForGetAllFileId.getInt(1) );
					ResultSet resultSetForGetTopVersion=preparedStatementForGetTopVersion.executeQuery();
					resultSetForGetTopVersion.next();
					topVersion=resultSetForGetTopVersion.getFloat(1);
					
					String getFileSize="Select FILE_SIZE_IN_BYTES From FileVersionsDetails WHERE FILE_ID=? AND VERSION=?";
				    PreparedStatement preparedStatementForGetFileSize=connection.prepareStatement(getFileSize);
				    preparedStatementForGetFileSize.setInt(1,resultSetForGetAllFileId.getInt(1) );
				    preparedStatementForGetFileSize.setFloat(2,topVersion );
					ResultSet resultSetForGetFileSize=preparedStatementForGetFileSize.executeQuery();
					resultSetForGetFileSize.next();
					fileSize=resultSetForGetFileSize.getLong(1);
					overAllFileSize+=fileSize;
				}
			
	        }
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("connection not found");
		  }
		return overAllFileSize;
	}
	
}
