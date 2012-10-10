package com.github.koszoaron.jfinslib;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        FinsConnection connection = FinsConnection.newInstance("127.0.0.1", 80);
        connection.createFinsMessage(FinsConnection.MSGTYPE_CONNECT, 0, 0, null);
        connection.createFinsMessage(FinsConnection.MSGTYPE_WRITEMEM, FinsConnection.MEMORY_AREA_B2, 01, new int[] {0x20, 0x40});
    }

}
