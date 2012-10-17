package com.github.koszoaron.jfinslib;

/**
 * Main class for testing the implementation
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        FinsMessage connectionMessage = new FinsMessage();
        System.out.println("Connect: " + connectionMessage.toString());
        
        FinsMessage writeMsg = new FinsMessage(0xb2, 0x03, new int[] {0x20, 0x40});
        System.out.println("Write:   " + writeMsg);
        
        FinsMessage readMsg = new FinsMessage(0xb2, 0x11, 5);
        System.out.println("Read:    " + readMsg);
    }

}
