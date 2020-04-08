package com.xp.NIO_Learn;

import java.io.IOException;

/**
 * 测试一下分支上传
 * ceshi
 */
public class BClient {

    public static void main(String[] args)
            throws IOException {
        new NioClient().start("BClient");
    }

}
