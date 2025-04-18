package com.Hadoop_Handling;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

public class HadoopConnection {
	public static FileSystem getHadoopConnection() {
		FileSystem fileSystem=null;
		Configuration conf=new Configuration();
		conf.set("fs.defaultFS", "hdfs://localhost:9000");
		try {
			fileSystem = FileSystem.get(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileSystem;
	}
}
