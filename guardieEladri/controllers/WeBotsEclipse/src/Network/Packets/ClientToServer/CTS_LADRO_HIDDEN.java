package Network.Packets.ClientToServer;

import java.nio.ByteBuffer;

import Network.Packets.Packet;

/*
 * Pacchetto inviato da un ladro quando raggiunge il punto in cui si deve nascondere
 */

public class CTS_LADRO_HIDDEN extends Packet
{
	public CTS_LADRO_HIDDEN(Packet packet, ByteBuffer buf) {
    	super(packet);
    }

    public CTS_LADRO_HIDDEN() {
        super(0, Packet.CTS_LADRO_HIDDEN);
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
