package com.Servlets;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.json.JSONObject;

import com.Authentication.DaoMethods;
import com.Hadoop_Handling.HadoopConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;


@WebServlet("/DownloadFileFromListingServlet")
public class DownloadFileFromListingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
		 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			     HttpSession getSession=request.getSession();
		         String userName=(String)getSession.getAttribute("userName");
		         StringBuilder sb = new StringBuilder();
		         BufferedReader reader = request.getReader();
		         String line;
		         while ((line = reader.readLine()) != null) {
		             sb.append(line);
		         }
		         JSONObject json = new JSONObject(sb.toString());
		         int fileId = json.getInt("fileId");
		         float version = json.getFloat("version");
			     String fileName=DaoMethods.getFileNameFromSql(fileId);
			     String hadoopFilePath="/"+userName;
			     int index=fileName.lastIndexOf('.');
				 String folderName=fileName.substring(0,index);
				 String fileType=fileName.substring(index,fileName.length());
				 hadoopFilePath+="/"+folderName+"/"+folderName+"("+version+")"+fileType;
			      
				  FileSystem fileSystem=HadoopConnection.getHadoopConnection();
			      Path hdfsPath = new Path(hadoopFilePath);
			      
			      System.out.println(fileSystem.exists(hdfsPath));
		          if (!fileSystem.exists(hdfsPath)) {
		          response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
		          return;
		           }
		         
		         FSDataInputStream hdfsInputStream = fileSystem.open(hdfsPath);

		         OutputStream outStream = response.getOutputStream();
		         byte[] buffer = new byte[4096];
		         int bytesRead;
		         while ((bytesRead = hdfsInputStream.read(buffer)) != -1) {
		             outStream.write(buffer, 0, bytesRead);
		         }
		         hdfsInputStream.close();
		         outStream.flush();
		         outStream.close();
				
		 }
}
