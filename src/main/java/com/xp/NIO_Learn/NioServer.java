package com.xp.NIO_Learn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO服务器端Demo,写清楚注释，方便回顾。
 */
public class NioServer {

    /**
     * 启动
     * 七个步骤创建
     */
    public void start() throws IOException {
        /**
         * 1. 创建Selector
         */
        Selector selector = Selector.open();

        /**
         * 2. 通过ServerSocketChannel创建channel通道
         */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        /**
         * 3. 为channel通道绑定监听端口
         */
        serverSocketChannel.bind(new InetSocketAddress(8000));

        /**
         * 4. **设置channel为非阻塞模式**【重要】--false
         */
        serverSocketChannel.configureBlocking(false);

        /**
         * 5. 将serverSocketChannel注册到selector上，监听连接事件
         * 把channel注册到select上，调用的反而是channel的方法
         * 后面有好几个参数：
         * OP_READ
         * OP_ACCEPT 接收连接
         * OP_CONNECT
         * OP_WRITE
         */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动成功！");

        /**
         * 6. 循环等待新接入的连接
         */
        while(true){
            /**
             * 获取可用channel数量
             */
            int readyChannels = selector.select();

            /**
             * 空轮询
             */
            if (readyChannels == 0) continue;

            /**
             * 获取注册到select上的所有可用channel的集合, 获取准备就绪的 SelectionKey类型的集合
             */
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            //迭代器
            Iterator iterator = selectionKeys.iterator();

            //迭代取出实例
            while (iterator.hasNext()) {
                /**
                 * selectionKey实例
                 */
                SelectionKey selectionKey = (SelectionKey) iterator.next();

                /**
                 * 移除Set中的当前处理过的selectionKey
                 */
                iterator.remove();

                /**
                 * 7. 根据就绪状态，调用对应方法处理业务逻辑
                 */
                /**
                 * 如果是 接入事件
                 */
                if (selectionKey.isAcceptable()) {
                    acceptHandler(serverSocketChannel, selector);
                }

                /**
                 * 如果是 可读事件
                 */
                if (selectionKey.isReadable()) {
                    readHandler(selectionKey, selector);
                }
            }
        }
    }

    /**
     * 接入事件处理器
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel,
                               Selector selector)
            throws IOException {
        /**
         * 如果要是接入事件，创建socketChannel
         * 这个socket就是与客户端建立的scoket连接
         * 在BIO中建立的是socket连接
         * 在NIO中建立的是SocketChannel
         * 一个Channel只能被注册到Selector上一次，如果将Channel注册多次到Selector上，其实相当于是在更新
         * 不再次注册也是可以运行的
         */
        SocketChannel socketChannel = serverSocketChannel.accept();

        /**
         * 将socketChannel设置为非阻塞工作模式
         */
        socketChannel.configureBlocking(false);

        /**
         * 将channel注册到selector上，监听 可读事件
         */
        socketChannel.register(selector, SelectionKey.OP_READ);

        /**
         * 回复客户端提示信息
         */
        socketChannel.write(Charset.forName("UTF-8")
                .encode("你与聊天室里其他人都不是朋友关系，请注意隐私安全"));
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector)
            throws IOException {
        /**
         * 要从 selectionKey 中获取到已经就绪的channel
         */
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        /**
         * 创建buffer
         */
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        /**
         * 循环读取客户端请求信息
         */
        String request = "";
        //返回一共读取了多少个字节数据。>0 的话就继续操作
        while (socketChannel.read(byteBuffer) > 0) {
            /**
             * 之前是写入状态，将通道中数据读入缓冲区
             * 此处需要切换buffer为读模式
             */
            byteBuffer.flip();

            /**
             * 读取buffer中的内容
             * 保证编码和解码的字符计是一样的
             * 把byteBuffer编解码成一个字符串
             */
            request += Charset.forName("UTF-8").decode(byteBuffer);
        }

        /**
         * 将channel再次注册到selector上，监听他的可读事件，多次注册相当于刷新
         */
        socketChannel.register(selector, SelectionKey.OP_READ);

        /**
         * 将客户端发送的请求信息 广播给其他客户端
         */
        if (request.length() > 0) {
            // 广播给其他客户端
            broadCast(selector, socketChannel, request);
        }
    }

    /**
     * 广播给其他客户端
     */
    private void broadCast(Selector selector,
                           SocketChannel sourceChannel, String request) {
        /**
         * 获取到所有已接入的客户端channel，通过selector就可以获取
         * 之前是获取所有就绪的selectedKeys，这里是获取所有的，不管是否就绪，注意区分！
         */
        Set<SelectionKey> selectionKeySet = selector.keys();

        /**
         * 循环向所有channel广播信息
         */
        selectionKeySet.forEach(selectionKey -> {
            //获取目标的channel
            Channel targetChannel = selectionKey.channel();

            // 剔除发消息的客户端，不给发送消息来的channel发送，所以需要剔除掉发送消息的sourceChannel
            if (targetChannel instanceof SocketChannel
                    && targetChannel != sourceChannel) {
                try {
                    // 将信息发送到targetChannel客户端
                    ((SocketChannel) targetChannel).write(
                            Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 主方法
     * @param args
     */
    public static void main(String[] args) throws IOException {
        //启动服务器
        new NioServer().start();
    }

}
