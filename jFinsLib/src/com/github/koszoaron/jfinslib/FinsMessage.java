package com.github.koszoaron.jfinslib;

public class FinsMessage {
    private int[] message = {};
    private static final int[] FINS_LENGTH_BYTES = {4, 5, 6, 7};
    
    public FinsMessage() {
        message = FinsConnection.FINS_HEADER;
    }
    
    public int[] getMessage() {
        return message;
    }
    
    public void append(int[] bytes) {
        int[] res = new int[message.length + bytes.length];
        
        System.arraycopy(message, 0, res, 0, message.length);
        System.arraycopy(bytes, 0, res, message.length, bytes.length);
        
        message = res;
    }
    
    public void append(int singleByte) {
        int[] toAppend = {singleByte};
        append(toAppend);
    }
    
    public void updateMessageLength() {
        int length = message.length - 8;
        
        int[] lengthBytes = {
                (byte) (length >>> 24),
                (byte) (length >>> 16),
                (byte) (length >>> 8),
                (byte) length
        };
        
        for (int i : FINS_LENGTH_BYTES) {
            message[i] = lengthBytes[i-4];
        }
    }
    
    @Override
    public String toString() {
        String res = "";
        
        for (int i : message) {
            res += String.format("%02x", i) + " ";
        }
        
        res += "(length: " + message.length + ")";
        
        return res;
    }
}
