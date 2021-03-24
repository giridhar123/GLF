package Network.Packets.ServerToClient;

import java.nio.ByteBuffer;

import Map.Mappa;
import Map.Point;
import Network.Packet;

public class STC_SEND_MAP extends Packet{
	
	private Mappa mappa;
	private int xAmpiezzaSpawn, xDimInterna, yDimInterna; //SIZE: 2 * 4 byte
	private double getWeBotsXYMap[]; //SIZE: 2 * 8 byte
	
	public STC_SEND_MAP(Mappa mappa)
	{
		// SIZE PRECEDENTI + SIZEOF (1 INT (4 byte) * DIMENSIONE MATRICE DELLA MAPPA)  
		super(16 + 8 + 4 + 4 * (mappa.getXSize() * mappa.getYSize()), Packet.STC_SEND_MAP);
		this.xAmpiezzaSpawn = mappa.getXAmpiezzaSpawn();
		this.xDimInterna = mappa.getXDimInterna();
		this.yDimInterna = mappa.getYDimInterna();
		this.getWeBotsXYMap = mappa.getWeBotsXYMap();
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
    	
    	int xTot = xDimInterna + 2 * xAmpiezzaSpawn;
    	int[][] mappa = new int[xTot][yDimInterna];
    	for (int i = 0; i < xTot; ++i)
    		for(int j = 0; j < yDimInterna; ++j)
    			mappa[i][j] = buf.getInt();
    	
    	this.mappa = new Mappa(mappa, xAmpiezzaSpawn, xDimInterna, yDimInterna , arrayXY); 
  
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
    	
    	buf.putDouble(getWeBotsXYMap[0]);
    	buf.putDouble(getWeBotsXYMap[1]);
    	buf.putInt(xAmpiezzaSpawn);
    	buf.putInt(xDimInterna);
    	buf.putInt(yDimInterna);
    	
    	int xTot = xDimInterna + 2 * xAmpiezzaSpawn;
    	for (int i = 0; i < xTot; ++i)
    	{
    		for(int j = 0; j < yDimInterna; ++j)
    		{
    			buf.putInt(mappa.get(new Point(i, j)));
    		}
    	}
    	
        buf.position(0);
        return buf;
    }
}
