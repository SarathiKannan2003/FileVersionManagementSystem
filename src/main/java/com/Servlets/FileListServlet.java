package com.Servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import com.FileManipulationDao.UploadedFilesDao;
import com.FileManipulationDao.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/FileListServlet")
public class FileListServlet extends HttpServlet {
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
	        int pageNo  = jsonRequest.getInt("pageNo");
	       
	        
		  List<Map<String, String>> fileList = new ArrayList<>();

		    UploadedFilesDao uploadedFilesDao=new UploadedFilesDao();
	        ArrayList<User> listOfFiles=uploadedFilesDao.UploadedFilesList(userId,pageNo);
	        for(User user:listOfFiles) {
	        	  fileList.add(createFile(user.getFileId()+"", user.getFileName(), user.getCreatedTime(),
	        			  user.getFileSize(),user.getFolderStorage()));
	        
	        }

	        Map<String, Object> jsonResponse = new HashMap<>();
	        jsonResponse.put("files", fileList);

	        ObjectMapper objectMapper = new ObjectMapper();
	        String jsonString = objectMapper.writeValueAsString(jsonResponse);
	        
	        response.getWriter().write(jsonString);
			
	    }

	    private Map<String, String> createFile(String id, String name, String createdTime,String fileSize,String fileStorage) {
	        Map<String, String> file = new LinkedHashMap<>();
	        file.put("fileId", id);
	        file.put("fileName", name);
	        file.put("createdTime", createdTime);
	        file.put("fileSize", fileSize);
	        file.put("fileStorage", fileStorage);
	        return file;
	    }

}
