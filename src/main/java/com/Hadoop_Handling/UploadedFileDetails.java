package com.Hadoop_Handling;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileStatus;
import java.io.IOException;

public class UploadedFileDetails {
    FileSystem fileSystem;
    Path filePath;
    FileStatus fileStatus ;
	public UploadedFileDetails(String hdfsPath){
		fileSystem=HadoopConnection.getHadoopConnection();
		filePath = new Path(hdfsPath);
		try {
			fileStatus = fileSystem.getFileStatus(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	     public long getFileSize() {
	         return this.fileStatus.getLen();
	     }
	     public long lastModifiedTime () {
	    	 return this.fileStatus.getModificationTime();
	     }
}