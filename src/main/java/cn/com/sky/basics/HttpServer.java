package cn.com.sky.basics;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.net.InetSocketAddress;

/**
 * <pre>
 *
 *  服务端启动类
 *
 * 所有 Netty 服务端的启动类都可以采用如下代码结构进行开发。
 *
 * 简单梳理一下流程：
 * 首先创建引导器；
 * 然后配置线程模型，通过引导器绑定业务逻辑处理器，并配置一些网络参数；
 * 最后绑定端口，就可以完成服务器的启动了。
 *
 * </pre>
 */
public class HttpServer {

    public void start(int port) throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //主从多线程模式
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    //设置 Channel 类型
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    //注册 ChannelHandler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast("codec", new HttpServerCodec())         // HTTP 编解码
                                    .addLast("compressor", new HttpContentCompressor())   // HttpContent 压缩
                                    .addLast("aggregator", new HttpObjectAggregator(65536)) // HTTP 消息聚合
                                    .addLast("handler", new HttpServerHandler());      // 自定义业务逻辑处理器
                        }

                    })
                    //设置 Channel 参数
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //端口绑定
            //bind() 方法会真正触发启动，sync() 方法则会阻塞，直至整个启动过程完成。
            ChannelFuture f = b.bind().sync();

            System.out.println("Http Server started， Listening on " + port);

            //这句话会让线程进入 wait 状态，这样服务端可以一直处于运行状态，如果没有这行代码，bind 操作之后就会进入 finally 代码块，整个服务端就退出结束了。
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        new HttpServer().start(8088);
    }

}
