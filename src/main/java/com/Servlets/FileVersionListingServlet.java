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

import com.FileManipulationDao.FileVersionsDao;
import com.FileManipulationDao.UserForFileVersions;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/FileVersionListingServlet")
public class FileVersionListingServlet extends HttpServlet {
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
        // Parse the JSON data
        JSONObject jsonRequest = new JSONObject(jsonBuilder.toString());
   
        int fileId = jsonRequest.getInt("fileId");
        int offSet = jsonRequest.getInt("offSet");
	  List<Map<String, String>> fileList = new ArrayList<>();
	  FileVersionsDao fileVersionsDao=new FileVersionsDao();
        ArrayList<UserForFileVersions> listOfFiles=fileVersionsDao.UploadedFilesList(fileId,offSet);
        for(UserForFileVersions user:listOfFiles) {
        	  fileList.add(createFile(user.getFileId(), user.getFileName(),user.getVersion(), user.getCreatedTime(),user.getFileSize(),user.getLockedOrNot()));
        }

        Map<String, Object> jsonResponse = new HashMap<>();
        jsonResponse.put("files", fileList);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(jsonResponse);
       
        response.getWriter().write(jsonString);
    }

    private Map<String, String> createFile(String id, String fileName, String version,String createdTime,String size,String isLocked) {
        Map<String, String> file = new LinkedHashMap<>();
        file.put("fileId", id);
        file.put("fileName", fileName);
        file.put("version", version);
        file.put("createdTime", createdTime);
        file.put("fileSize", size);
        file.put("isLocked", isLocked);
        return file;
    }
}
