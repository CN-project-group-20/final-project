package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import file.readConfig;

public class PreferredNB {
	volatile List<Link> linkList;
	volatile Map<Integer, Integer> download_Map;
	volatile Map<Integer, Long> startTime_Map;
	volatile Set<Integer> interested_Set;
	volatile Set<Integer> hasFile_Set;
	
    volatile TranState peerState;
	
	volatile UnChokedNB unchoked_NB;
	
	private final static int MILLISECONDS = 1000;
	private final static int DEFAULT_SCOPE = 1000;
	
	private static int unchokedInterval; 
	private static int pieceNum;
	
	peerInfo myInfo;
	
	PreferredNB(Peer peer) {
		//unchokedInterval = readConfig.unchokingInterval * MILLISECONDS;
		pieceNum = (int)Math.ceil(((double)readConfig.FileSize / (double)readConfig.PieceSize));
		this.download_Map = peer.downloadMap;
		this.startTime_Map = peer.startTimeMap;
		this.interested_Set = peer.interestedSet;
		this.hasFile_Set = peer.hasFileSet;
		this.peerState = peer.peerState;
		this.unchoked_NB = peer.unchoked_NB;
		this.myInfo = peer.info;
		this.linkList = peer.linkList;
	}
	
	public void run() {
		try {
			while (!peerState.isAllFinish()) {
				handlePreferredNeighbor();
				Thread.sleep(unchokedInterval);
				clearPreferredNeighbors();
				if(checkAllPeersDownload()) {
					synchronized(peerState) {
						peerState.setUploadFinish(true);;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Preferred neighbor end!");
	}
	
	private List<Integer> testDownloadSpeed() {
		List<Integer> high_RateList = new ArrayList<>();
		TreeMap<Double, Integer> speed_Map = new TreeMap<Double, Integer>(Collections.reverseOrder());
		long currentTime = System.currentTimeMillis();
		System.out.print("Speed map: ");
		for (Integer cur_peer : interested_Set) {
			double download_Speed = 0;
			if(download_Map.containsKey(cur_peer) && startTime_Map.containsKey(cur_peer)) {
				download_Speed = (download_Map.get(cur_peer) * DEFAULT_SCOPE) * 1.0 / (currentTime - startTime_Map.get(cur_peer))
			}
			else {
				download_Speed = 0;
			}
			speed_Map.put(new Double(download_Speed), cur_peer);
			System.out.print(download_Speed + " " + cur_peer + "**** ");
		}
//		System.out.println();
//		System.out.println("downloadMap: 1002 " + downloadMap.get(1002) + " **** 1003 " + downloadMap.get(1003));
//		System.out.println("startTimeMap: 1002 " + startTimeMap.get(1002) + " **** 1003 " + startTimeMap.get(1003));
//		System.out.println("Optimistically Unchoked Neighbor: " + unchokedNeighbors.optimisticallyUnchokedNeighbor);
		int preferredNeighborsNum = readConfig.NumberOfPreferredNeighbors + 1;
		int i = 0;
		for (Integer id : speed_Map.values()) {
			if (i == preferredNeighborsNum) 
				break;
			else {
				high_RateList.add(id);
				i++;
			}	
		}
		
		if (high_RateList.isEmpty()) 
			return high_RateList;
		
		if (high_RateList.contains(unchoked_NB.optimisticallyUnchokedNB)) {
			high_RateList.remove(unchoked_NB.optimisticallyUnchokedNB);
		}
		if(!(high_RateList.size() != preferredNeighborsNum)){
			int removeIndex = high_RateList.size() - 1;
			high_RateList.remove(removeIndex);
		}
		
		return high_RateList;
	}
	
	private void handlePreferredNeighbor() throws IOException {
		System.out.println("Start selecting the preferred neighbors " + interested_Set.size());
		List<Integer> topRate_List = testDownloadSpeed();
		
		synchronized (unchoked_NB.preferredNBSet) {
			for (Integer key : topRate_List) {
				System.out.println(key + " is added to the top rate list");
				unchoked_NB.preferredNBSet.add(key);
			}
			
			(new Log(myInfo.peerId)).PrefeerredNeighborsLog(myInfo.peerId, topRate_List);
			
			for (Link link : linkList) {
				if (unchoked_NB.preferredNBSet.contains(link.targetInfo.peerId)) {
					link.server.sendUnchokeMessage();
				}
				else {
					//System.out.println("not a valid link");
				}
			}
		}
	}
	
	private void clearPreferredNeighbors() throws IOException {
		synchronized (unchoked_NB.preferredNBSet) {
			for (Link link : linkList) {
				if (unchoked_NB.preferredNBSet.contains(link.targetInfo.peerId)) {
					link.server.sendChokeMessage();
				}
			}
			unchoked_NB.preferredNBSet.clear();
		}
	}
	
	private boolean checkAllPeersDownload() {
		for(Integer peerID : download_Map.keySet()) {
			if(pieceNum != download_Map.get(peerID)) {
				if(hasFile_Set.contains(peerID)) {
					continue;
				}
				if (peerID == myInfo.peerId) {
					continue;
				}
				//System.out.println("peerid = " + peerID + "  hasFile = " + hasFileSet.contains(peerID) + "  size = " +hasFileSet.size());
				
				return false;
			}
		}
		if (!peerState.isDownloadFinish()) {
			return false;
		}
		return true;
	}

}
