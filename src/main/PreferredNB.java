package main;

import java.util.List;
import java.util.Map;
import java.util.Set;

import file.readConfig;

public class PreferredNB {
	private final static int MILLISECONDS = 1000;
	private final static int DEFAULT_SCOPE = 1000;
	
	private static int unchokedInterval; 
	private static int pieceNumber;
	
	peerInfo myInfo;
	
	volatile List<Link> linkList;
	volatile Map<Integer, Integer> downloadMap;
	volatile Map<Integer, Long> startTimeMap;
	volatile Set<Integer> interestedSet;
	volatile Set<Integer> hasFileSet;
	
	volatile TranState peerState;
	
	//volatile UnchokedNeighbors unchokedNeighbors;
	
	PreferredNB(Peer peer) {
		//unchokedInterval = readConfig.unchokingInterval * MILLISECONDS;
		pieceNumber = (int)Math.ceil(((double)readConfig.FileSize / (double)readConfig.PieceSize));
		this.myInfo = peer.info;
		this.linkList = peer.linkList;
		this.downloadMap = peer.downloadMap;
		this.startTimeMap = peer.startTimeMap;
		this.interestedSet = peer.interestedSet;
		this.hasFileSet = peer.hasFileSet;
		this.peerState = peer.peerState;
		//this.unchokedNeighbors = peer.unchokedNeighbors;
	}

}
