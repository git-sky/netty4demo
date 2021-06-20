package cn.com.sky.basics.event;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;


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
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      public void initChannel(SocketChannel ch) {
                                          ch.pipeline()
                                                  .addLast(new SampleInBoundHandler("SampleInBoundHandlerA", false))
                                                  .addLast(new SampleInBoundHandler("SampleInBoundHandlerB", false))
                                                  .addLast(new SampleInBoundHandler("SampleInBoundHandlerC", true));
                                          ch.pipeline()
                                                  .addLast(new SampleOutBoundHandler("SampleOutBoundHandlerA"))
                                                  .addLast(new SampleOutBoundHandler("SampleOutBoundHandlerB"))
                                                  .addLast(new SampleOutBoundHandler("SampleOutBoundHandlerC"));
                                      }
                                  }
                    )
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



