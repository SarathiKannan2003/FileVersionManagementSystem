package com.Servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.json.JSONObject;

import com.Authentication.AuthenticationMethods;
import com.Authentication.DaoMethods;
import com.FileManipulationDao.FileManipulationMethods;
import com.FileManipulationDao.OverwriteDao;
import com.Hadoop_Handling.HadoopConnection;
import com.Hadoop_Handling.UploadedFileDetails;

@WebServlet("/OverWriteServlet")
@MultipartConfig
public class OverWriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		HttpSession getSession=request.getSession();
        String userName=(String)getSession.getAttribute("userName");
        int userId=(int)getSession.getAttribute("userId");

        String newFileName=request.getHeader("fileName");
        int existFileId=Integer.parseInt(request.getHeader("existFileId"));
        String existFileName=DaoMethods.getFileNameFromSql(existFileId);
        String existFileVersion=request.getHeader("existFileVersion");
	    
	    int index=existFileName.lastIndexOf('.');
	    String existFolderName=existFileName.substring(0,index);
	    String existFileType=existFileName.substring(index,existFileName.length());
	    
	    int ind=newFileName.lastIndexOf('.');
	    String newFileType=newFileName.substring(ind,newFileName.length());

	    if(FileManipulationMethods .isFileLocked(existFileId,Float.parseFloat(existFileVersion))) {
	        	JSONObject errorResponse = new JSONObject();
	            errorResponse.put("error", "Illegal action");
	            response.getWriter().write(errorResponse.toString());
    			response.setStatus(500);
	            return;
	        }
	        
	        if(!newFileType.equals(existFileType)) {
	        	JSONObject errorResponse = new JSONObject();
	            errorResponse.put("status", "FileType Mismatched");
	            errorResponse.put("ErrorCode", "Err01");
	            response.getWriter().write(errorResponse.toString());
	            response.setStatus(422);
	            return;	
	        }
	        String newHdfsPath="/"+userName+"/"+existFolderName+"/"+existFolderName+"("+existFileVersion+")"+existFileType;
	        Path hdfsPath=new Path(newHdfsPath);
	        FileSystem fileSystem=HadoopConnection.getHadoopConnection();
            synchronized(this) {
            MessageDigest digest=null;
    		try {
    			digest = MessageDigest.getInstance("SHA-256");
    		} catch (NoSuchAlgorithmException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	
    		 try (InputStream requestStream = request.getInputStream();
    	              OutputStream hdfsOutputStream = fileSystem.create(hdfsPath, true)) {
    	             
    	             byte[] buffer = new byte[4096];
    	             int bytesRead;
    	             while ((bytesRead = requestStream.read(buffer)) != -1) {
    	                 hdfsOutputStream.write(buffer, 0, bytesRead);
    	                 digest.update(buffer, 0, bytesRead);
    	             }
    		 }
            
            byte[] hashBytes = digest.digest();
        
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            String checkSum=hexString.toString();
	        UploadedFileDetails fileDetails=new UploadedFileDetails(newHdfsPath);
	        long fileSizeInBytes=fileDetails.getFileSize();
	        long unixTime=fileDetails.lastModifiedTime();
	        OverwriteDao fileVersionUploadDao=new OverwriteDao();
	        fileVersionUploadDao.sqlUpdateAfterOverWriting(existFileId, Float.parseFloat(existFileVersion), unixTime,fileSizeInBytes,checkSum);
	        
	        String fileSize=FileManipulationMethods .convertFileSizeToHumanReadableFormat(fileSizeInBytes);
	        String userTimeZone=FileManipulationMethods .getTimeZone(userId);
	        String createdTime=AuthenticationMethods.getTimeFormatFromUnixTime(unixTime,userTimeZone);
	        
	        JSONObject jsonResponse = new JSONObject();
	        jsonResponse.put("createdTime",createdTime);
	        jsonResponse.put("fileSize",fileSize);
	        jsonResponse.put("status","Successfully OverWrited");
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse.toString());
            }
	    }
}
