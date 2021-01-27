package Network;

public enum Opcodes
{
    CTS_PROVA((short) 0x000);

    private final short code;

    private Opcodes(short code) {
        this.code = code;
    }

    public short getValue() {
        return code;
    }
}