package com.github.koszoaron.jfinslib;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

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
    
    /**
     * Connects to a device.
     * 
     * @return True if the operation was successful
     */
    public boolean connect() {
        boolean success = false;
        
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
            
            
            /*
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
            disconnect();*/
        }
        
        return success;
    }
    
    /**
     * Disconnects from the connected device and closes all sockets.
     * 
     * @return True if the operation was successful
     */
    public boolean disconnect() {
        boolean success = false;
        
        if (connected) {
            if (!serverConnection.isClosed()) {
                try {
                    streamToServer.close();
                    streamFromServer.close();
                    serverConnection.close();
                    
                    success = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }                
                
                streamToServer = null;
                streamFromServer = null;
                serverConnection = null;
            }
        }
        
        return success;
    }
    
    /**
     * Sends a message to the connected device.
     * 
     * @param message The {@link FinsMessage} to send
     * @return The response from the device (in an array of integers)
     */
    public int[] sendFinsMessage(FinsMessage message) {
        int[] response = null;
        
        return response;
    }
    
    /**
     * Writes the values in the argument to the specified register of the connected device.
     * 
     * @param registerAddress The address of the register
     * @param type The type of the register
     * @param values The values to write (in an array of integers)
     * @return True if the operation was successful
     */
    public boolean writeRegister(int registerAddress, int type, int[] values) {
        boolean success = false;
        
        return success;
    }
    
    /**
     * Reads the value of a single register of the connected device.
     * 
     * @param registerAddress The address of the register
     * @param type The type of the register
     * @return The value stored in the register or {@code UNKNOWN_VALUE} if the operation was not successful
     */
    public int readRegister(int registerAddress, int type) {
        int res = Constants.UNKNOWN_VALUE;
        
        return res;
    }
}
