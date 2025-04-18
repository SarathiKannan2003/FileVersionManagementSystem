package com.FileManipulationDao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.Hadoop_Handling.HadoopConnection;
import com.Hadoop_Handling.UploadedFileDetails;



public class FileUploadDao {
	Connection  connection=DataBaseConnection.getConnection();
	public  Object getHdfsPath(String userName,int userId,String fileName,String folderName,boolean setTopVersion) {
		HadoopUser hadoopUser=new HadoopUser();
		try {
		    String checkUserDirectoryExistsOrNot="Select USER_ID From StoredFilesDetails WHERE USER_ID=?";
			PreparedStatement preparedStatementForCheackUserDirectoryExistsOrNot=connection.prepareStatement(checkUserDirectoryExistsOrNot);
			preparedStatementForCheackUserDirectoryExistsOrNot.setInt(1, userId);
			ResultSet resultSetForCheckUserDirectoryExistsOrNot=preparedStatementForCheackUserDirectoryExistsOrNot.executeQuery();
			
		    if(resultSetForCheckUserDirectoryExistsOrNot.next()) {
			    int existUserId=resultSetForCheckUserDirectoryExistsOrNot.getInt(1);
			   
			    String checkFileDirectoryExistsOrNot="Select FILE_ID From StoredFilesDetails WHERE USER_ID=? AND FILE_NAME=?";
				PreparedStatement preparedStatementCheckFileDirectoryExistsOrNot=connection.prepareStatement(checkFileDirectoryExistsOrNot);
				preparedStatementCheckFileDirectoryExistsOrNot.setInt(1, existUserId);
				preparedStatementCheckFileDirectoryExistsOrNot.setString(2, fileName);
				ResultSet resultSetCheckFileDirectoryExistsOrNot=preparedStatementCheckFileDirectoryExistsOrNot.executeQuery();
				
				if(resultSetCheckFileDirectoryExistsOrNot.next()) {
					int index=fileName.lastIndexOf('.');
				    String fileType=fileName.substring(index,fileName.length());
			        int fileId=resultSetCheckFileDirectoryExistsOrNot.getInt(1);  
			        
			            String updateVersion="SELECT MAX(VERSION) From FileVersionsDetails WHERE FILE_ID=?";
						PreparedStatement preparedStatementForUpdateVersion=connection.prepareStatement(updateVersion);
						preparedStatementForUpdateVersion.setInt(1, fileId);
						ResultSet resultSetForUpdateVersion=preparedStatementForUpdateVersion.executeQuery();
						resultSetForUpdateVersion.next();
						float version=resultSetForUpdateVersion.getFloat(1);
						
					    version+=0.1;
						version=Math.round(version*10)/10.0f;
						if(setTopVersion) {
							version=(float) Math.ceil(version);
						}
			    	String hdfsPath="/"+userName+"/"+folderName+"/"+folderName+"("+version+")"+fileType;
			    	hadoopUser.setFileName(fileName);
			    	hadoopUser.setUserId(userId);
			    	hadoopUser.setHdfsPath(hdfsPath);
			    	hadoopUser.setVersion(version);
			    	hadoopUser.setFileExists(true);
			    	hadoopUser.setFileId(fileId);
			    	return hadoopUser;
				    
				}
				else {
					String dirPath="/"+userName+"/"+folderName;
			        FileSystem fileSystem=HadoopConnection.getHadoopConnection();					
				    Path path = new Path(dirPath);
				    try {
						fileSystem.mkdirs(path);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		    
			    	int index=fileName.lastIndexOf('.');
				    String fileType=fileName.substring(index,fileName.length());      
			    	String hdfsPath=dirPath+"/"+folderName+"(1.0)"+fileType;
			    	
			    	hadoopUser.setFileName(fileName);
			    	hadoopUser.setUserId(userId);
			    	hadoopUser.setHdfsPath(hdfsPath);
			    	hadoopUser.setVersion((float) 1.0);
			    	hadoopUser.setFileExists(false);
			    	return hadoopUser;
				}
		    }
		    else {
		    	String dirPath = "/"+userName+"/"+folderName;
		        FileSystem fileSystem=HadoopConnection.getHadoopConnection();
					try {				
			            Path path = new Path(dirPath);
			            fileSystem.mkdirs(path);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	int index=fileName.lastIndexOf('.');
			    String fileType=fileName.substring(index,fileName.length());
		    	String hdfsPath=dirPath+"/"+folderName+"(1.0)"+fileType;
		    	
		    	hadoopUser.setFileName(fileName);
		    	hadoopUser.setUserId(userId);
		    	hadoopUser.setHdfsPath(hdfsPath);
		    	hadoopUser.setVersion((float) 1.0);
		    	hadoopUser.setFileExists(false);
		    	return hadoopUser;
		  
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connection not found");
		}
		return hadoopUser;
	}
	
	
	public void sqlUpdateBeforeUploadAFileToHdfs(HadoopUser hadoopUser) {
		try {
    	
    	if(!hadoopUser.isFileExists()) {
	    String insertIntoStoredFilesCredentials="INSERT INTO StoredFilesDetails(USER_ID,FILE_NAME) VALUES(?,?)";
		PreparedStatement preparedStatementForInsertIntoStoredFilesCredentialsQuery=connection.prepareStatement(insertIntoStoredFilesCredentials);
		preparedStatementForInsertIntoStoredFilesCredentialsQuery.setInt(1, hadoopUser.getUserId());
		preparedStatementForInsertIntoStoredFilesCredentialsQuery.setString(2,hadoopUser.getFileName());
		preparedStatementForInsertIntoStoredFilesCredentialsQuery.executeUpdate();
    	}
    	
		String getFileID="SELECT FILE_ID FROM StoredFilesDetails WHERE USER_ID=? AND FILE_NAME=?";
		PreparedStatement preparedStatementForGetFileID=connection.prepareStatement(getFileID);
		preparedStatementForGetFileID.setInt(1, hadoopUser.getUserId());
		preparedStatementForGetFileID.setString(2, hadoopUser.getFileName());
		ResultSet resultSetForGetFileID=preparedStatementForGetFileID.executeQuery();
		resultSetForGetFileID.next();
		int fileId=resultSetForGetFileID.getInt(1);
		hadoopUser.setFileId(fileId);
		
		String insertIntoFileVersionsDetails="INSERT INTO FileVersionsDetails(FILE_ID,VERSION) VALUES(?,?)";
		PreparedStatement preparedStatementForInsertIntoFileVersionsDetails=connection.prepareStatement(insertIntoFileVersionsDetails);
		preparedStatementForInsertIntoFileVersionsDetails.setInt(1, fileId);
		preparedStatementForInsertIntoFileVersionsDetails.setFloat(2,hadoopUser.getVersion());
		preparedStatementForInsertIntoFileVersionsDetails.executeUpdate();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connection not found");
		}
	}
	
	public void sqlUpdateAfterUploadAFileToHdfs(HadoopUser hadoopUser) {
		try {
		
		UploadedFileDetails fileDetails=new UploadedFileDetails(hadoopUser.getHdfsPath());
    	long fileSize=fileDetails.getFileSize();
    	long modifiedTime=fileDetails.lastModifiedTime();
    	
    	if(!hadoopUser.isFileExists()) {
	    String insertIntoStoredFilesCredentials="UPDATE StoredFilesDetails SET CREATED_TIME=?,FILE_STORAGE_IN_BYTES=? WHERE FILE_ID=?";
		PreparedStatement preparedStatementForInsertIntoStoredFilesCredentialsQuery=connection.prepareStatement(insertIntoStoredFilesCredentials);
		preparedStatementForInsertIntoStoredFilesCredentialsQuery.setLong(1, modifiedTime);
		preparedStatementForInsertIntoStoredFilesCredentialsQuery.setLong(2, fileSize);
		preparedStatementForInsertIntoStoredFilesCredentialsQuery.setInt(3, hadoopUser.getFileId());
		preparedStatementForInsertIntoStoredFilesCredentialsQuery.executeUpdate();
		hadoopUser.setFileStorage(fileSize);
    	}
		
		String insertIntoFileVersionsDetails="UPDATE FileVersionsDetails SET CREATED_TIME=?,FILE_SIZE_IN_BYTES=?,CHECK_SUM=? WHERE FILE_ID=? AND VERSION=?";
		PreparedStatement preparedStatementForInsertIntoFileVersionsDetails=connection.prepareStatement(insertIntoFileVersionsDetails);
		preparedStatementForInsertIntoFileVersionsDetails.setLong(1, modifiedTime);
		preparedStatementForInsertIntoFileVersionsDetails.setLong(2, fileSize);
		preparedStatementForInsertIntoFileVersionsDetails.setString(3, hadoopUser.getCheckSum());
		preparedStatementForInsertIntoFileVersionsDetails.setInt(4, hadoopUser.getFileId());
		preparedStatementForInsertIntoFileVersionsDetails.setFloat(5,hadoopUser.getVersion());
		preparedStatementForInsertIntoFileVersionsDetails.executeUpdate();
		hadoopUser.setCreatedTime(modifiedTime);
		hadoopUser.setFileSize(fileSize);
		
		if(hadoopUser.isFileExists()) {
		String getFileStorage="SELECT FILE_STORAGE_IN_BYTES FROM StoredFilesDetails WHERE FILE_ID=?";
		PreparedStatement preparedStatementForGetFileStorage=connection.prepareStatement(getFileStorage);
		preparedStatementForGetFileStorage.setInt(1, hadoopUser.getFileId());
		ResultSet resultSetForGetFileStorage=preparedStatementForGetFileStorage.executeQuery();
		resultSetForGetFileStorage.next();
		long fileStorage=resultSetForGetFileStorage.getLong(1);
		
		String updateFileStorage="UPDATE StoredFilesDetails SET FILE_STORAGE_IN_BYTES=? WHERE FILE_ID=?"; 
		PreparedStatement preparedStatementForUpdateFileStorage=connection.prepareStatement(updateFileStorage);
		preparedStatementForUpdateFileStorage.setLong(1, fileStorage+fileSize);
		preparedStatementForUpdateFileStorage.setInt(2, hadoopUser.getFileId());
		preparedStatementForUpdateFileStorage.executeUpdate();
		hadoopUser.setFileStorage(fileStorage+fileSize);
		}
		
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("connection not found");
		}
	}

}
