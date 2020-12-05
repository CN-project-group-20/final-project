package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
	 Socket socket;
 
    /**
     * 构造方法
     * @param port 端口
     */
    public Server(Link link){

    }
    
    public Server() {
    	
    }
 
    public Socket init(int portNum) throws UnknownHostException, IOException{
    	ServerSocket listener = new ServerSocket(portNum);
    	socket = listener.accept();
    	listener.close();
    	return socket;
    }

}

