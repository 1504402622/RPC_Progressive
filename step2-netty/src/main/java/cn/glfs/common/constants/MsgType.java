package cn.glfs.common.constants;

/**
 * 消息类型
 */
public enum MsgType {
    REQUEST,// 请求
    RESPONSE,// 响应
    HEARTBAT;// 心跳类型
    public static MsgType findByType(int type) {
        return MsgType.values()[type];
    }
}
