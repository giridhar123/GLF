package Network.Server;

import java.nio.ByteBuffer;

import Map.Mappa;
import Network.Packet;

public class STC_SEND_MAP extends Packet{
	
	private Mappa mappa;
	
	public STC_SEND_MAP(Mappa mappa)
	{
		// 16 
		super(16 + 8 + 4 * (mappa.getXSize() * mappa.getYSize()), Packet.STC_SEND_MAP);
		this.mappa = mappa;
	}
	
	public STC_SEND_MAP(Packet packet, ByteBuffer buf) {
    	super(packet);
    	
    	double arrayXY[] = new double[2];
    	arrayXY[0] =  buf.getDouble();
    	arrayXY[1] = buf.getDouble();
    	
    	int xDim = buf.getInt();
    	int yDim = buf.getInt();
    	int[][] mappa = new int[xDim][yDim];
    	for (int i = 0; i < xDim; ++i)
    		for(int j = 0; j < yDim; ++j)
    			mappa[i][j] = buf.getInt();
    	
    	this.mappa = new Mappa(mappa, xDim, yDim , arrayXY); 
  
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
    	buf.putDouble(mappa.getWeBotsXYMap()[0]);
    	buf.putDouble(mappa.getWeBotsXYMap()[1]);
    	buf.putInt(mappa.getXSize());
    	buf.putInt(mappa.getYSize());
    	for (int i = 0; i < mappa.getXSize(); ++i)
    		for(int j = 0; j < mappa.getYSize(); ++j)
    			buf.putInt(mappa.get(i, j));
        buf.position(0);
        return buf;
    }
}
