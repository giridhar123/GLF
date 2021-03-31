package Network.Packets.ServerToClient;

import java.nio.ByteBuffer;

import Network.Packets.Packet;

public class STC_START_GUARDIE extends Packet
{
	public STC_START_GUARDIE(Packet packet, ByteBuffer buf) {
    	super(packet);
    }

    public STC_START_GUARDIE() {
        super(0, Packet.STC_START_GUARDIE);
    }
    
    @Override
    public ByteBuffer encode()
    {
    	ByteBuffer buf = ByteBuffer.allocate(getSize());
    	buf.putInt(getSize());
    	buf.putShort(this.getOpcode());
        buf.position(0);
        return buf;
    }

}
