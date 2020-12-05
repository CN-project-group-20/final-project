package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	Socket socket;
	
	public Socket init(String hostName, int portNum) throws UnknownHostException, IOException{
    	socket = new Socket(hostName, portNum);
    	return socket;
    }


}
