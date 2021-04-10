package Network.Packets.ClientToServer;
import java.nio.ByteBuffer;

import Network.Packets.Packet;

/*
 * Pacchetto inviato da un client quando si connette con il server
 * in modo che quest'ultimo possa identificare il tipo di client che si è appena connesso
 */

public class CTS_PEER_INFO extends Packet 
{	
    private byte type;
    
    public static final byte GUARDIA = 1;
    public static final byte LADRO = 2;
    public static final byte SUPERVISOR = 3;
    
    public CTS_PEER_INFO(Packet packet, ByteBuffer buf) {
    	super(packet);
    	this.type = buf.get();
    }

    public CTS_PEER_INFO(byte type) {
        super(1, Packet.CTS_PEER_INFO);
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String toString() {
        String toString = "toString";
        
        return toString;
    }
        
    @Override
    public ByteBuffer encode()
    {
    	ByteBuffer buf = ByteBuffer.allocate(getSize());
    	buf.putInt(getSize());
    	buf.putShort(this.getOpcode());
    	buf.put(this.type);
        buf.position(0);
        return buf;
    }
}