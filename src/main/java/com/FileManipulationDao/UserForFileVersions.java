package com.FileManipulationDao;

public class UserForFileVersions {
	private String version;
	private String createdTime;
	private String fileSize;
	private String lockedOrNot;
	private String fileId;
	private String fileName;
	
	public String getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId+"";
	}
	public String getLockedOrNot() {
		return lockedOrNot;
	}
	public void setLockedOrNot(int lockedOrNot) {
		this.lockedOrNot = lockedOrNot+"";
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(float version) {
		this.version = version+"";
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
}
