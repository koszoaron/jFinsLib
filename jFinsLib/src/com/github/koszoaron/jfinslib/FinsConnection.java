package com.github.koszoaron.jfinslib;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.github.koszoaron.jfinslib.FinsMessage.ValueType;

/**
 * A class representing a connection to a PLC device using the FINS protocol
 * 
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public class FinsConnection {
    public static final int HEX_REGISTER = 0;
    public static final int BCD_REGISTER = 1;  //TODO convert to enum
    
    private static final int SLEEP_MS = 100;
    
    private static final int TESTING_SLEEP_MS = 150;
    private static final int TESTING_LONG_SLEEP_MS = 250;
    private static final int TESTING_READ_DEFAULT_ANSWER = 42;
    private static final int TESTING_READ_ERROR = -1;
    
    private String serverAddress;
    private int serverPort;
    private Socket serverConnection;
    private boolean connected = false;
    private int timeout = 30;
    private boolean testing = false;
    private int testingDefaultAnswer = TESTING_READ_DEFAULT_ANSWER;
    
    private DataOutputStream streamToServer;
    private InputStream streamFromServer;
    
    /**
     * Creates a new FinsConnection object
     * 
     * @param address The IP address of the device
     * @param port The port of the device
     */
    private FinsConnection(String address, int port) {
        this.serverAddress = address;
        this.serverPort = port;
    }
    
    /**
     * Returns a new instance of a device connection.
     * 
     * @param address The IP address of the device
     * @param port The TCP port of the device
     * @return An instance of FinsConnection
     */
    public static FinsConnection newInstance(String address, int port) {
        return new FinsConnection(address, port);
    }
    
    /**
     * @return True if the connection to the device is alive
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * @return True if in testing mode..
     */
    public boolean isTesting() {
        return testing;
    }
    
    /**
     * Enables or disables testing mode.
     * 
     * @param testing True to enable
     * @param defaultAnswer The value to return when requesting a read operation
     */
    public void setTesting(boolean testing, int defaultAnswer) {
        this.testing = testing;
        this.testingDefaultAnswer = defaultAnswer;
    }
    
    /**
     * Connects to a device.
     * 
     * @return True if the operation was successful
     */
    public boolean connect() {
        boolean success = false;
        
        if (testing) {
            try {
                Thread.sleep(TESTING_LONG_SLEEP_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            connected = true;
            return true;
        }
        
        if (!connected) {
            SocketAddress address = new InetSocketAddress(serverAddress, serverPort);
            serverConnection = new Socket();
            
            //establish socket connection
            try {
                serverConnection.connect(address, timeout);
                streamToServer = new DataOutputStream(serverConnection.getOutputStream());
                streamFromServer = serverConnection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                
                return false;
            }
            
            //send a connect message
            FinsMessage connectMessage = new FinsMessage();
            try {
                streamToServer.write(connectMessage.getMessageBytes());
            } catch (IOException e) {
                e.printStackTrace();
                
                return false;
            }            
            
            //wait 1 sec
            try {
                Thread.sleep(SLEEP_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                
                return false;
            }
            
            //read the response
            byte[] inputBytes = new byte[32];
            int bytesRead = -1;
            StringBuilder response = new StringBuilder();
            try {
                if (streamFromServer.available() > 0) {
                    bytesRead = streamFromServer.read(inputBytes);
                }
                if (bytesRead > 0) {
                    for (int i : inputBytes) {
                        response.append(String.format("%02x", i) + " ");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                
                return false;
            }
            
            connected = true;
            success = true;
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
        
        if (testing) {
            try {
                Thread.sleep(TESTING_SLEEP_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            connected = false;
            return true;
        }
        
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
                connected = false;
            }
        }
        
        return success;
    }
    
    /**
     * Sends a message to the connected device.
     * 
     * @param message The {@link FinsMessage} to send
     * @return The response from the device (in an array of integers) or {@code NULL}
     */
    public int[] sendFinsMessage(FinsMessage message) {
        int[] response = null;
        
        if (connected) {
            //send the message
            try {
                streamToServer.write(message.getMessageBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            //wait 1 sec
            try {
                Thread.sleep(SLEEP_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            //get the response
            byte[] inputBytes = new byte[64];
            int bytesRead = -1;
            try {
                if (streamFromServer.available() > 0) {
                    bytesRead = streamFromServer.read(inputBytes);
                }
                if (bytesRead > 0) {
                    response = new int[bytesRead];
                    
                    for (int i = 0; i < bytesRead; i++) {
                        response[i] = inputBytes[i];
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return response;
    }
    
    /**
     * Writes the values in the argument to the specified register of the connected device.
     * 
     * @param memoryArea The register area designation byte
     * @param registerAddress The address of the register
     * @param values The values to write (in an array of integers)
     * @return True if the operation was successful
     */
    public boolean writeRegister(int memoryArea, int registerAddress, int[] values) {
        boolean success = false;
        
        if (testing) {
            try {
                Thread.sleep(TESTING_SLEEP_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        
        int[] response = sendFinsMessage(new FinsMessage(memoryArea, registerAddress, values, null));
        if (response != null) {
            success = true;
        }
        
        return success;
    }
    
    /**
     * Writes the values in the argument as binary-coded decimals to the specified register of the connected device.
     * 
     * @param memoryArea The register area designation byte
     * @param registerAddress The address of the register
     * @param values The values to write (in an array of integers)
     * @return True if the operation was successful
     */
    public boolean writeBcdRegister(int memoryArea, int registerAddress, int[] values) {
        boolean success = false;
        
        if (testing) {
            try {
                Thread.sleep(TESTING_SLEEP_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        
        int[] response = sendFinsMessage(new FinsMessage(memoryArea, registerAddress, values, ValueType.BCD));
        if (response != null) {
            success = true;
        }
        
        return success;
    }
    
    /**
     * Reads the value of a single register of the connected device.
     * 
     * @param memoryArea The register area designation byte
     * @param registerAddress The address of the register
     * @return The value stored in the register or {@code UNKNOWN_VALUE} if the operation was not successful
     */
    public int readRegister(int memoryArea, int registerAddress) {
        int res = Constants.UNKNOWN_VALUE;
        
        if (testing) {
            try {
                Thread.sleep(TESTING_LONG_SLEEP_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return TESTING_READ_ERROR;
            }
            return testingDefaultAnswer;
        }
        
        int[] response = sendFinsMessage(new FinsMessage(memoryArea, registerAddress, 1));
        if (response != null) {
            int upperByte = response[response.length-2];
            int lowerByte = response[response.length-1];
            
            res = upperByte * 256 + lowerByte;
        }
        
        return res;
    }
}
