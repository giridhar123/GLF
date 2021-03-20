package Network;

import java.nio.ByteBuffer;

public class Packet
{
	public static final short CTS_PEER_INFO = (short) 0x0000;
	public static final short STC_SEND_MAP = (short) 0x0001;
	public static final short CTS_WORLD_READY = (short) 0x0002;
	public static final short CTS_UPDATE_MAP_POINT = (short) 0x0003;
	
	private int size;
    private short opcode;
    
    public Packet(Packet otherPacket)
    {
    	this.opcode = otherPacket.opcode;
    	this.size = otherPacket.size;
    }
    
    public Packet(int size, ByteBuffer buf)
    {
    	this.size = size;
    	this.opcode = buf.getShort();
    }
    
    public Packet(final short opcode)
    {
    	this.size = 4 + 2; //SIZE OF INT + SIZE OF SHORT
        this.opcode = opcode;
    }
    
    public Packet(final int size, final short opcode)
    {
    	this(opcode);
    	this.size += size;
        this.opcode = opcode;
    }
    
    public int getSize()
    {
    	return size;
    }
    
    public short getOpcode()
    {
        return this.opcode;
    }
    
    public void setOpcode(final short opcode)
    {
        this.opcode = opcode;
    }

    public ByteBuffer encode()
    {
    	return null;
    }
}