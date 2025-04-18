package com.FileManipulationDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.Authentication.AuthenticationMethods;
import com.Authentication.DaoMethods;

public class FileVersionsDao {
	public ArrayList<UserForFileVersions> UploadedFilesList(int fileId,int offSet) {
		ArrayList<UserForFileVersions> listOfFileVersions=new ArrayList<>();
          try {
        	  Connection  connection=DataBaseConnection.getConnection();
	    String getUserId="Select USER_ID From StoredFilesDetails WHERE FILE_ID=?";
	    PreparedStatement preparedStatementForGetUserId=connection.prepareStatement(getUserId);
		preparedStatementForGetUserId.setInt(1, fileId);
		ResultSet resultSetForGetFileId=preparedStatementForGetUserId.executeQuery();
		resultSetForGetFileId.next();
		int userId=resultSetForGetFileId.getInt(1);
	
		String getTimeZone="Select TIME_ZONE From UserInfo WHERE USER_ID=?";
		PreparedStatement preparedStatementForGetTimeZone=connection.prepareStatement(getTimeZone);
		preparedStatementForGetTimeZone.setInt(1, userId);
		ResultSet resultSetForGetTimeZone=preparedStatementForGetTimeZone.executeQuery();
	    resultSetForGetTimeZone.next();
	    String userTimeZone=resultSetForGetTimeZone.getString(1);
	    
		String getFileList="Select VERSION,CREATED_TIME,FILE_SIZE_IN_BYTES,FILE_LOCK_STATUS From FileVersionsDetails WHERE FILE_ID=? ORDER BY VERSION DESC LIMIT ? OFFSET ?";
	    PreparedStatement preparedStatementForGetFileList=connection.prepareStatement(getFileList);
		preparedStatementForGetFileList.setInt(1, fileId);
		if(offSet==-1) {
			preparedStatementForGetFileList.setInt(2, 2);
			preparedStatementForGetFileList.setInt(3, 0);
		}
		else {
		preparedStatementForGetFileList.setInt(2, 10);
		preparedStatementForGetFileList.setInt(3, offSet*10);
		}
		ResultSet resultSetForGetFileList=preparedStatementForGetFileList.executeQuery();
	    while(resultSetForGetFileList.next()) {
	    	
	    	UserForFileVersions user=new UserForFileVersions();
	    	float version=resultSetForGetFileList.getFloat(1);
	    	user.setVersion(version);
	
	    	long unixTime=resultSetForGetFileList.getLong(2);
	    
	    	String createdTime=AuthenticationMethods.getTimeFormatFromUnixTime(unixTime,userTimeZone);
	    	user.setCreatedTime(createdTime);
	    	long fileSizeInBytes=resultSetForGetFileList.getLong(3);
	    	String actualFileSize=FileManipulationMethods .convertFileSizeToHumanReadableFormat(fileSizeInBytes);
	    	user.setFileSize(actualFileSize);
	    	String fileName=DaoMethods.getFileNameFromSql(fileId);
	    	int index=fileName.lastIndexOf('.');
		    String folderName=fileName.substring(0,index);
		    String fileType=fileName.substring(index,fileName.length());
		      
	    	user.setFileName(folderName+"("+version+")"+fileType);
	    	user.setLockedOrNot(resultSetForGetFileList.getInt(4));
	    	user.setFileId(fileId);
	    	listOfFileVersions.add(user);
	    }
	    return listOfFileVersions;
          }
          catch (SQLException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        		System.out.println("connection not found");
        	       }
        	      return listOfFileVersions;
	}

}
