package com.xp.NIO_Learn;

import java.io.IOException;

/**
 * test branch
 */
public class BClient {

    public static void main(String[] args)
            throws IOException {
        new NioClient().start("BClient");
    }

}
