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

import org.json.JSONObject;

@WebServlet("/OpenFileServlet")
public class OpenFileServlet extends HttpServlet {
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
	        
	        JSONObject jsonResponse = new JSONObject();
	        jsonResponse.put("successs", "File opened sucessfully!");
	        jsonResponse.put("fileId", fileId);
	        
	        HttpSession session=request.getSession();
			session.setAttribute("fileId", fileId);
	        response.setContentType("application/json");
	        response.getWriter().write(jsonResponse.toString());
	}

}
