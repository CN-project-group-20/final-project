package file;

import java.io.*;
import java.util.*;

import message.BitField;
import main.peerInfo;

public class readConfig 
{
	//in this class, implement the loading of configuration which is in the page 5 of the "project description"
	//I need to read the config of Common.cfg and peerInfo.cfg so that the peerprocess could use the config.
	public static int NumberOfPreferredNeighbors;
	public static int UnchokingInterval;
	public static int OptimisticUnchokingInterval;
	public static String FileName;  //
	public static int FileSize; // the FileSize and PieceSize should be changed based on Thefile.dat.
	public static int PieceSize;
	public static int peerIdr; // the tail 'r' means that this data was read from cfg.
	public static String hostNamer;
	public static int portNumberr;
	public volatile BitField bitFieldr; //BitField is class which need to be implement.
	
	
	//why i use volatile, because in jmm, each time when i read or write this kind 
	//of data type, the thread would read-load or store-write this data, so the
	// volatile type data would be percepted by each thread.
	public volatile List<peerInfo> peerList = new ArrayList<peerInfo>();
	public volatile peerInfo myInfo;
	
	public void readCommon() throws IOException
	{// read common
		FileReader fr = new FileReader("Common.cfg");
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		String[] arrs = null;
		while((line = br.readLine())!=null)
		{
			arrs = line.split(" ");
			switch(arrs[0])
			{
				case "NumberOfPreferredNeighbors":
					NumberOfPreferredNeighbors=Integer.parseInt(arrs[1]);
					break;
                case "UnchokingInterval":
                	UnchokingInterval = Integer.parseInt(arrs[1]);
                    break;
                case "OptimisticUnchokingInterval":
                	OptimisticUnchokingInterval = Integer.parseInt(arrs[1]);
                    break;
                case "FileName":
                	FileName = arrs[1];
                    break;
                case "FileSize":
                	FileSize = Integer.parseInt(arrs[1]);
                    break;
                case "PieceSize":
                	PieceSize = Integer.parseInt(arrs[1]);
                    break;
			}
		}
		br.close();
	}
	
	public void readpeerInfo(int peerNumber) throws IOException
	{//read peerInfo.cfg
		FileReader fr = new FileReader("PeerInfo.cfg");
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		String[] arrs = null;
		while((line = br.readLine())!=null)
		{
			arrs = line.split(" ");
			peerInfo pi = new peerInfo();
			pi.peerId = Integer.parseInt(arrs[0]);
			pi.hostName = arrs[1];
			pi.portNumber = Integer.parseInt(arrs[2]);
			pi.ownthisfile = Integer.parseInt(arrs[3]) == 1;// if ownthisfile = 1, means has this file, 0 means does not have this file
			int pieceNumber;
			if(FileSize % PieceSize ==0)
			{
				pieceNumber = (int)(FileSize/PieceSize);
			}
			else
			{
				pieceNumber = (int)(FileSize/PieceSize +1);
				
			}
			int peerSequence = 0;
			pi.sequence = peerSequence++;
			
			if(pi.peerId == peerNumber) 
			{
				myInfo = pi;
			}
			peerList.add(pi);
		}
		br.close();
	}
	
	
	@Override 
	public String toString()
	{// This method aims to return all the needed info if needed.
		return "";
	}
	
}
