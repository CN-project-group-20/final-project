package message;


public class Payload {
	public int pieceIndex;
	public BitField bitfield;
	public byte[] content;
	
	//'choke', 'unchoke', 'interested', 'not interested' messages have no payload
	public Payload() {}
	
	//'have', 'request' message has a payload that contains a 4-byte piece index field
	public Payload(int pieceIndex) {
		this.pieceIndex = pieceIndex;
	}
	
	//'bitfield' message has a bitfield as its payload
	public Payload(BitField bitfield) {
		this.bitfield = bitfield;
	}
	
	//piece message has a payload  that contains a 4-byte piece index field and the content of the piece
	public Payload(int pieceIndex, byte[] content) {
		this.pieceIndex = pieceIndex;
		this.content = content;
	}
}
