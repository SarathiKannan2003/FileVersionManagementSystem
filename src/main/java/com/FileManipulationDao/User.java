package com.FileManipulationDao;
public class User {
	private String fileName;
	private String createdTime;
	private String folderStorage;
	private int fileId;
	private String fileSize;
	
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public int getFileId() {
		return fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getFolderStorage() {
		return folderStorage;
	}
	public void setFolderStorage(String folderStorage) {
		this.folderStorage = folderStorage;
	} 
}
