package com.kojavaee.unity.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServer {
	private final int port;
	
	public ChatServer(int port) {
		this.port = port;
	}
	
	public void run() throws InterruptedException {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChatServerInit());
			
			sb.bind(port).sync().channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("chat server start ....");
		
		int port = 8443;
		new ChatServer(port).run();
	}
}
