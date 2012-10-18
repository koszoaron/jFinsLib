package com.github.koszoaron.jfinslib;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Main class for testing the implementation
 */
public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        FinsConnection connection = FinsConnection.newInstance("192.168.1.103", 9600);
        try {
            connection.connect();
        } catch (SocketTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
