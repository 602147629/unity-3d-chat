package com.kojavaee.unity.chat;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatClientHandler extends SimpleChannelInboundHandler<String>{
	
	public static final Logger logger = Logger.getLogger(ChatClientHandler.class.getName());

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.err.println(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.log(Level.WARNING, "Unexpected exception from downstream.",cause);
		ctx.close();
	}	

}
