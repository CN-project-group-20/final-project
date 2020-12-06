package main;

import java.io.IOException;
import file.readConfig;
import file.readFile;
/*Sun:The above two import added by me because this file has used the two classes.*/


import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import file.*;

public class Peer {
	volatile peerInfo info;
	volatile readConfig config;
	volatile readFile readFile;
	volatile TranState peerState;
	volatile UnChokedNB unchoked_NB;
	
	volatile List<Link> linkList;
	volatile Map<Integer, Integer> downloadMap;
	volatile Map<Integer, Long> startTimeMap;
	volatile Set<Integer> interestedSet;
	volatile Set<Integer> inFlightSet;
	volatile Set<Integer> hasFileSet;
	
	public Peer(int peerNumber) throws IOException {
		init(peerNumber);
		
	}
	
	public void init(int peerNumber) throws UnknownHostException, IOException {
		config = new readConfig();
		config.readpeerInfo(peerNumber);
		info = config.myInfo;
		peerState = new TranState(info);
		unchoked_NB = new UnChokedNB();
		
	    linkList = new ArrayList<Link>();
	    downloadMap = new HashMap<Integer, Integer>();
		startTimeMap = new HashMap<Integer, Long>();
		interestedSet = new HashSet<Integer>();
		inFlightSet = new HashSet<Integer>();
		hasFileSet = new HashSet<Integer>();
	    
	    for(peerInfo targetInfo : config.peerList) {
	    	if(targetInfo != info) {
	    		downloadMap.put(targetInfo.peerId, 0);
	    		if(targetInfo.ownthisfile) {
	    			hasFileSet.add(targetInfo.peerId);
	    		}
	    		Link newLink = new Link(this, targetInfo);
	    		newLink.init();
	    		linkList.add(newLink);
	    		new Thread(newLink).start();
	    	}
	    }
	}
	
	public static void main(String[] args) {
		try {
			Peer newPeer = new Peer(Integer.parseInt(args[0]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}

