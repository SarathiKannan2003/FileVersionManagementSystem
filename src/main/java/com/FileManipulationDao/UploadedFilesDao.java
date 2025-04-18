package com.FileManipulationDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.Authentication.AuthenticationMethods;

public class UploadedFilesDao {
	public ArrayList<User> UploadedFilesList(int userId,int pageNo) {
		ArrayList<User> listOfFiles=new ArrayList<>();
          try {
    Connection  connection=DataBaseConnection.getConnection();
    String getUserId="Select TIME_ZONE From UserInfo WHERE USER_ID=?";
	PreparedStatement preparedStatementForGetUserId=connection.prepareStatement(getUserId);
	preparedStatementForGetUserId.setInt(1,userId);
	ResultSet resultSetForGetUserId=preparedStatementForGetUserId.executeQuery();
    resultSetForGetUserId.next();
    String userTimeZone=resultSetForGetUserId.getString(1);
       
    String getFileList="Select FILE_NAME,FILE_ID,CREATED_TIME,FILE_STORAGE_IN_BYTES From StoredFilesDetails WHERE USER_ID=? AND FILE_ID>? LIMIT 10";
    PreparedStatement preparedStatementForGetFileList=connection.prepareStatement(getFileList);
	preparedStatementForGetFileList.setInt(1, userId);
	preparedStatementForGetFileList.setInt(2, pageNo);
	ResultSet resultSetForGetFileList=preparedStatementForGetFileList.executeQuery();
    while(resultSetForGetFileList.next()) {
    	User user=new User();
    	user.setFileName(resultSetForGetFileList.getString(1));
    	int fileId=resultSetForGetFileList.getInt(2);
        user.setFileId(fileId);
    	long unixTime=resultSetForGetFileList.getLong(3);
    	String createdTime=AuthenticationMethods.getTimeFormatFromUnixTime(unixTime,userTimeZone);
    	user.setCreatedTime(createdTime);
    	long folderStorageInBytes=resultSetForGetFileList.getLong(4);
        String fileStorage=FileManipulationMethods .convertFileSizeToHumanReadableFormat(folderStorageInBytes);

        float topVersion=FileManipulationMethods .getTopVersion(fileId);
        long fileSizeInBytes= FileManipulationMethods .getFileSize(fileId,topVersion);
        String fileSize=FileManipulationMethods .convertFileSizeToHumanReadableFormat(fileSizeInBytes);
    	user.setFolderStorage(fileStorage);
    	user.setFileSize(fileSize);
    	listOfFiles.add(user);

    }

    return listOfFiles;
} catch (SQLException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	System.out.println("connection not found");
       }
      return listOfFiles;
    }
}