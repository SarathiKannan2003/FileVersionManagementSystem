package com.FileManipulationDao;

public class HadoopUser {
	private String hdfsPath;
	private int userId;
	private String fileName;
	private float version;
	private boolean fileExists;
	private int fileId;
	private long fileStorage;
	private long createdTime;
	private String checkSum;
	private String timeZone;
	private long fileSize;
	
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public long getFileStorage() {
		return fileStorage;
	}
	public void setFileStorage(long fileStorage) {
		this.fileStorage = fileStorage;
	}
	public long getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	public String getCheckSum() {
		return checkSum;
	}
	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}
	public boolean isFileExists() {
		return fileExists;
	}
	public void setFileExists(boolean fileExists) {
		this.fileExists = fileExists;
	}
	public float getVersion() {
		return version;
	}
	public void setVersion(float version) {
		this.version = version;
	}
	public String getHdfsPath() {
		return hdfsPath;
	}
	public void setHdfsPath(String hdfsPath) {
		this.hdfsPath = hdfsPath;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
