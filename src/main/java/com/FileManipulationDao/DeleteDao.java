package com.FileManipulationDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.Servlets.DeleteUser;

public class DeleteDao {
	
	public DeleteUser getUnlockedFiles(int fileId) throws SQLException {
		Connection  connection=DataBaseConnection.getConnection();
		DeleteUser user=new DeleteUser();
		ArrayList<Float> unlockedFiles=new ArrayList<>();
		
		String getUnlockedFiles="Select VERSION from FileVersionsDetails where FILE_ID=? AND FILE_LOCK_STATUS=0";
	    PreparedStatement preparedStatementForGetUnlockedFiles=connection.prepareStatement(getUnlockedFiles);
	    preparedStatementForGetUnlockedFiles.setInt(1, fileId);
		ResultSet resultSetForGetUnlockedFiles=preparedStatementForGetUnlockedFiles.executeQuery();
		while(resultSetForGetUnlockedFiles.next()) {
		        unlockedFiles.add(resultSetForGetUnlockedFiles.getFloat(1));
		}
		
		String deleteFileVersions="Delete from FileVersionsDetails where FILE_ID=? AND FILE_LOCK_STATUS=0";
	    PreparedStatement preparedStatementForDeleteFileVersions=connection.prepareStatement(deleteFileVersions);
	    preparedStatementForDeleteFileVersions.setInt(1, fileId);
		preparedStatementForDeleteFileVersions.executeUpdate();

		String getFileStorage="Select SUM(FILE_SIZE_IN_BYTES) from FileVersionsDetails where FILE_ID=?";
	    PreparedStatement preparedStatementForGetFileStorage=connection.prepareStatement(getFileStorage);
	    preparedStatementForGetFileStorage.setInt(1, fileId);
		ResultSet resultSetForGetFileStorage=preparedStatementForGetFileStorage.executeQuery();
		resultSetForGetFileStorage.next();
		long fileStoarge=resultSetForGetFileStorage.getLong(1);
		
		if(fileStoarge==0) {
			user.setNoFiles(true);
			String deleteFile="Delete From StoredFilesDetails WHERE FILE_ID=?";
		    PreparedStatement preparedStatementForDeleteFile=connection.prepareStatement(deleteFile);
		    preparedStatementForDeleteFile.setLong(1, fileId);
			preparedStatementForDeleteFile.executeUpdate();
			user.setList(unlockedFiles);
			return user;
		}
		
		String updateFileStorage="Update StoredFilesDetails SET FILE_STORAGE_IN_BYTES=? WHERE FILE_ID=?";
	    PreparedStatement preparedStatementForUpdateFileStorage=connection.prepareStatement(updateFileStorage);
	    preparedStatementForUpdateFileStorage.setLong(1, fileStoarge);
	    preparedStatementForUpdateFileStorage.setInt(2, fileId);
		preparedStatementForUpdateFileStorage.executeUpdate();
        user.setList(unlockedFiles);
        user.setNoFiles(false);
        user.setFileStorage(fileStoarge);
		
        float topVersion=FileManipulationMethods.getTopVersion(fileId);
		String getFileSize="select FILE_SIZE_IN_BYTES from FileVersionsDetails WHERE FILE_ID=? AND VERSION=?";
	    PreparedStatement preparedStatementForGetFileSize=connection.prepareStatement(getFileSize);
	    preparedStatementForGetFileSize.setLong(1, fileId);
	    preparedStatementForGetFileSize.setFloat(2,topVersion);
		ResultSet resultSetForGetFileSize=preparedStatementForGetFileSize.executeQuery();
		resultSetForGetFileSize.next();
		long fileSize=resultSetForGetFileSize.getLong(1);
		user.setFileSize(fileSize);
		return user;
	}
}
