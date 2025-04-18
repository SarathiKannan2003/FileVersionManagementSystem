package com.Servlets;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import com.Authentication.AuthenticationMethods;
import com.FileManipulationDao.FileManipulationMethods;
import com.FileManipulationDao.FileUploadDao;
import com.FileManipulationDao.HadoopUser;
import com.Hadoop_Handling.HadoopConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;

@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 String fileName = request.getParameter("fileName");
	     HttpSession getSession=request.getSession();
         String userName=(String)getSession.getAttribute("userName");
         int userId=(int)getSession.getAttribute("userId");
      
         response.setContentType("application/json");
         JSONObject jsonResponse = new JSONObject();

         if (fileName == null || fileName.isEmpty()) {
             jsonResponse.put("status", "error");
             jsonResponse.put("message", "Filename is required");
             response.getWriter().write(jsonResponse.toString());
             return;
         }
         
         int index=fileName.lastIndexOf('.');
         String folderName=fileName.substring(0,index);
         
         HadoopUser hadoopUser=new HadoopUser();
         FileUploadDao fileUploadDao=new FileUploadDao();
         synchronized(this) {
         hadoopUser=(HadoopUser) fileUploadDao.getHdfsPath(userName,userId,fileName,folderName,false);
         fileUploadDao.sqlUpdateBeforeUploadAFileToHdfs(hadoopUser);
         }
 	     String path = hadoopUser.getHdfsPath();
 	     Path hdfsPath=new Path(path);
 	     FileSystem fileSystem=HadoopConnection.getHadoopConnection();
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
            
         } catch (IOException e) {
             jsonResponse.put("status", "error");
             jsonResponse.put("message", e.getMessage());
         }
         byte[] hashBytes = digest.digest();

         StringBuilder hexString = new StringBuilder();
         for (byte b : hashBytes) {
             hexString.append(String.format("%02x", b));
         }
         String checkSum=hexString.toString();
         hadoopUser.setCheckSum(checkSum);
         fileUploadDao.sqlUpdateAfterUploadAFileToHdfs(hadoopUser);
           
         long unixTime=hadoopUser.getCreatedTime();
         String userTimeZone=FileManipulationMethods .getTimeZone(userId);
         String createdTime=AuthenticationMethods.getTimeFormatFromUnixTime(unixTime,userTimeZone);
         
         long fileSizeInBytes= hadoopUser.getFileSize();
         String fileSize=FileManipulationMethods .convertFileSizeToHumanReadableFormat(fileSizeInBytes);

     	 long folderStorageInBytes=hadoopUser.getFileStorage();
     	 String fileStorage=FileManipulationMethods.convertFileSizeToHumanReadableFormat(folderStorageInBytes);
     	
     	jsonResponse.put("fileId", hadoopUser.getFileId());
        jsonResponse.put("fileName", fileName);
        jsonResponse.put("createdTime",createdTime);
        jsonResponse.put("fileSize",fileSize);
        jsonResponse.put("fileStorage",fileStorage);
        jsonResponse.put("fileExist",hadoopUser.isFileExists());

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
     }
       
}