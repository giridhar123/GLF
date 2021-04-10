package Network.Packets.ServerToClient;

import java.nio.ByteBuffer;

/*
 * Pacchetto che utilizza il server per inviare la mappa ai client
 */

import Map.Mappa;
import Map.Point;
import Network.Packets.Packet;

public class STC_SEND_MAP extends Packet{
	
	private Mappa mappa;
	
	public STC_SEND_MAP(Mappa mappa)
	{
		// SIZE PRECEDENTI + SIZEOF (1 INT (4 byte) * DIMENSIONE MATRICE DELLA MAPPA)  
		super(16 + 8 + 4 + 4 + 8 + 8 + 4 * (mappa.getXSize() * mappa.getYSize()), Packet.STC_SEND_MAP);
		this.mappa = mappa;
	}
	
	public STC_SEND_MAP(Packet packet, ByteBuffer buf) {
    	super(packet);
    	
    	double arrayXY[] = new double[2];
    	arrayXY[0] =  buf.getDouble();
    	arrayXY[1] = buf.getDouble();
    	
    	int xAmpiezzaSpawn = buf.getInt();
    	int xDimInterna = buf.getInt();
    	int yDimInterna = buf.getInt();
    	int dimSpawnGate = buf.getInt();
    	double spiazzamentoX = buf.getDouble();
    	double spiazzamentoY = buf.getDouble();
    	
    	int xTot = xDimInterna + 2 * xAmpiezzaSpawn;
    	int[][] mappa = new int[xTot][yDimInterna];
    	for (int i = 0; i < xTot; ++i)
    		for(int j = 0; j < yDimInterna; ++j)
    			mappa[i][j] = buf.getInt();
    	
    	this.mappa = new Mappa(mappa, xAmpiezzaSpawn, xDimInterna, yDimInterna, dimSpawnGate, arrayXY, spiazzamentoX, spiazzamentoY); 
  
    }
	
	public Mappa getMappa()
	{
		return mappa;
	}

	@Override
    public ByteBuffer encode()
    {
    	ByteBuffer buf = ByteBuffer.allocate(getSize());
    	buf.putInt(getSize());
    	buf.putShort(this.getOpcode());
    	
    	buf.putDouble(mappa.getFloorSize()[0]);
    	buf.putDouble(mappa.getFloorSize()[1]);
    	buf.putInt(mappa.getxAmpiezzaSpawn());
    	buf.putInt(mappa.getXDimInterna());
    	buf.putInt(mappa.getYDimInterna());
    	buf.putInt(mappa.getDimSpawnGate());
    	buf.putDouble(mappa.getSpiazzamentoX());
    	buf.putDouble(mappa.getSpiazzamentoY());
    	
    	int xTot = mappa.getXDimInterna() + 2 * mappa.getXAmpiezzaSpawn();
    	for (int i = 0; i < xTot; ++i)
    	{
    		for(int j = 0; j < mappa.getYDimInterna(); ++j)
    		{
    			buf.putInt(mappa.get(new Point(i, j)));
    		}
    	}
    	
        buf.position(0);
        return buf;
    }
}
