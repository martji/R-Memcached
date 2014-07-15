package com.myself.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import messageBody.clientMsg.nc_Read;
import messageBody.clientMsg.nc_Write;
import messageBody.memcachedmsg.nm_Connected;
import messageBody.memcachedmsg.nm_Connected_web_back;
import messageBody.requestMsg.nr_Connected_mem_back;
import messageBody.requestMsg.nr_Read;
import messageBody.requestMsg.nr_Read_res;
import messageBody.requestMsg.nr_write;
import messageBody.requestMsg.nr_write_res;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.myself.database.DBMessage;
import com.myself.memcached.MemcachedMgr;
import common.EMSGID;

public class webSession implements Runnable
{
	ConcurrentLinkedQueue<MessageEvent> recvQueue = new ConcurrentLinkedQueue<MessageEvent>();
	Map<Integer, Channel> MemcachedChannelMap = new ConcurrentHashMap<Integer, Channel>();
	Map<Integer, Channel> RequestChannelMap = new ConcurrentHashMap<Integer, Channel>();
	static webSession session = null;
	public Channel curChannel;
	public long totalTime = 0;
	public long ticks =0;
	
	//public static Logger log = LoggerUtil.getInstance();
	public static Logger log = Logger.getLogger(webSession.class.getName());
	
	public static webSession getInstance()
	{
		if (session == null) 
		{
			session = new webSession();
		}
		return session;
	}
	
	public void start()
	{
		//DBSession.getInstance().start(); 数据库连接
		new Thread(session).start();
		System.out.println("session start");
	}	

	// 添加request连接
	public void addRequestChannel(Channel ch)
	{
		RequestChannelMap.put(ch.getId(), ch);
	}
	// 删除request连接
	public void removeRequestChannel(Channel ch)
	{
		RequestChannelMap.remove(ch.getId());	
	}
	// 获得request连接
	public Channel getRequestChannel(Integer id) 
	{
		return RequestChannelMap.get(id);
	}	
	
