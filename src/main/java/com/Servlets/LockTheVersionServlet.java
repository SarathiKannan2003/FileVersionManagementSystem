package com.Servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import com.FileManipulationDao.FileManipulationMethods;

@WebServlet("/LockTheVersionServlet")
public class LockTheVersionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		    response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	        // Read the incoming JSON data from the request
	        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
	        StringBuilder jsonBuilder = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            jsonBuilder.append(line);
	        }
	  
	        JSONObject jsonRequest = new JSONObject(jsonBuilder.toString());
	        int fileId = jsonRequest.getInt("fileId");
	        float version=jsonRequest.getFloat("version");
	        
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
	      
	        FileManipulationMethods .lockTheFile(fileId, version);
	        
	        JSONObject jsonResponse = new JSONObject();
	        jsonResponse.put("status", "Successfully Locked");

	        response.setContentType("application/json");
	        response.getWriter().write(jsonResponse.toString());     
	      
	}

}
