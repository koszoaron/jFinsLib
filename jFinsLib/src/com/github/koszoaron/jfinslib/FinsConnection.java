package com.github.koszoaron.jfinslib;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import com.github.koszoaron.jfinslib.FinsMessage.MessageType;

/*
 * 
OutStream: 46 49 4e 53 00 00 00 0c 00 00 00 00 00 00 00 00 00 00 00 00 (length: 20)
InStream:  46 49 4e 53 00 00 00 10 00 00 00 01 00 00 00 00 00 00 00 ffffffef 00 00 00 01 00 00 00 00 00 00 00 00 (length: 24)
OutStream: 46 49 4e 53 00 00 00 1c 00 00 00 02 00 00 00 00 80 00 02 00 01 00 00 ef 00 01 01 02 b2 00 01 00 00 01 00 04 (length: 36)
InStream:  46 49 4e 53 00 00 00 16 00 00 00 02 00 00 00 00 ffffffc0 00 02 00 ffffffef 00 00 01 00 01 01 02 00 00 00 00 (length: 30)
OutStream: 46 49 4e 53 00 00 00 1a 00 00 00 02 00 00 00 00 80 00 02 00 01 00 00 ef 00 01 01 01 b2 00 01 00 00 01 (length: 34)
InStream:  46 49 4e 53 00 00 00 18 00 00 00 02 00 00 00 00 ffffffc0 00 02 00 ffffffef 00 00 01 00 01 01 01 00 00 00 04 (length: 32)
 * 
 * */

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
            
            byte[] inputBytes = new byte[32];
            int bytesRead = -1;
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            if (streamFromServer.available() > 0) {
                bytesRead = streamFromServer.read(inputBytes);
            }
            
            StringBuilder response = new StringBuilder();
            if (bytesRead > 0) {
                for (int i : inputBytes) {
                    response.append(String.format("%02x", i) + " ");
                }
                System.out.println("InStream:  " + response + "(length: " + bytesRead + ")");
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            FinsMessage writeMessage = new FinsMessage(0xb2, 0x01, new int[] {0x1b});
            System.out.println("OutStream: " + writeMessage.toString());
            streamToServer.write(writeMessage.getMessageBytes());            
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            if (streamFromServer.available() > 0) {
                bytesRead = streamFromServer.read(inputBytes);
            }
            
            response = new StringBuilder();
            if (bytesRead > 0) {
                for (int i : inputBytes) {
                    response.append(String.format("%02x", i) + " ");
                }
                System.out.println("InStream:  " + response + "(length: " + bytesRead + ")");
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            FinsMessage readMessage = new FinsMessage(0xb2, 0x01, 1);
            System.out.println("OutStream: " + readMessage.toString());
            streamToServer.write(readMessage.getMessageBytes());
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            if (streamFromServer.available() > 0) {
                bytesRead = streamFromServer.read(inputBytes);
            }
            
            response = new StringBuilder();
            if (bytesRead > 0) {
                for (int i : inputBytes) {
                    response.append(String.format("%02x", i) + " ");
                }
                System.out.println("InStream:  " + response + "(length: " + bytesRead + ")");
            }
//           
//            
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
