package message;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ActualMsg {
	private static int lenMsgLen=4;
	private static int lenMsgType=1;
	private static int lenPieceIndex=4;
	public static enum MsgType {
		choke,					// 0
		unchoke,				// 1
		interested,				// 2
		not_interested,			// 3
		have,					// 4
		bitfield,				// 5
		request,				// 6
		piece					// 7
	}
	
	public int msgLength;
	public MsgType msgType;
	public Payload payLoad;
	
	public ActualMsg(int MsgLength, MsgType Type) {
		this.msgLength=MsgLength;
		this.msgType=Type;
	}
	
	public ActualMsg(int MsgLength, MsgType Type, Payload PayLoad) {
		this.msgLength=MsgLength;
		this.msgType=Type;
		this.payLoad=PayLoad;
	}
	
	public void readActualMsg(InputStream input) throws IOException, InterruptedException {
		byte[] msgLengthByteArray= new byte[lenMsgLen];
		while(input.available()<lenMsgLen+lenMsgType) {
			Thread.sleep(100);
		}
		synchronized(input) {
			input.read(msgLengthByteArray,0,lenMsgLen);
		}
		ByteBuffer msgLengthByteBuffer=ByteBuffer.wrap(msgLengthByteArray);
		msgLength=msgLengthByteBuffer.getInt();
		msgType=MsgType.values()[input.read()];
		switch(msgType) {
			case have:case request:{
				byte[] payLoadByteArray= new byte[lenPieceIndex];
				input.read(payLoadByteArray,0,lenPieceIndex);
				ByteBuffer payLoadByteBuffer=ByteBuffer.wrap(payLoadByteArray);
				int pieceIndex=payLoadByteBuffer.getInt();
				payLoad=new Payload(pieceIndex);
			}
			break;
			case bitfield:{
				byte[] payLoadByteArray= new byte[msgLength-lenMsgType];
				input.read(payLoadByteArray,0,msgLength-lenMsgType);
				BitField bitfield= new BitField(payLoadByteArray);
				payLoad=new Payload(bitfield);
			}
			break;
			case piece:{
				byte[] pieceIndexByteArray= new byte[lenPieceIndex];
				input.read(pieceIndexByteArray,0,lenPieceIndex);
				ByteBuffer pieceIndexByteBuffer=ByteBuffer.wrap(pieceIndexByteArray);
				int pieceIndex=pieceIndexByteBuffer.getInt();
				byte[] contentByteArray= new byte[msgLength-lenMsgType-lenPieceIndex];
				input.read(contentByteArray,0,msgLength-lenMsgType-lenPieceIndex);
				payLoad=new Payload(pieceIndex,contentByteArray);
			}
			break;
			default:{
				//'choke', 'unchoke', 'interested', 'not interested' messages have no payload
			}
			break;
		}
		return;
	}
	
	public void writeActualMsg(OutputStream output) throws IOException, InterruptedException {
		ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream (byteArrayStream);
		
		dataStream.writeInt(msgLength);
		dataStream.writeByte((byte)msgType.ordinal());
		switch(msgType) {
			case have:case request:{
				dataStream.writeInt(payLoad.pieceIndex);
			}
			break;
			case bitfield:{
				dataStream.write(payLoad.bitfield.arrayBitfield);
			}
			break;
			case piece:{
				dataStream.writeInt(payLoad.pieceIndex);
				dataStream.write(payLoad.content);
			}
			break;
			default:{
				//'choke', 'unchoke', 'interested', 'not interested' messages have no payload
			}
			break;
		}
		byteArrayStream.flush();
		byte[] byteArray = byteArrayStream.toByteArray();
		byteArrayStream.close();
		synchronized(output) {
			output.write(byteArray);
		}
		return;
	}
}
