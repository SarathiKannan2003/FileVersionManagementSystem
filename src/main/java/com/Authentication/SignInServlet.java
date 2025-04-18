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

@WebServlet("/SignInServlet")
public class SignInServlet extends HttpServlet {
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
	        String name = jsonRequest.getString("name");
	        String email = jsonRequest.getString("email");
	        String password = jsonRequest.getString("password");

	        AuthenticationDao authenticationDao=new AuthenticationDao();
	        boolean[] errorCheck=authenticationDao.SignInCheck(name,email,password);

	        		if(errorCheck[0]==true) {
	        			JSONObject errorResponse = new JSONObject();
	    	            errorResponse.put("status", "error");
	    	            errorResponse.put("ErrorCode", "Auth01");
	    	            response.getWriter().write(errorResponse.toString());
	    	            response.setStatus(411);
	    	            return;	
	        		}
	        		if(errorCheck[1]==true) {
	        			JSONObject errorResponse = new JSONObject();
	    	            errorResponse.put("status", "error");
	    	            errorResponse.put("ErrorCode", "Auth02");
	    	            response.getWriter().write(errorResponse.toString());
	        			response.setStatus(425);
	    	            return;	
	        		}
	        		if(errorCheck[2]==true) {
	        			JSONObject errorResponse = new JSONObject();
	    	            errorResponse.put("status", "error");
	    	            errorResponse.put("ErrorCode", "Auth03");
	    	            response.getWriter().write(errorResponse.toString());
	        			response.setStatus(427);
	    	            return;	
	        		}
	    
	        JSONObject successResponse = new JSONObject();
	        successResponse.put("status", "success");
	        successResponse.put("message", "signed In successfully!");
	      
	        DaoMethods dao=new DaoMethods();
	        int userId=dao.getUserId(name);
	        HttpSession session=request.getSession();
			session.setAttribute("userName", name);
			session.setAttribute("userId", userId);
	        response.getWriter().write(successResponse.toString());
	        
	}

}
