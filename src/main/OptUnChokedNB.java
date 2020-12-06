package main;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import file.readConfig;

public class OptUnChockedNB implements Runnable {
	
    volatile TranState peerState;
	volatile List<Link> linkList;
	volatile Set<Integer> interested_Set;

	private static final int MILLISECONDS = 1000;
	private static int unchoked_Interval;
	volatile UnChokedNB unchokedNB;
	peerInfo my_Info;

	OptUnChockedNB(Peer peer){
		init(peer);
	}
	
	void init(Peer peer) {
		unchoked_Interval = readConfig.OptimisticUnchokingInterval * MILLISECONDS;
		this.linkList = peer.linkList;
		this.peerState = peer.peerState;
		this.interested_Set = peer.interestedSet;
		this.unchokedNB = peer.unchoked_NB;
		this.my_Info = peer.info;
	}
	
	private int getOptUnchokedNB() throws IOException {
		int cur_ID = selectRandomPeerID();
		while((cur_ID == -1)||checkUnhokedAlready(cur_ID)) {
			if(!checkOptUnchokNeeded()) {
				return -1;
			}
			if (interested_Set.size() == 0) {
				return -1;
			}
			cur_ID = selectRandomPeerID();
			if (cur_ID == -1)
				return -1;
		}
		unchokedNB.updateOptUnchokedNB(cur_ID);
		
		(new Log(my_Info.peerId)).OptimisticallyUnchokedNeighborLog(my_Info.peerId, cur_ID);
		
		return cur_ID;
	}

	private Link getLink(int peerID) {
		for(Link link : linkList) {
			if(peerID == link.targetInfo.peerId) {
				return link;
			}
		}
		return null;
	}
	
	private int selectRandomPeerID() {
		Integer[] peers;
		synchronized(interested_Set) {
			peers = ((Integer[])interested_Set.toArray(new Integer[interested_Set.size()]));
			if (((Integer[])interested_Set.toArray(new Integer[interested_Set.size()])).length == 0)
				return -1;
		}
		int index = (int)(peers.length*Math.random());
		return peers[index].intValue();
	}
	
	private boolean checkOptUnchokNeeded() {
		return !unchokedNB.preferredNBSet.containsAll(interested_Set);
	}
	
	private boolean checkUnhokedAlready(int peerId) {
		return !unchokedNB.preferredNBSet.contains(new Integer(peerId));
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(unchoked_Interval);
			while(!peerState.isAllFinish()) {
				int peerID = getOptUnchokedNB();
				Link link = getLink(peerID);
				if(link == null) {
					Thread.sleep(unchoked_Interval);
					continue;
				}
				link.server.sendUnchokeMessage();
				Thread.sleep(unchoked_Interval);
				if(interested_Set.contains(peerID) || unchokedNB.optimisticallyUnchokedNB == peerID) {
					link.server.sendChokeMessage();
				}
			}
		}catch(InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Optimistic neighbor end!");
	}
	
	

}