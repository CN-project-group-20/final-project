package file;

import java.io.*;
import java.util.*;


class pieceInfo
{//more like a struct
	public int pieceIndex;
	public int pieceLength;
	pieceInfo(int pieceIndex,int pieceLength)
	{
		this.pieceIndex = pieceIndex;
		this.pieceLength = pieceLength;
	}
}

public class readFile 
{
	public volatile String filePath;
	public volatile List<pieceInfo> pieceInfoList = new ArrayList<>();
	public volatile int numOfPiece;
	
	private int newPiece(pieceInfo pi)
	{
		int piecelength = 0;
		int pieceIndex = 0;
		for (pieceInfo peer: pieceInfoList)
		{
			if(peer.pieceIndex<pi.pieceIndex)
			{
				piecelength = piecelength+peer.pieceLength;
				pieceIndex++;
			}
			else if(peer.pieceIndex==pi.pieceIndex) 
			{
				System.out.print("aims to rec");
				return -1;
			}
			else {
				System.out.print("beyound range!");
				
				break;
			}
			synchronized(pieceInfoList)
			{
				pieceInfoList.add(pieceIndex,pi);
			}
			
		}
		return piecelength;
	}
	
	
	
	public readFile(String FilePath, int ownThisFile) 
	{
		
		filePath = FilePath;
		if (ownThisFile+1==1)
		{
			System.out.printf("Do not own this File.");
		}
		if(readConfig.FileSize % readConfig.PieceSize==0)
		{
			numOfPiece = readConfig.FileSize/readConfig.PieceSize;
		}
		else
		{
			numOfPiece = readConfig.FileSize/readConfig.PieceSize+1;
		}
		int tailSize = 0;
		if(readConfig.FileSize % readConfig.PieceSize!=0) 
		{
			tailSize = readConfig.FileSize % readConfig.PieceSize;
		}
		for(int i = 0; i<numOfPiece-1; ++i)
		{
			pieceInfo pi = new pieceInfo(i,readConfig.PieceSize);
			pieceInfoList.add(pi);
		}
		pieceInfo piTail = new pieceInfo(numOfPiece - 1,tailSize);
		pieceInfoList.add(piTail);
	}
	
	public int insertPiece(int insertPieceIndex, byte[] insertedPiece) throws IOException
	{
		/* Sun: @12月5日 19：53 
		 * change all of the implemnet of insertPiece
		 * */
		int insertPosition = 0;
		int insertIndex = 0;
		for(i=0;i<pieceInfoList.size();i++)
		{
			if(pieceInfoList.get(i).pieceIndex<insertPieceIndex)
			{
				insertPosition = insertPosition + pieceInfoList.get(i).pieceLength;
				insertIndex++;
			}
			else if(pieceInfoList.get(i).pieceIndex==insertPieceIndex) 
			{
				System.out.println("This piece has already been inserted before.");
			}
			else 
			{
				System.out.println("The insert piece's index has some wrongs.");
				break;
			}
		}
		pieceInfo temppi = new pieceInfo(insertPieceIndex, insertedPiece.length);
		synchronized(pieceInfoList)
		{
			
			pieceInfoList.add(insertIndex, temppi);
		}
		/* Then insert the insertedPiece after finding the insertPosition */
		File file = new File(filePath);
		if(!file.exists())
		{
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(filePath);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(bytesInserted);
			bos.close();
			//
			byte[] afterInsert = insertByteArray(byteStream, insertedPiece, insertPosition);
			if(afterInsert == null) {
				return -1;
			}
			
			//Write the String back to the file
			FileOutputStream fos = new FileOutputStream(filePath);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(afterInsert);
			bos.close();
		}
		else
		{
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] byteStream = new byte[(int)file.length()];
			bis.read(byteStream, 0, byteStream.length);
			bis.close();
		}
		return insertPieceIndex;
	}
	
	private byte[] insertByteArray(byte[] oldBytes, byte[] insertedBytes, int insertPosition) 
	{/* Sun: @12月6日 19：31 
		 * 
		 * */
		if(insertPosition > oldBytes.length)
		{
			System.out.println("The insert position beyond the max length of the old bytes--readFile.java");
			return null;
		}
			
		byte[] afterInsert = new byte[oldBytes.length + insertedBytes.length];
		System.arraycopy(oldBytes, 0, afterInsert, 0, insertPosition);//put old bytes into afterbytes without any changes.
		System.arraycopy(insertedBytes, 0, afterInsert, insertPosition, insertedBytes.length);//put the bytes which need to be inserted into afterbytes.
		System.arraycopy(oldBytes, insertPosition, afterInsert, insertPosition + insertedBytes.length, oldBytes.length - insertPosition);//insert the remnant bytes after the insetedBytes.
		return afterInsert;
	}
	
	public byte[] getPiece(int pieceIndex) throws IOException
	{/* Sun: @12月6日 19：31 
		 * 
		 * */
		int offset = 0;
		for(pieceInfo pi : pieceInfoList) {
			if(pi.pieceIndex == pieceIndex) {
				return getPiece(offset, pi.length);
			}
			offset += pi.length;
		}
		return null;
	}
	
	private byte[] getPiece(int offset, int length) throws IOException
	{/* Sun: @12月6日 19：31 
		 * 
		 * */
		byte[] piece = null;
		File file = new File(filePath);
		FileInputStream fis = new FileInputStream(filePath);
		BufferedInputStream  bis  = new BufferedInputStream (fis);
		byte[] byteStream = new byte[(int)file.length()];
		bis.read(byteStream);
		bis.close();
		piece = Arrays.copyOfRange(byteStream, offset, offset + length);
		return piece;
	}

	
}
