package message;

import java.util.ArrayList;

public class BitField {
	public static int byteSize = 8;
	public static byte maxByte = -1; //11111111     
	public static byte minByte = 0; //00000000

	public byte[] arrayBitfield;
	public static int pieceNum;
	
	public BitField(int pieceNum) {
		this(pieceNum, false); //HasFile = false
	}
	
	public BitField(byte[] ArrayOfBitfield) {
		this.arrayBitfield = ArrayOfBitfield;
	}
	
	
	public BitField(int PieceNum, boolean HasFile) {
		pieceNum=PieceNum;
		int tail= pieceNum % byteSize;
		int pieceSizeOfByte;
		int i;
		if(tail==0) {
			tail=byteSize;
			pieceSizeOfByte=pieceNum / byteSize;
		}
		else
			pieceSizeOfByte=pieceNum / byteSize + 1;
		arrayBitfield=new byte[pieceSizeOfByte];
		if(HasFile) {
			for(i=0;i<pieceSizeOfByte-1;i++)
				arrayBitfield[i]=maxByte;
			for(i=0;i<byteSize;i++)
				arrayBitfield[pieceSizeOfByte-1]=(byte)((int)arrayBitfield[pieceSizeOfByte-1]*2+(i<tail?1:0));
		}
		else {
			for(i=0;i<pieceSizeOfByte;i++)
				arrayBitfield[i]=minByte;
		}
	}
	
	public boolean checkInterested(BitField serverBitField) {
		for(int i=0; i<arrayBitfield.length; i++) {
			String s1=getBit(arrayBitfield[i]);
			String s2=getBit(serverBitField.arrayBitfield[i]);
			for(int j=0;j<byteSize;j++) {
				if(s1.charAt(j) == '0' && s2.charAt(j) == '1')
					return true;
			}
		}
		return false;
	}
	
	public boolean checkCompleted() {
		int tail= pieceNum % byteSize;
		if(tail==0)
			tail=byteSize;
		for(int i=0;i<arrayBitfield.length-1;i++) {
			if(arrayBitfield[i]!=maxByte) 
				return false;
		}
		String s=getBit(arrayBitfield[arrayBitfield.length-1]);
		for(int i=0;i<tail;i++) {
			if(s.charAt(i) != '1')
				return false;
		}
		return true;
	}
	
	public boolean checkIfInterested(BitField serverBitField, Set<Integer> inFlightSet) {
		String clientBitString = this.toString();
		String serverBitString = serverBitField.toString();
		for(int i=0; i<clientBitString.length(); i++) {
			if(clientBitString.charAt(i) == '0' && serverBitString.charAt(i) == '1' && !inFlightSet.contains(i))
				return true;
		}
		return false;
	}
	
	public int pieceRandomRequest(BitField serverBitField) {
		ArrayList<Integer> arrayRequest= new ArrayList<>();
		for(int i=0; i<arrayBitfield.length; i++) {
			String s1=getBit(arrayBitfield[i]);
			String s2=getBit(serverBitField.arrayBitfield[i]);
			for(int j=0;j<byteSize;j++) {
				if(s1.charAt(j) == '0' && s2.charAt(j) == '1')
					arrayRequest.add(i*byteSize+j);
			}
		}
		if(arrayRequest.size()==0)
			System.out.println("The BitField/pieceRandomRequest/arrayRequest.size = 0");
			return -1;
		int randomIndex=(int)(Math.random()*arrayRequest.size());
		return arrayRequest.get(randomIndex);
	}
	
	public void pieceUpdate(int offset) {
		int i=offset/byteSize;
		int j=offset%byteSize;
		String s=getBit(arrayBitfield[i]);
		String s2;
		if(j==0)
			s2='1'+s.substring(1, byteSize);
		else if(j==7)
			s2=s.substring(0, 7)+'1';
		else
			s2=s.substring(0, j)+'1'+s.substring(j+1, byteSize);
		arrayBitfield[i]=bitToByte(s2);
	}
	
	public boolean pieceCheck(int offset) {
		int i=offset/byteSize;
		int j=offset%byteSize;
		String s=getBit(arrayBitfield[i]);
		return s.charAt(j)=='1';	
	}
	
	public static String getBit(byte by){
		StringBuffer sb = new StringBuffer();
		sb.append((by>>7)&0x1)
		 .append((by>>6)&0x1)
		 .append((by>>5)&0x1)
		 .append((by>>4)&0x1)
		 .append((by>>3)&0x1)
		 .append((by>>2)&0x1)
		 .append((by>>1)&0x1)
		 .append((by>>0)&0x1);
		return sb.toString();
		}
	
	public static byte bitToByte(String bit) {  
        int re, len;  
        if (null == bit) {  
            return 0;  
        }  
        len = bit.length();  
        if (len != 8) {  
            return 0;  
        }  
        if (bit.charAt(0) == '0')
        	re = Integer.parseInt(bit, 2);  
        else
        	re = Integer.parseInt(bit, 2) - 256;  
        return (byte) re;  
    }
	
	@Override
	public String toString() {
		String bitFieldString = new String();
		for(byte b : bitFieldArray)
			bitFieldString += byteToString(b);
		return bitFieldString;
	}
}
