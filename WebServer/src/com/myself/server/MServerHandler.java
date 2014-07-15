package com.myself.server;


import messageBody.requestMsg.nr_Read_res;
import messageBody.requestMsg.nr_write_res;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import common.EMSGID;


public class MServerHandler extends SimpleChannelUpstreamHandler 
{
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) 
    {
        if (!(e.getMessage() instanceof NetMsg)) 
        {
            return;
        }        
        webSession.getInstance().addSession(e);
        
//        NetMsg msg  = (NetMsg)e.getMessage();
//        if (msg.getMsgID() == EMSGID.nr_read_res)
//		{
//			nr_Read_res msgLite = msg.getMessageLite();
//			System.out.println(System.nanoTime()-msgLite.getTime());
//		}
//        else if (msg.getMsgID() == EMSGID.nr_write_res){
//        	nr_write_res msgLite = msg.getMessageLite();
//			System.out.println(System.nanoTime()-msgLite.getTime());
//		}
    }
 
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
    {  
		Channel channel = e.getChannel();
		webSession.getInstance().removeRequestChannel(channel);
		channel.close();
    }
    
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)throws Exception 
	{
		webSession.getInstance().addRequestChannel(e.getChannel());
	}
}