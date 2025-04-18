package com.Servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.json.JSONObject;

import com.Authentication.DaoMethods;
import com.FileManipulationDao.FileUploadDao;
import com.FileManipulationDao.HadoopUser;
import com.Hadoop_Handling.HadoopConnection;


@WebServlet("/UploadFileVersionServlet")
public class UploadFileVersionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		  HttpSession getSession=request.getSession();
          String userName=(String)getSession.getAttribute("userName");
          int userId=(int)getSession.getAttribute("userId");
          int fileId=(int)getSession.getAttribute("fileId");
		  response.setContentType("application/json");
	      response.setCharacterEncoding("UTF-8");
	      
	      String fileName=DaoMethods.getFileNameFromSql(fileId); 
		  int index=fileName.lastIndexOf('.');
	      String folderName=fileName.substring(0,index);
	      String fileType=fileName.substring(index,fileName.length());
	      
	      String uploadedFileName=request.getParameter("fileName");
		  int ind=uploadedFileName.lastIndexOf('.');
		  String existFileType=uploadedFileName.substring(ind,uploadedFileName.length());
	    
		  if(!fileType.equals(existFileType)) {
			  JSONObject errorResponse = new JSONObject();
	            errorResponse.put("status", "FileType Mismatched");
	            errorResponse.put("ErrorCode", "Err01");
	            response.getWriter().write(errorResponse.toString());
	            response.setStatus(422);
	            return;	
		  }
		  
          HadoopUser hadoopUser=new HadoopUser();
          FileUploadDao fileUploadDao=new FileUploadDao();
          
          synchronized(this) {
	      hadoopUser=(HadoopUser) fileUploadDao.getHdfsPath(userName,userId,fileName,folderName,false);
	      fileUploadDao.sqlUpdateBeforeUploadAFileToHdfs(hadoopUser);
          }
          
          Path newHdfsPath = new Path(hadoopUser.getHdfsPath());
          FileSystem fileSystem=HadoopConnection.getHadoopConnection();
          JSONObject jsonResponse = new JSONObject();
          
          MessageDigest digest=null;
       	  try {
   		 	digest = MessageDigest.getInstance("SHA-256");
   		  } catch (NoSuchAlgorithmException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		  }
   	
           try (InputStream requestStream = request.getInputStream();
                OutputStream hdfsOutputStream =fileSystem.create(newHdfsPath, true)) {
               
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
   
       	  jsonResponse.put("status","success" );
          response.getWriter().write(jsonResponse.toString());
          
	}

}
