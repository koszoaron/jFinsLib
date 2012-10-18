package com.github.koszoaron.jfinslib;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import com.github.koszoaron.jfinslib.FinsMessage.MessageType;

public class FinsConnection {
    public static final int HEX_REGISTER = 0;
    public static final int BCD_REGISTER = 1;  //TODO convert to enum
    
    private String serverAddress;
    private int serverPort;
    private Socket serverConnection;
    private boolean connected = false;
    private int timeout = 30;
    
    private DataOutputStream streamToServer;
    private InputStream streamFromServer;
    
    private FinsConnection(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }
    
    public static FinsConnection newInstance(String address, int port) {
        return new FinsConnection(address, port);
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void connect() throws IOException, SocketTimeoutException {
        if (!connected) {
            SocketAddress address = new InetSocketAddress(serverAddress, serverPort);
            serverConnection = new Socket();
            serverConnection.connect(address, timeout);
            
            connected = true;
            
            streamToServer = new DataOutputStream(serverConnection.getOutputStream());
            streamFromServer = serverConnection.getInputStream();
            FinsMessage connectMessage = new FinsMessage();
            
            System.out.println("OutStream: " + connectMessage.toString());
            streamToServer.write(connectMessage.getMessageBytes());
            
            
            System.out.print("Reading: ");
            while (streamFromServer.available() > 0) {
                System.out.print(streamFromServer.read());
            }
            System.out.println();
            
            FinsMessage writeMessage = new FinsMessage(0xb2, 0x01, new int[] {0x08});
            System.out.println("OutStream: " + writeMessage.toString());
            streamToServer.write(writeMessage.getMessageBytes());
            
            System.out.print("Reading: ");
            while (streamFromServer.available() > 0) {
                System.out.print(streamFromServer.read());
            }
            System.out.println();
            
            streamToServer.flush();
            
            disconnect();
        }
    }
    
    public void disconnect() throws IOException {
        if (connected) {
            if (!serverConnection.isClosed()) {
                streamToServer.close();
                streamFromServer.close();
                serverConnection.close();
                
                streamToServer = null;
                streamFromServer = null;
                serverConnection = null;
            }
        }
    }
    
    public void writeRegister(int registerAddress, int type) {
        //TODO
    }
    
    public void sendFinsMessage(MessageType type, int memoryArea, int register, int[] values) {
        //TODO
    }
    
    public void sendFinsMessage(FinsMessage message) {
        //TODO
    }
}
