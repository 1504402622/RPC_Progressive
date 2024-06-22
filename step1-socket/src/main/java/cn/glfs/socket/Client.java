package cn.glfs.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    static String hostName = "localhost"; // 服务端的主机名或IP地址
    static int port = 12345; // 服务端监听的端口
    public static void main(String[] args) throws IOException {
        runWithObject();
    }
    public static void runWithObject() throws IOException {
        Socket socket = new Socket(hostName, port);
        MyObject myObject = new MyObject("Hello", "World");
        //将对象转为对象输出流
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(myObject);
        objectOutputStream.close();
        socket.close();
    }
}
