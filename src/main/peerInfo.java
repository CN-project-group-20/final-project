package main;

import java.util.*;

import message.BitField;

import java.io.*;

public class peerInfo 
{
	
	public int peerId;
	public String hostName;
	public int portNumber; // or String
	public boolean ownthisfile; // or boolean
	public int initSequence; //aims to track peer
	public volatile BitField bitField; // need help from BitField
	public int sequence; // aims to track peerid
	


	public void setBitField(BitField bitField) {
		synchronized(this.bitField) {
			this.bitField = bitField;
		}
	}
	
	public void updateBitField(int pieceIndex) {
		synchronized(this.bitField) {
			//this.bitField.updateBitField(pieceIndex);
		}
	}
	
	
	
	
	
	
	
	
	
	public String toString() {
		String peerInfoString = "peerID = " + peerId + "\n"
								+ "hostName = " + hostName + "\n"
								+ "listeningPort = " + portNumber + "\n"
								+ "bitField = " + bitField.toString() + "\n"
								+ "initSeq = " + initSequence + "\n";
		return peerInfoString;
	}
}
