package Network.Client;
import java.nio.ByteBuffer;

import Network.AbstractPackets;
import Network.Opcodes;

public class CTS_PROVA extends AbstractPackets 
{
    private short size;
    private byte num;
    private String frase;

    public CTS_PROVA(Opcodes opcode, short size) {
        super(opcode);
        this.size = size;
    }

    public short getSize() {
        return size;
    }

    public void setSize(short size) {
        this.size = size;
    }

    public byte getNum() {
        return num;
    }

    public void setNum(byte num) {
        this.num = num;
    }

    public String getFrase() {
        return frase;
    }

    public void setFrase(String frase) {
        this.frase = frase;
    }

    public String toString() {
        String toString = "toString";
        
        return toString;
    }

    @Override
    public void decode(ByteBuffer buf) throws Exception {
        /*
        if (buf.readableBytes() < this.size) {
            throw new Exception();
        }

        buf.readShort();
        this.build = buf.readInt();
        this.unknown = buf.readInt();

        StringBuilder b = new StringBuilder();

        byte c;

        while ((c = buf.readByte()) != 0) {
            b.append((char) c);
        }

        this.account = b.toString();
        this.seed = buf.readBytes(4).array();
        this.digest = buf.readBytes(20).array();

        int sizeAddons = buf.readInt();

        byte[] compressedAddons = buf.readBytes(buf.readableBytes()).array();

        Inflater decompressor = new Inflater();
        decompressor.setInput(compressedAddons);

        ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedAddons.length);

        final byte[] buffer = new byte[1024];
        while (!decompressor.finished()) {
            try {
                final int count = decompressor.inflate(buffer);
                bos.write(buffer, 0, count);
            } catch (final DataFormatException e) {
            }
        }
        try {
            bos.close();
        } catch (final IOException e) {
        }

        final byte[] decompressedAddons = bos.toByteArray();

        if (sizeAddons != decompressedAddons.length) {
            System.out.println("Something went wrong");
            return;
        }

        ByteBuf addonBuffer = Unpooled.wrappedBuffer(decompressedAddons).order(ByteOrder.LITTLE_ENDIAN);

        this.listAddon = new ArrayList<>();
        
        while (addonBuffer.isReadable()) {            
            String name;
            byte unk6;
            int crc, unk7;
            
            b = new StringBuilder();
            while ((c = addonBuffer.readByte()) != 0) {
                b.append((char) c);
            }

            name = b.toString();                            
            
            crc = addonBuffer.readInt();
            unk7 = addonBuffer.readInt();
            unk6 = addonBuffer.readByte();           
            
            AddonInfo info = new AddonInfo(name, unk6, crc, unk7);
            this.listAddon.add(info);
            */
        }
        
        @Override
        public ByteBuffer encode() throws Exception {
        	ByteBuffer buf = ByteBuffer.allocate(size);
            buf.putShort(this.size);
            buf.putShort(this.opcode.getValue());
            buf.position(0);
            return buf;
        }
    }