package com.github.koszoaron.jfinslib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    
    private PrintWriter streamToServer;
    private BufferedReader streamFromServer;
    
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
            
            streamToServer = new PrintWriter(serverConnection.getOutputStream());
            streamFromServer = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
            
            //TODO FINS connect, return bool
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
