package com.myself.memcached;


import messageBody.requestMsg.nr_Connected_mem;
import messageBody.requestMsg.nr_Read_res;
import messageBody.requestMsg.nr_write_res;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.myself.server.NetMsg;
import com.myself.server.webSession;
import common.EMSGID;


public class MClientHandler extends SimpleChannelUpstreamHandler 
{ 
	
	private static int ticks=0;
	private static long diffTime = 0;
	
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) 
    {
    	nr_Connected_mem.Builder builder = nr_Connected_mem.newBuilder();
    	NetMsg send = NetMsg.newMessage();
    	send.setMsgID(EMSGID.nr_connected_mem);
    	send.setMessageLite(builder);
    	
    	e.getChannel().write(send);
    }
 
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) 
    {  	
    	 if (!(e.getMessage() instanceof NetMsg)) 
         {
             return;
         }    	 
		webSession.getInstance().addSession(e);

    	 
    	 
//         NetMsg msg  = (NetMsg)e.getMessage();
//         if (msg.getMsgID() == EMSGID.nr_read_res || msg.getMsgID() == EMSGID.nr_write_res)
// 		{
// 			method();
// 		}
//         else 
//         {
//			webSession.getInstance().addSession(e);
//		}
    } 
    
	public synchronized  static void method()
	{			 
		 if (ticks == 0)
		{
			diffTime = System.nanoTime();
		}
		 ticks++;
		 if (ticks == 5000)
		{
			System.out.println((System.nanoTime()-diffTime)/1000000.0);
			ticks = 0;
		}
	}
 
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) 
    {       
    	if (e.getChannel().getLocalAddress() == null) {
			return;
		}
        webSession.getInstance().removeClientChannel(e.getChannel());
        e.getChannel().close();
    }
}