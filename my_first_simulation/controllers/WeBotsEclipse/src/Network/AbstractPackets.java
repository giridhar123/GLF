package Network;

import java.nio.ByteBuffer;

public abstract class AbstractPackets
{
    protected Opcodes opcode;
    
    public AbstractPackets(final Opcodes opcode){
        this.opcode = opcode;
    }
    
    public Opcodes getOpcode(){
        return this.opcode;
    }
    
    public void setOpcode(final Opcodes opcode){
        this.opcode = opcode;
    }

    public abstract void decode(ByteBuffer buf) throws Exception;
    public abstract ByteBuffer encode() throws Exception;
}