package Network;

import java.nio.ByteBuffer;

public class Packet
{
	public static final short CTS_PEER_INFO = (short) 0x00FF;
	
    private short opcode;
    private int size;
    
    public Packet(Packet otherPacket)
    {
    	this.opcode = otherPacket.opcode;
    	this.size = otherPacket.size;
    }
    
    public Packet(ByteBuffer buf)
    {
    	this.opcode = buf.getShort();
    }
    
    public Packet(final short opcode){
    	this.size = 4 + 2;
        this.opcode = opcode;
    }
    
    public Packet(final int size, final short opcode){
    	this(opcode);
    	this.size += size;
        this.opcode = opcode;
    }
    
    public int getSize()
    {
    	return size;
    }
    
    public short getOpcode(){
        return this.opcode;
    }
    
    public void setOpcode(final short opcode){
        this.opcode = opcode;
    }

    public ByteBuffer encode()
    {
    	return null;
    }
}