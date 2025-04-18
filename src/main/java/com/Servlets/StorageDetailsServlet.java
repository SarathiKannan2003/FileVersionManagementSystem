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

import com.Authentication.DaoMethods;
import com.FileManipulationDao.FileManipulationMethods;

@WebServlet("/StorageDetailsServlet")
public class StorageDetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	  
	        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
	        StringBuilder jsonBuilder = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            jsonBuilder.append(line);
	        }
	       
	        JSONObject jsonRequest = new JSONObject(jsonBuilder.toString());
	        int userId = jsonRequest.getInt("userId");
	        
		DaoMethods dao=new DaoMethods();
	    long userStorageInBytes=dao.getUserStorage(userId);
		//long userStorageInBytes=UserStorage.getUserStorage(userId);
	    String userStorage=FileManipulationMethods .convertFileSizeToHumanReadableFormat(userStorageInBytes);
	    long overAllFileSizeInBytes=dao.getOverAllFileSize(userId);
	    String overAllFileSize=FileManipulationMethods .convertFileSizeToHumanReadableFormat(overAllFileSizeInBytes);
	
		JSONObject jsonResponse = new JSONObject();
	        jsonResponse.put("userStorage", userStorage);
	        jsonResponse.put("overAllFileSize", overAllFileSize);
	        response.setContentType("application/json");
	        response.getWriter().write(jsonResponse.toString());
	}

}
