package main;

public class TranState {
	public volatile boolean downloadFinish;
	public volatile boolean uploadFinish;
	
	public boolean isDownloadFinish() {
		synchronized(this) {
			return downloadFinish;
		}
	}
	
	public void setDownloadFinish(boolean downloadFinish) {
		synchronized(this) {
			this.downloadFinish = downloadFinish;
		}
		
	}
	
	public boolean isUploadFinish() {
		synchronized(this) {
			return uploadFinish;
		}
		
	}
	public void setUploadFinish(boolean uploadFinish) {
		synchronized(this) {
			this.uploadFinish = uploadFinish;
		}
	}
	
	public boolean isAllFinish() {
		return isDownloadFinish() && isUploadFinish();
	}
	
	
}
