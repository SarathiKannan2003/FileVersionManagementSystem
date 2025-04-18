package com.Servlets;

import java.util.ArrayList;

public class DeleteUser {
	ArrayList<Float> list;
	boolean noFiles;
	long fileSize;
	long fileStorage;
	public ArrayList<Float> getList() {
		return list;
	}
	public void setList(ArrayList<Float> list) {
		this.list = list;
	}
	public boolean isNoFiles() {
		return noFiles;
	}
	public void setNoFiles(boolean noFiles) {
		this.noFiles = noFiles;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public long getFileStorage() {
		return fileStorage;
	}
	public void setFileStorage(long fileStorage) {
		this.fileStorage = fileStorage;
	}
	
}
