package com.github.koszoaron.jfinslib;

/**
 * Main class for testing the implementation
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        FinsConnection connection = FinsConnection.newInstance("192.168.1.103", 9600);
        connection.setTesting(true, 6424);
        
        connection.connect();
    }

}
