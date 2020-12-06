package main;

public class TranState {
	public volatile int id;
	public volatile boolean downloadFinish;
	public volatile boolean uploadFinish;
	
	public TranState(peerInfo newInfo) {
		int id = newInfo.peerId;
		this.downloadFinish = newInfo.bitField.checkCompleted();
		this.uploadFinish = false;
		// TODO Auto-generated constructor stub
	}
	
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
		return downloadFinish && uploadFinish;
	}
	
	public synchronized int getId() {
		return id;
	}

	public synchronized void setId(int id) {
		this.id = id;
	}
	
	
}
