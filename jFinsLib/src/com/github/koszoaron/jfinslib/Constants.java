package com.github.koszoaron.jfinslib;

/**
 * @author Aron Koszo <koszoaron@gmail.com>
 */
public final class Constants {

    private Constants() {} //this class should not be instantiated
    
    public static final int DEFAULT_GCT = 0x02;  //fixed, max 0x07
    public static final int DEFAULT_DNA = 0x00;  //destination network address, 0x00 = local
    public static final int DEFAULT_DA1 = 0x01;  //destination node address, host-link
    public static final int DEFAULT_DA2 = 0x00;  //destination unit address, 0x00 = CPU
    public static final int DEFAULT_SNA = 0x00;  //source network address, 0x00 = local
    public static final int DEFAULT_SA1 = 0xFB;  //source node address, 0x00 = CPU     
    public static final int DEFAULT_SA2 = 0x00;  //source unit address, 0x00 = local
    public static final int DEFAULT_SID = 0x01;  //source ID
    
}
