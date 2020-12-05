package main;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Link implements Runnable{
	
	volatile List<Link> linkList;
	public int id;
	public int portNum;
	public int hostName;
	
	Socket linkSocket;
	
	
	volatile peerInfo localInfo;
	volatile peerInfo targetInfo;
	
	Client client;
	Server server;
	
	public Link(Peer local, peerInfo targetInfo) {
		this.localInfo = local.info;
		this.targetInfo = targetInfo;

	}
	
	public void init() throws UnknownHostException, IOException {
		server = new Server();
		client = new Client();
		if(localInfo.peerId < targetInfo.peerId) {
			//create a server and wait for connection
			linkSocket = server.init(localInfo.portNumber);
		}
		else {
			linkSocket = client.init(targetInfo.hostName,targetInfo.portNumber);
		}
	}
	
	public void run() {
		
	}
}
