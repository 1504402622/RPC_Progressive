package cn.glfs.common.constants;

/**
 * 值对象：协议头长度、魔数、版本号、
 */
public class ProtocolConstants {
    public static final int HEADER_TOTAL_LEN = 18;
    public static final short MAGIC = 0x10;
    public static final byte VERSION = 0x1;
}
