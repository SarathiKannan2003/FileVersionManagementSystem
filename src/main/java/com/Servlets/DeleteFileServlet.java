package com.Servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;

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
import com.FileManipulationDao.DeleteDao;
import com.FileManipulationDao.FileManipulationMethods;
import com.Hadoop_Handling.HadoopConnection;

@WebServlet("/DeleteFileServlet")
public class DeleteFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession getSession=request.getSession();
        String userName=(String)getSession.getAttribute("userName");
		 response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");

	        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
	        StringBuilder jsonBuilder = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            jsonBuilder.append(line);
	        }
	        
	        JSONObject jsonRequest = new JSONObject(jsonBuilder.toString());
	   
	        int fileId = jsonRequest.getInt("fileId");
	        String fileName=DaoMethods.getFileNameFromSql(fileId);
			int index=fileName.lastIndexOf('.');
		    String folderName=fileName.substring(0,index);
		    String fileType=fileName.substring(index,fileName.length());
		    String hdfsPathForFile="/"+userName+"/"+folderName;
		    
		    DeleteDao deleteDao=new DeleteDao();
	   	    DeleteUser user=null;
			 try {
				user=deleteDao.getUnlockedFiles(fileId);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	
			 FileSystem fileSystem=HadoopConnection.getHadoopConnection();
		     JSONObject jsonResponse = new JSONObject();
		     if(user.isNoFiles()) {
		    	 Path filePath=new Path(hdfsPathForFile);
		    	 fileSystem.delete(filePath, true);
		    	 jsonResponse.put("status","allFiles");
		    	 response.setContentType("application/json");
			     response.getWriter().write(jsonResponse.toString());
			     return;
		     }
		     
		    ArrayList<Float> unlockedFiles=user.getList();
		    String newFilePath;
         	for(Float version:unlockedFiles) {    
         		 newFilePath=hdfsPathForFile+"/"+folderName+"("+version+")"+fileType;
         		 Path filePath=new Path(newFilePath);
         	     fileSystem.delete(filePath, false);
         	}
		     long fileSizeInBytes=user.getFileSize();
		     long fileStorageInBytes=user.getFileStorage();
		     
	         String fileSize=FileManipulationMethods.convertFileSizeToHumanReadableFormat(fileSizeInBytes);
	     	 String fileStorage=FileManipulationMethods.convertFileSizeToHumanReadableFormat(fileStorageInBytes);
	     	
		     jsonResponse.put("status","success");
		     jsonResponse.put("fileSize",fileSize);
		     jsonResponse.put("fileStorage",fileStorage);
	    	 response.setContentType("application/json");
		     response.getWriter().write(jsonResponse.toString());
		     return;
	}

}
