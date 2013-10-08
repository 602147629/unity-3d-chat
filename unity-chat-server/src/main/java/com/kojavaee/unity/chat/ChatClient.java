package com.kojavaee.unity.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ChatClient {
	private final String host;
	private final int port;
	
	public ChatClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void run() throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioSocketChannel.class)
			 .handler(new ChatClientInit());
		
			Channel ch = b.connect(host, port).sync().channel();
			
			ChannelFuture lastWriteFutrue = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			for(;;) {
				String line = in.readLine();
				
				if(line == null) {
					break;
				}
				
				lastWriteFutrue = ch.writeAndFlush(line + "\r\n");
				
				if("bye".equals(line.toLowerCase())) {
					ch.closeFuture().sync();
				}
			}
			
			if(lastWriteFutrue != null) {
				lastWriteFutrue.sync();
			}
		} finally {
			group.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("chat client start...");
		
		String host = "127.0.0.1";
		int port = 8443;
		
		new ChatClient(host, port).run();
	}
	
}
