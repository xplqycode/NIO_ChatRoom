package com.xp.NIO_Learn;

import java.io.IOException;

public class AClient {

    public static void main(String[] args)
            throws IOException {
        new NioClient().start("AClient");
    }
    //拉的master代码，然后合并到test上

}
