package cn.glfs.socket;

import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    static  int port = 12345;// server监听端口

    public static void main(String[] args) throws IOException {
        runWithObject();
    }

    public static void runWithObject() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        try {
            // 阻塞等待连接
            Socket clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + clientSocket.getInetAddress().getHostAddress());
            //结束客户端数据
            ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
            MyObject object = (MyObject)objectInputStream.readObject();
            System.out.println("Received object from client: " + object);
            objectInputStream.close();
            clientSocket.close();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            serverSocket.close();
        }
    }
}
