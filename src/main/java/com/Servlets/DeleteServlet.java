package com.Servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import com.FileManipulationDao.FileDeleteDao;
import com.FileManipulationDao.FileManipulationMethods;
import com.Hadoop_Handling.HadoopConnection;

@WebServlet("/DeleteServlet")
public class DeleteServlet extends HttpServlet {
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
	        float version= jsonRequest.getFloat("version");
	       
	        float topVersion=FileManipulationMethods .getTopVersion(fileId);
	        if(version==topVersion) {
	        	JSONObject errorResponse = new JSONObject();
	            errorResponse.put("status", "error");
	            response.getWriter().write(errorResponse.toString());
    			response.setStatus(500);
	            return;
	        }
	
	        if(FileManipulationMethods .isFileLocked(fileId,version)) {
	        	JSONObject errorResponse = new JSONObject();
	            errorResponse.put("status", "error");
	            response.getWriter().write(errorResponse.toString());
    			response.setStatus(500);
	            return;
	        }
	        
			 String fileName=DaoMethods.getFileNameFromSql(fileId);
			 int index=fileName.lastIndexOf('.');
		     String folderName=fileName.substring(0,index);
		     String fileType=fileName.substring(index,fileName.length());   
			 String hdfsPathForFile="/"+userName+"/"+folderName+"/"+folderName+"("+version+")"+fileType;
		  
			 FileSystem fileSystem=HadoopConnection.getHadoopConnection();
		     Path filePath=new Path(hdfsPathForFile);
		     fileSystem.delete(filePath, false);
 
			 FileDeleteDao fileDeleteDao=new FileDeleteDao();
		     fileDeleteDao.deleteFile(fileId,version);
		       	        
	        JSONObject jsonResponse = new JSONObject();
	        jsonResponse.put("status", "Deleted Successfully!");

	        response.setContentType("application/json");
	        response.getWriter().write(jsonResponse.toString());
	}

}
