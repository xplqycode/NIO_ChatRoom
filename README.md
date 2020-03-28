# 基于NIO的简易版多人聊天室的实现
NIO 核心

- Channel 通道
  - 双向通道
  - 非阻塞模式
  - 操作唯一性，通过buff
  Channel实现类型：
  - 文件类  FileChannel
  - UDP类：DatagramChannel
  - TCP类：ServerSocketChannel / SocketChannel
- Buff 缓冲区
  读写Channel中的数据
  本质上就是一块内存区域
  - Capacity 容量
  - Position 位置
  - limit 上限
  - mark 记录下当前position位置
  flip() 方法来进行读写切换 转变 limit 和 position 位置
- Selector 选择器
  IO就绪准备
  BIO网络编程的基础
  SelectionKey 选择键：
  - 四种就绪状态常量
