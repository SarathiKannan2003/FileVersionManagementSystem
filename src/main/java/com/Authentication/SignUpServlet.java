package com.Authentication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;


@WebServlet("/SignUpServlet")
public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        try {
			while ((line = reader.readLine()) != null) {
			    jsonBuilder.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
        JSONObject jsonRequest = new JSONObject(jsonBuilder.toString());
        String name = jsonRequest.getString("name");
        String email = jsonRequest.getString("email");
        String mobileNumber = jsonRequest.getString("mobileNumber");
        String timeZone = jsonRequest.getString("timeZone");
        String password = jsonRequest.getString("password");
       
        AuthenticationDao authenticationDao=new AuthenticationDao();
        String dataBaseAction=authenticationDao.SignUpCheck(name,email,mobileNumber,timeZone,password);
        if(dataBaseAction.equals("userName")) {
       	JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("ErrorCode", "Auth04");
            response.getWriter().write(errorResponse.toString());
        	response.setStatus(430);
            return;	
        }
        if(dataBaseAction.equals("email")) {
        	JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("ErrorCode", "Auth05");
            response.getWriter().write(errorResponse.toString());
        	response.setStatus(432);
            return;	
        }
    
        
        JSONObject successResponse = new JSONObject();
        successResponse.put("status", "success");
        successResponse.put("message", "signed Up successfully!");
      
        DaoMethods dao=new DaoMethods();
        int userId=dao.getUserId(name);
        HttpSession session=request.getSession();
		session.setAttribute("userName", name);
		session.setAttribute("userId", userId);
        try {
			response.getWriter().write(successResponse.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

}
