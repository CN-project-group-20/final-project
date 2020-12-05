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
	
	public int insertPiece(int pieceIndex, byte[] bytes) throws IOException
	{
		pieceInfo pi = new pieceInfo(pieceIndex, bytes.length);
		int newpiecelength = newPiece(pi);
		if(newpiecelength==-1)
			return newpiecelength;//the -1 aims to rec again.
		File file = new File(filePath);//volatile
		if(file.exists() && file.isFile())
		{
			FileInputStream in = new FileInputStream(filePath);
			BufferedInputStream br = new BufferedInputStream(in);
			byte[] byteOfFile = new byte[(int)file.length()];
			br.read(byteOfFile);
			br.close();
		}
		
		//need to be implement
		return 0;//here is not right 
		
	}
	
	public byte[] getPiece(int pieceIndex) throws IOException
	{//感觉这里写错了
		int a = 0;
		for (pieceInfo peer: pieceInfoList)
		{
			if(peer.pieceIndex ==pieceIndex)
			{
				byte[] piece = null;
				File file = new File(filePath);
				FileInputStream in = new FileInputStream(filePath);
				BufferedInputStream  br  = new BufferedInputStream (in);
				byte[] byteOfFile = new byte[(int)file.length()];
				br.read(byteOfFile);
				br.close();
				piece = Arrays.copyOfRange(byteOfFile, a, a + peer.pieceLength);
				return piece;
			}
			a = a + peer.pieceLength;
		}
		return null;
	}

	
}
