package com.kojavaee.unity.chat;

import javax.net.ssl.SSLEngine;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslHandler;

public class ChatClientInit extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		
		SSLEngine engine = ChatSslContextFactory.getClientContext().createSSLEngine();
		engine.setUseClientMode(true);
		
		pipeline.addLast("ssl", new SslHandler(engine));
		
		pipeline.addLast("framer",new DelimiterBasedFrameDecoder(8192,Delimiters.lineDelimiter()));
		pipeline.addLast("decoder", new StringDecoder());
		pipeline.addLast("encoder", new StringEncoder());
		
		pipeline.addLast("handler", new ChatClientHandler());
	}

}
