package com.github.koszoaron.jfinslib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class FinsConnection {
    public static final int HEX_REGISTER = 0;
    public static final int BCD_REGISTER = 1;
    
    public static final int MSGTYPE_CONNECT = 0;
    public static final int MSGTYPE_READMEM = 1;
    public static final int MSGTYPE_WRITEMEM = 2;
    
    //                                             FINS..................  message length........  command.......................................  connect...............
    public static final int dataConnection[] =    {0x46, 0x49, 0x4E, 0x53, 0x00, 0x00, 0x00, 0x0C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    //                                             FINS..................  message length........  command.......................................  ICF   RSV   GCT   DNA   DA1   DA2   SNA   SA1   SA2   SID   write mem.  area  num reg...  rsv.  register..  value1....  value2....  value3....    
    public static final int dataTripleCommand[] = {0x46, 0x49, 0x4E, 0x53, 0x00, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x80, 0x00, 0x03, 0x00, 0x01, 0x00, 0x00, 0xFB, 0x00, 0x01, 0x01, 0x02, 0xb2, 0x00, 0x07, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    
    public static final int[] FINS_HEADER = {0x46, 0x49, 0x4e, 0x53};
    public static final int[] FINS_LENGTH_PLACEHOLDER = {0x00, 0x00, 0x00, 0x00};
    
    public static final int[] FINS_COMMAND_CONNECT = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    public static final int[] FINS_COMMAND_GENERIC = {0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00};
    
    public static final int[] FINS_CONNECT = {0x00, 0x00, 0x00, 0x00};
    
    public static final int ICF_COMMAND = 0x80;
    public static final int ICF_RESPONSE = 0xC0;
    public static final int RSV = 0x00;  //fixed
    public static final int GCT = 0x02;  //fixed, max 0x07
    public static final int DNA = 0x00;  //destination network address, 0x00 = local
    public static final int DA1 = 0x01;  //destination node address, host-link
    public static final int DA2 = 0x00;  //destination unit address, 0x00 = CPU
    public static final int SNA = 0x00;  //source network address, 0x00 = local
    public static final int SA1 = 0xFB;  //source node address, 0x00 = CPU     
    public static final int SA2 = 0x00;  //source unit address, 0x00 = local
    public static final int SID = 0x01;  //source ID
    
    public static int[] CMD_MEMORY_AREA_READ = {0x01, 0x01};
    public static int[] CMD_MEMORY_AREA_WRITE = {0x01, 0x02};
    
    public static int MEMORY_AREA_B2 = 0xb2;
    public static int MEMORY_AREA_82 = 0x82;
    
    private String serverAddress;
    private int serverPort;
    private Socket serverConnection;
    private boolean connected = false;
    private int timeout = 30;
    
    private PrintWriter streamToServer;
    private BufferedReader streamFromServer;
    
    private FinsConnection(String address, int port) {
        this.serverAddress = serverAddress;
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
        
    }
    
    public void createFinsMessage(int type, int memoryArea, int register, int[] values) {
        FinsMessage message = new FinsMessage();
        
        switch (type) {
            case MSGTYPE_CONNECT:
                message.append(FINS_LENGTH_PLACEHOLDER);
                message.append(FINS_COMMAND_CONNECT);
                message.append(FINS_CONNECT);
                message.updateMessageLength();
                break;
            case MSGTYPE_READMEM:
                message.append(FINS_LENGTH_PLACEHOLDER);
                message.append(FINS_COMMAND_GENERIC);
                message.append(ICF_COMMAND);
                message.append(RSV);
                message.append(GCT);
                message.append(DNA);
                message.append(DA1);
                message.append(DA2);
                message.append(SNA);
                message.append(SA1);
                message.append(SA2);
                message.append(SID);
                message.append(CMD_MEMORY_AREA_READ);
                message.append(memoryArea);
                message.append(values.length);  //TODO num of registers to read
                message.append(RSV);
                message.append(register);  //TODO convert to array of 2 bytes
                break;
            case MSGTYPE_WRITEMEM:
                message.append(FINS_LENGTH_PLACEHOLDER);
                message.append(FINS_COMMAND_GENERIC);
                message.append(ICF_COMMAND);
                message.append(RSV);
                message.append(GCT);
                message.append(DNA);
                message.append(DA1);
                message.append(DA2);
                message.append(SNA);
                message.append(SA1);
                message.append(SA2);
                message.append(SID);
                message.append(CMD_MEMORY_AREA_WRITE);
                message.append(memoryArea);
                message.append(toWord(values.length));  //TODO convert to array of 2 bytes
                message.append(RSV);
                message.append(toWord(register));  //TODO convert to array of 2 bytes
                message.append(toWord(values));
                message.updateMessageLength();
                break;
        }
           
        
        System.out.println(message);
    }
    
    private int[] getMessageLenghtBytes(int length) {
        return new int[] {
                (byte) (length >>> 24),
                (byte) (length >>> 16),
                (byte) (length >>> 8),
                (byte) length
        };
    }
    
    private int[] toWord(int hexNumber) {
        return new int[] {
                (byte) (hexNumber >>> 8),
                (byte) hexNumber
        };
    }
    
    private int[] toWord(int[] hexNumberArray) {
        int[] res = new int[hexNumberArray.length * 2];
        
        for (int i = 0, j = 0; i < res.length; i += 2, j++) {
            int[] word = toWord(hexNumberArray[j]);
            res[i] = word[0];
            res[i+1] = word[1];
        }
        
        return res;
    }
}
