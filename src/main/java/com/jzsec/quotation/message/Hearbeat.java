package com.jzsec.quotation.message;

/**
 * Created by caodaoxi on 17-7-14.
 */
public class Hearbeat implements MessageBody{

    public byte[] encode() {
        return new byte[0];
    }
}
