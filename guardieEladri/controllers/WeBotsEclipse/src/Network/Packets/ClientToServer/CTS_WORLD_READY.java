package Network.Packets.ClientToServer;

import java.nio.ByteBuffer;

import Network.Packets.Packet;

/*
 * Pacchetto inviato dal supervisor dopo la fase di spawn
 * in modo che il server può avviare il resto dei controllori in modo che la simulazione possa proseguire
 */

public class CTS_WORLD_READY extends Packet
{
	public CTS_WORLD_READY(Packet packet, ByteBuffer buf) {
    	super(packet);
    }

    public CTS_WORLD_READY() {
        super(0, Packet.CTS_WORLD_READY);
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
