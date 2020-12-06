package main;

import java.util.HashSet;
import java.util.Set;

public class UnChokedNB {
	volatile int curId;
	volatile Set<Integer> preferredNBSet;
	volatile int optimisticallyUnchokedNB;
	
	UnChokedNB() {
		init();
	}
	
	private void init(){
		preferredNBSet = new HashSet<Integer>();
	}
	
	public void updateOptUnchokedNB(int id) {
		optimisticallyUnchokedNB = id;
		//System.out.println("id: " + id);
	}

	public synchronized int getCurId() {
		return curId;
	}

	public synchronized void setCurId(int curId) {
		this.curId = curId;
	}

}