	// 增加client连接
	public void addClientChannel(Integer num,Channel ch)
	{
		MemcachedChannelMap.put(num, ch);
	}
	public Channel getClientChannel(Integer id) 
	{
		return MemcachedChannelMap.get(id);
	}
	// 删掉client连接
	@SuppressWarnings("rawtypes")
	public void removeClientChannel(Channel ch)
	{
		Iterator iter = MemcachedChannelMap.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Entry entry = (Entry) iter.next();
			if ((Channel)entry.getValue() == ch) 
			{
				MemcachedChannelMap.remove((Integer)entry.getKey());	
				break;
			}
		}		
	}
	//////////////////////////////////////////////////////////
	public void run()
	{		
		while(true)
		{
			MessageEvent event = recvQueue.poll();
			while(event != null)
			{
				handle(event);
				event = recvQueue.poll();
			}
			
			try
			{
				Thread.sleep(0);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public int gethashMem(String key) 
	{
		return Math.abs(key.hashCode()%MemcachedMgr.getInstance().getSize());
	}
	
	public void handle(MessageEvent e) 
	{
		NetMsg msg = (NetMsg)e.getMessage();
		switch (msg.getMsgID()) 
		{
		case nm_connected:
		{
			nm_Connected msgBody = msg.getMessageLite();
			addClientChannel(msgBody.getNum(), e.getChannel());			
			
			nm_Connected_web_back.Builder builder = nm_Connected_web_back.newBuilder();
			
			NetMsg send = NetMsg.newMessage();
			send.setMessageLite(builder);
			send.setMsgID(EMSGID.nm_connected_web_back);	
			
			sendMsg(e.getChannel(), send);
		}
		break;
		case nr_connected_mem_back:
		{
			nr_Connected_mem_back msgLite= msg.getMessageLite();
			addClientChannel(msgLite.getMemID(), e.getChannel());			
		}
			break;
		case nc_read:
		{
			nc_Read msgBody = msg.getMessageLite();
			nr_Read.Builder builder = nr_Read.newBuilder();
			builder.setClientid(e.getChannel().getId());
			builder.setKey(msgBody.getKey());
			
			NetMsg sendMsg = NetMsg.newMessage();
			sendMsg.setMessageLite(builder);
			sendMsg.setMsgID(EMSGID.nr_read);			
			
			if(randSendMsg2Memcached(gethashMem(msgBody.getKey()), sendMsg) == false)
			{
				DBMessage dbMsg=new DBMessage();
				dbMsg.ClientID = e.getChannel().getId();
				dbMsg.mode = DBMessage.mode_query;
				dbMsg.key = msgBody.getKey();
				DBSession.getInstance().addDBMessage(dbMsg);
			}
		}
		break;
		case nc_write:
		{
			nc_Write msgBody = msg.getMessageLite();
			
			nr_write.Builder builder = nr_write.newBuilder();
			builder.setKey(msgBody.getKey());
			builder.setValue(msgBody.getValue());				
			
			NetMsg send = NetMsg.newMessage();
			send.setMessageLite(builder);
			send.setMsgID(EMSGID.nr_write);
			if(SendMsg2Leader(gethashMem(msgBody.getKey()), send) == false)
			{
				DBMessage dbMsg=new DBMessage();
				dbMsg.ClientID = e.getChannel().getId();
				dbMsg.mode = DBMessage.mode_set;
				dbMsg.key = msgBody.getKey();
				dbMsg.value = msgBody.getValue();
				DBSession.getInstance().addDBMessage(dbMsg);
			}
		}
		break;
		
		case nr_read_res:
		{
			nr_Read_res msgBody = msg.getMessageLite();
//			System.out.println(String.valueOf((System.nanoTime()-msgBody.getTime())/1000000.0));
//			log.log(Priority.INFO, String.valueOf((System.nanoTime()-msgBody.getTime())/1000000.0));
//			log.log(Level.INFO, String.valueOf((System.nanoTime()-msgBody.getTime())/1000000.0));
//			System.err.println((System.nanoTime()-msgBody.getTime())/1000000.0);
//			if (ticks==0)
//			{
//				totalTime = System.currentTimeMillis();
//			}
			
			totalTime += System.nanoTime()-msgBody.getTime();
			ticks++;
			if (ticks == 1000)
			{
				System.out.println(totalTime/1000000000.0f);
				
				totalTime = 0;
				ticks = 0;
			}
//			if (msgBody.getValue().isEmpty())  //读数据库
//			{
//				DBMessage dbMsg=new DBMessage();
//				dbMsg.mode = DBMessage.mode_query;
//				dbMsg.key = msgBody.getKey();
//				
//				DBSession.getInstance().addDBMessage(dbMsg);
//			}
//			else 
//			{
//				nc_ReadRes.Builder builder = nc_ReadRes.newBuilder();
//				builder.setKey(msgBody.getKey());
//				builder.setValue(msgBody.getValue());
//				
//				NetMsg send = NetMsg.newMessage();
//				send.setMessageLite(builder);
//				send.setMsgID(EMSGID.nc_read_res);
//				
//				//sendMsg(getRequestChannel(msgBody.getClientid()), send);
//			}
		}
		break;
		case nr_write_res:
		{
			nr_write_res msgBody = msg.getMessageLite();
			//DBMessage dbMsg = new DBMessage();  //异步写数据库
			//dbMsg.mode = DBMessage.mode_set;
			//dbMsg.key = msgBody.getKey();
			//dbMsg.value = msgBody.getValue();
			//DBSession.getInstance().addDBMessage(dbMsg);
			
			
//			log.log(Level.INFO, String.valueOf((System.nanoTime()-msgBody.getTime())/1000000.0));
			//System.err.println();
			
//			System.out.println(String.valueOf((System.nanoTime()-msgBody.getTime())/1000000.0));
//			
//			if (ticks==0)
//			{
//				totalTime = System.currentTimeMillis();
//			}
			totalTime += System.nanoTime()-msgBody.getTime();
			ticks++;
			if (ticks == 1000)
			{
				System.out.println(totalTime/1000000000.0f);
				
				totalTime = 0;
				ticks = 0;
			}
			
//			nc_WriteRes.Builder builder = nc_WriteRes.newBuilder();
//			builder.setKey(msgBody.getKey());
//			builder.setValue(msgBody.getValue());
//			
//			NetMsg send = NetMsg.newMessage();
//			send.setMessageLite(builder);
//			send.setMsgID(EMSGID.nc_write_res);
			
			//sendMsg(getRequestChannel(msgBody.getClientid()), send);
		}
		break;
		default:
			System.err.println(msg.getMsgID().toString());
			break;
		}
		//log.log(Level.INFO, msg.getMsgID().toString());
	}
	public void addSession(MessageEvent e)
	{
		recvQueue.offer(e);
	}	
	public boolean sendAllMsg(Integer hash, NetMsg msg)
	{
		for (int i = 0; i < MemcachedMgr.nCopyNode; i++) 
		{
			Channel eChannel = getClientChannel(hash+i);
			if (eChannel != null) 
			{
				sendMsg(eChannel, msg);
				return false;
			}			
		}
		return true;
	}	
	
	public boolean randSendMsg2Memcached(Integer hash, NetMsg msg)
	{
		Random random = new Random();
		int index = random.nextInt(MemcachedMgr.nCopyNode);
		for (int i = 0; i < MemcachedMgr.nCopyNode; i++) 
		{
			int num = (hash+i+index+MemcachedMgr.getInstance().getSize())%MemcachedMgr.getInstance().getSize();
			Channel eChannel = getClientChannel(num);
			if (eChannel != null) 
			{				
				sendMsg(eChannel, msg);
				return true;
			}			
		}
		return false;
	}
	
	public boolean SendMsg2Leader(Integer hash, NetMsg msg)
	{
		for (int i = 0; i < MemcachedMgr.nCopyNode; i++) 
		{
			int index = (hash+i+MemcachedMgr.getInstance().getSize())
					%MemcachedMgr.getInstance().getSize();
			Channel eChannel = getClientChannel(index);
			if (eChannel != null) 
			{
				sendMsg(eChannel, msg);
				return true;
			}			
		}
		return false;
	}
		
	public void sendMsg(Channel ch, NetMsg msg)
	{
		try
		{
			ch.write(msg);
		}
		catch (Throwable e) 
		{
			//log.log(Level.WARNING, "send msg fail");
		}
	}

	
	public boolean get(String key)
	{
		nr_Read.Builder builder = nr_Read.newBuilder();
		builder.setKey(key);
		builder.setTime(System.nanoTime());
		NetMsg msg = NetMsg.newMessage();
		msg.setMessageLite(builder);
		msg.setMsgID(EMSGID.nr_read);
		randSendMsg2Memcached(key.hashCode(), msg);
		return true;
	}
	
	public boolean set(String key, String value)
	{
		nr_write.Builder builder = nr_write.newBuilder();
		builder.setKey(key);
		builder.setValue(value);
		builder.setTime(System.nanoTime());
		NetMsg msg = NetMsg.newMessage();
		msg.setMessageLite(builder);
		msg.setMsgID(EMSGID.nr_write);
		SendMsg2Leader(key.hashCode(), msg);
		return true;
	}
}
