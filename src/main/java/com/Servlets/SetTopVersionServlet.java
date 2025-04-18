package com.Servlets;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.json.JSONObject;

import com.Authentication.DaoMethods;
import com.FileManipulationDao.FileManipulationMethods;
import com.FileManipulationDao.FileUploadDao;
import com.FileManipulationDao.HadoopUser;
import com.Hadoop_Handling.HadoopConnection;

import javax.servlet.annotation.WebServlet;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@WebServlet("/SetTopVersionServlet")
public class SetTopVersionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		    HttpSession getSession=request.getSession();
            String userName=(String)getSession.getAttribute("userName");
            int userId=(int)getSession.getAttribute("userId");
		    response.setContentType("application/json");
	        response.setCharacterEncoding("UTF-8");
	       
	        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
	        StringBuilder jsonBuilder = new StringBuilder();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            jsonBuilder.append(line);
	        }
	        JSONObject jsonRequest = new JSONObject(jsonBuilder.toString());
	        float version= jsonRequest.getFloat("version");
	        int fileId = jsonRequest.getInt("fileId");
	        
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
		 String hdfsFilePath="/"+userName+"/"+folderName+"/"+folderName+"("+version+")"+fileType;
		 
         if (hdfsFilePath == null || hdfsFilePath.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File path is required.");
            return;
          }

            FileSystem fileSystem=HadoopConnection.getHadoopConnection();
            Path path = new Path(hdfsFilePath);

            // Open an input stream to read the file from HDFS
            FSDataInputStream hdfsInputStream = fileSystem.open(path);
            HadoopUser hadoopUser=new HadoopUser();
            FileUploadDao fileUploadDao=new FileUploadDao();
            
            synchronized(this) {
	        hadoopUser=(HadoopUser) fileUploadDao.getHdfsPath(userName,userId,fileName,folderName,true);
	        fileUploadDao.sqlUpdateBeforeUploadAFileToHdfs(hadoopUser);
            }
            
            Path newHdfsPath = new Path(hadoopUser.getHdfsPath());
            MessageDigest digest=null;
			try {
				digest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try (FSDataOutputStream outputStream = fileSystem.create(newHdfsPath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = hdfsInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    digest.update(buffer, 0, bytesRead);
                }
            }
            byte[] hashBytes = digest.digest();
	        // Convert byte array to hex string
	        StringBuilder hexString = new StringBuilder();
	        for (byte b : hashBytes) {
	            hexString.append(String.format("%02x", b));
	        }

	        String checkSum=hexString.toString();
	        hadoopUser.setCheckSum(checkSum);
            fileUploadDao.sqlUpdateAfterUploadAFileToHdfs(hadoopUser);
            hdfsInputStream.close();
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "success");
	        
	        response.setContentType("application/json");
	        response.getWriter().write(jsonResponse.toString());        
            
	}

}
