package com.FileManipulationDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OverwriteDao {

	public void sqlUpdateAfterOverWriting(int existFileId,float existFileVersion,long createdTime,long newFileSize,String checkSum) {
		try {
	    Connection  connection=DataBaseConnection.getConnection();
		String getFileSize="SELECT FILE_SIZE_IN_BYTES FROM FileVersionsDetails WHERE FILE_ID=? AND VERSION=?";
		PreparedStatement preparedStatementForGetFileSize=connection.prepareStatement(getFileSize);
		preparedStatementForGetFileSize.setInt(1, existFileId);
		preparedStatementForGetFileSize.setFloat(2, existFileVersion);
		ResultSet resultSetForGetFileSize=preparedStatementForGetFileSize.executeQuery();
		resultSetForGetFileSize.next();
		long fileSize=resultSetForGetFileSize.getLong(1);
		long fileSizeDifference=fileSize-newFileSize;

		String updateFileVersionDetails="UPDATE FileVersionsDetails SET CREATED_TIME=?,FILE_SIZE_IN_BYTES=?,CHECK_SUM=? WHERE FILE_ID=? AND VERSION=?";
		PreparedStatement preparedStatementForUpdateFileVersionDetails=connection.prepareStatement(updateFileVersionDetails);
		preparedStatementForUpdateFileVersionDetails.setLong(1, createdTime);
		preparedStatementForUpdateFileVersionDetails.setLong(2, newFileSize);
		preparedStatementForUpdateFileVersionDetails.setString(3, checkSum);
		preparedStatementForUpdateFileVersionDetails.setInt(4, existFileId);
		preparedStatementForUpdateFileVersionDetails.setFloat(5, existFileVersion);
		preparedStatementForUpdateFileVersionDetails.executeUpdate();
		
		
		String getFileStorage="SELECT FILE_STORAGE_IN_BYTES FROM StoredFilesDetails WHERE FILE_ID=?";
		PreparedStatement preparedStatementForGetFileStorage=connection.prepareStatement(getFileStorage);
		preparedStatementForGetFileStorage.setInt(1, existFileId);
		ResultSet resultSetForGetFileStorage=preparedStatementForGetFileStorage.executeQuery();
		resultSetForGetFileStorage.next();
		long fileStorage=resultSetForGetFileStorage.getLong(1);
		fileStorage-=fileSizeDifference;
		
		String setFileStorage="UPDATE StoredFilesDetails SET FILE_STORAGE_IN_BYTES=? WHERE FILE_ID=?";
		PreparedStatement preparedStatementForSetFileStorage=connection.prepareStatement(setFileStorage);
		preparedStatementForSetFileStorage.setLong(1, fileStorage);
		preparedStatementForSetFileStorage.setInt(2, existFileId);
		preparedStatementForSetFileStorage.executeUpdate();
		
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connection not found");
		}
	}
}
