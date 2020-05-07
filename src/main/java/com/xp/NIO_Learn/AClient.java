package com.xp.NIO_Learn;

import java.io.IOException;

public class AClient {
    //从master拉的分支，会合并到test分支上
    //测试git push
    public static void main(String[] args)
            throws IOException {
        new NioClient().start("AClient");
    }
    //拉的master代码，然后合并到test上

}
