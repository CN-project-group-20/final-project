package message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class HandshakeMsg {
	private static int lenMsg=32;
	private static int lenHsh=18;
	private static int lenZbs=10;
	private static int lenPid=4;
	
	private static String contentHandshakeHeader="P2PFILESHARINGPROJ";
	private static byte[] contentZeroBits={0,0,0,0,0,0,0,0,0,0};
	
	public String handshakeHeader;
	public byte[] zeroBits;
	public int peerId;
	
	public HandshakeMsg(int peerID) {
		zeroBits= new byte[lenZbs];
		this.handshakeHeader=contentHandshakeHeader;
		this.zeroBits=contentZeroBits;
		this.peerId=peerID;
	}
	
	public boolean checkRightNeighbor(int expectedID) {
		return this.handshakeHeader==contentHandshakeHeader 
				&& this.peerId==expectedID;
	}
	
	public void readHandshakeMsg(InputStream input) throws IOException, InterruptedException {
		byte[] HandshakeByteArray= new byte[lenMsg];
		while(input.available()!=lenMsg) {
			Thread.sleep(100);
		}
		synchronized(input) {
			input.read(HandshakeByteArray,0,lenMsg);
		}
		handshakeHeader= new String(Arrays.copyOfRange(HandshakeByteArray, 0, lenHsh));
		zeroBits=Arrays.copyOfRange(HandshakeByteArray, lenHsh, lenHsh+lenZbs);
		byte[] peerIdByteArray=new byte[lenPid];
		peerIdByteArray=Arrays.copyOfRange(HandshakeByteArray, lenHsh+lenZbs, lenMsg);
		ByteBuffer peerIdByteBuffer=ByteBuffer.wrap(peerIdByteArray);
		peerId=peerIdByteBuffer.getInt();
		return;
	}
	
	public void writeHandshakeMsg(OutputStream output) throws IOException {
		ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream (byteArrayStream);
		
		dataStream.writeBytes(handshakeHeader);
		dataStream.write(zeroBits);
		dataStream.writeInt(peerId);
		byteArrayStream.flush();
		byte[] byteArray = byteArrayStream.toByteArray();
		byteArrayStream.close();
		synchronized(output) {
			output.write(byteArray, 0, lenMsg);
		}
		return;
	}
}
