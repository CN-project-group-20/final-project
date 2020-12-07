package main;

import java.util.*;
import java.io.*;
import message.BitField;

public class peerInfo 
{
	
	public int peerId;
	public String hostName;
	public int portNumber; // or String
	public boolean ownthisfile; // or boolean
	public int initSequence; //aims to track peer
	public volatile BitField bitField; // need help from BitField
	public int sequence; // aims to track peerid
	
	public boolean initBefore(peerInfo server)
	{
		if(sequence < server.sequence)
			return true;
		else
			return false;
	}

	public void setBitField(BitField bitField) {
		synchronized(this.bitField) {
			this.bitField = bitField;
		}
	}
	
	public void updateBitField(int pieceIndex) {
		synchronized(this.bitField) {
			this.bitField.pieceUpdate(pieceIndex);
		}
	}
	
	public boolean checkInterested(peerInfo server)
	{
		boolean interested;
		synchronized(bitField)
		{
			interested = bitField.checkInterested(server.bitField);
		}
		return interested;
	}
	
	public boolean checkInterested(peerInfo server, Set<Integer> x)
	{//need to be discussed.
		boolean interested;
		synchronized(bitField)
		{
			interested = bitField.checkInterested(server.bitField, x);
		}
		return interested;
	}
	
	@Override

	
	
	
	
	
	
	
	
	public String toString() {
		String peerInfoString = "peerID = " + peerId + "\n"
								+ "hostName = " + hostName + "\n"
								+ "listeningPort = " + portNumber + "\n"
								+ "bitField = " + bitField.toString() + "\n"
								+ "initSeq = " + initSequence + "\n";
		return peerInfoString;
	}
}
