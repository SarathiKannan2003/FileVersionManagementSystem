package com.FileManipulationDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileDeleteDao {
	public  void deleteFile(int fileId,float version) {
		try {
			Connection  connection=DataBaseConnection.getConnection();
			String getFileSize="Select FILE_SIZE_IN_BYTES from FileVersionsDetails where FILE_ID=? AND VERSION=?";
		    PreparedStatement preparedStatementForGetFileSize=connection.prepareStatement(getFileSize);
		    preparedStatementForGetFileSize.setInt(1, fileId);
		    preparedStatementForGetFileSize.setFloat(2, version);
			ResultSet resultSetForGetFileSize=preparedStatementForGetFileSize.executeQuery();
			resultSetForGetFileSize.next();
			long fileSize=resultSetForGetFileSize.getLong(1);
			
			String getFileStorage="Select FILE_STORAGE_IN_BYTES from StoredFilesDetails where FILE_ID=?";
		    PreparedStatement preparedStatementForGetFileStorage=connection.prepareStatement(getFileStorage);
		    preparedStatementForGetFileStorage.setInt(1, fileId);
			ResultSet resultSetForGetFileStorage=preparedStatementForGetFileStorage.executeQuery();
			resultSetForGetFileStorage.next();
			long fileStorage=resultSetForGetFileStorage.getLong(1);
		
			String deleteVersion="Delete from FileVersionsDetails where FILE_ID=? AND VERSION=?";
		    PreparedStatement preparedStatementForDeleteVersion=connection.prepareStatement(deleteVersion);
		    preparedStatementForDeleteVersion.setInt(1, fileId);
		    preparedStatementForDeleteVersion.setFloat(2, version);
			preparedStatementForDeleteVersion.executeUpdate();
			
			String updateFileStorage="Update StoredFilesDetails SET FILE_STORAGE_IN_BYTES=? WHERE FILE_ID=?";
		    PreparedStatement preparedStatementForUpdateFileStorage=connection.prepareStatement(updateFileStorage);
		    preparedStatementForUpdateFileStorage.setLong(1, fileStorage-fileSize);
		    preparedStatementForUpdateFileStorage.setInt(2, fileId);
			preparedStatementForUpdateFileStorage.executeUpdate();
			
        }
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connection not found");
	  }
   }
}