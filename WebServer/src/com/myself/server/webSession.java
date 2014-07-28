package com.myself.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import messageBody.memcachedmsg.nm_Connected;
import messageBody.memcachedmsg.nm_Connected_web_back;
import messageBody.requestMsg.nr_Connected_mem_back;
import messageBody.requestMsg.nr_Read;
import messageBody.requestMsg.nr_Read_res;
import messageBody.requestMsg.nr_Stats;
import messageBody.requestMsg.nr_Stats_res;
import messageBody.requestMsg.nr_write;
import messageBody.requestMsg.nr_write_res;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.json.JSONArray;

import com.myself.memcached.MemcachedMgr;
import common.EMSGID;

public class webSession implements Runnable {
	ConcurrentLinkedQueue<MessageEvent> recvQueue = new ConcurrentLinkedQueue<MessageEvent>();
	Map<Integer, Channel> MemcachedChannelMap = new ConcurrentHashMap<Integer, Channel>();
	Map<Integer, Channel> RequestChannelMap = new ConcurrentHashMap<Integer, Channel>();
	public static webSession session = null;
	
	public Channel curChannel;
	public long totalTime = 0;
	public long ticks = 0;
	
	public static JSONArray results = new JSONArray();
	public static Map<Integer, String> stats = new HashMap<>();

	// public static Logger log = LoggerUtil.getInstance();
	public static Logger log = Logger.getLogger(webSession.class.getName());

	public static webSession getInstance() {
		if (session == null) {
			session = new webSession();
		}
		return session;
	}

	public void start() {
		// DBSession.getInstance().start(); 数据库连接
		new Thread(session).start();
		System.out.println("session start");
	}

	// 添加request连接
	public void addRequestChannel(Channel ch) {
		RequestChannelMap.put(ch.getId(), ch);
	}

	// 删除request连接
	public void removeRequestChannel(Channel ch) {
		RequestChannelMap.remove(ch.getId());
	}

	// 获得request连接
	public Channel getRequestChannel(Integer id) {
		return RequestChannelMap.get(id);
	}

	// 增加client连接
	public void addClientChannel(Integer num, Channel ch) {
		MemcachedChannelMap.put(num, ch);
	}

	public Channel getClientChannel(Integer id) {
		return MemcachedChannelMap.get(id);
	}

	// 删掉client连接
	@SuppressWarnings("rawtypes")
	public void removeClientChannel(Channel ch) {
		Iterator iter = MemcachedChannelMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			if ((Channel) entry.getValue() == ch) {
				MemcachedChannelMap.remove((Integer) entry.getKey());
				break;
			}
		}
	}

	// ////////////////////////////////////////////////////////
	public void run() {
		while (true) {
			MessageEvent event = recvQueue.poll();
			while (event != null) {
				handle(event);
				event = recvQueue.poll();
			}

			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int gethashMem(String key) {
		return Math.abs(key.hashCode() % MemcachedMgr.getInstance().getSize());
	}

	public void handle(MessageEvent e) {
		NetMsg msg = (NetMsg) e.getMessage();
		switch (msg.getMsgID()) {
		case nm_connected: {
			nm_Connected msgBody = msg.getMessageLite();
			addClientChannel(msgBody.getNum(), e.getChannel());
			nm_Connected_web_back.Builder builder = nm_Connected_web_back.newBuilder();
			NetMsg send = NetMsg.newMessage();
			send.setMessageLite(builder);
			send.setMsgID(EMSGID.nm_connected_web_back);

			sendMsg(e.getChannel(), send);
		}
			break;
		case nr_connected_mem_back: {
			nr_Connected_mem_back msgLite = msg.getMessageLite();
			addClientChannel(msgLite.getMemID(), e.getChannel());
		}
			break;
		case nr_stats_res: {
			nr_Stats_res msgBody = msg.getMessageLite();
			stats.put(msg.getNodeRoute(), msgBody.getValue());
			//System.out.println("stats:\n" + msgBody.getValue());
		}
			break;
		case nr_read_res: {
			nr_Read_res msgBody = msg.getMessageLite();
			Map<String, String> readResult = new HashMap<String, String>();
			readResult.put("type", "GET");
			readResult.put("node", ""+msg.getNodeRoute());
			readResult.put("key", msgBody.getKey());
			readResult.put("value", msgBody.getValue());
			results.put(readResult);
			//System.out.println("key:"+msgBody.getKey()+" value:"+msgBody.getValue());
		}
			break;
		case nr_write_res: {
			nr_write_res msgBody = msg.getMessageLite();
			Map<String, String> readResult = new HashMap<String, String>();
			readResult.put("type", "SET");
			readResult.put("node", ""+msg.getNodeRoute());
			readResult.put("key", msgBody.getKey());
			readResult.put("value", msgBody.getValue());
			results.put(readResult);
			//System.out.println("key:"+msgBody.getKey()+" value:"+msgBody.getValue());
		}
			break;
		default:
			System.err.println(msg.getMsgID().toString());
			break;
		}
	}

	public void addSession(MessageEvent e) {
		recvQueue.offer(e);
	}

	public boolean sendAllMsg(Integer hash, NetMsg msg) {
		for (int i = 0; i < MemcachedMgr.nCopyNode; i++) {
			Channel eChannel = getClientChannel(hash + i);
			if (eChannel != null) {
				sendMsg(eChannel, msg);
				return false;
			}
		}
		return true;
	}

	public boolean allSendMsg2Memcached(NetMsg msg) {
		int size = MemcachedChannelMap.size();
		for (int i = 0; i < size; i++) {
			Channel eChannel = getClientChannel(i);
			if (eChannel != null) {
				sendMsg(eChannel, msg);
			}
		}
		return true;
	}

	public boolean randSendMsg2Memcached(Integer hash, NetMsg msg) {
		Random random = new Random();
		int index = random.nextInt(MemcachedMgr.nCopyNode);
		for (int i = 0; i < MemcachedMgr.nCopyNode; i++) {
			int num = (hash + i + index + MemcachedMgr.getInstance().getSize())
					% MemcachedMgr.getInstance().getSize();
			Channel eChannel = getClientChannel(num);
			if (eChannel != null) {
				sendMsg(eChannel, msg);
				return true;
			}
		}
		System.err.println("SendMsg wrong : randSendMsg2Memcached in webSession line 174");
		System.exit(-1);
		return false;
	}

	public boolean SendMsg2Leader(Integer hash, NetMsg msg) {
		for (int i = 0; i < MemcachedMgr.nCopyNode; i++) {
			int index = (hash + i + MemcachedMgr.getInstance().getSize())
					% MemcachedMgr.getInstance().getSize();
			Channel eChannel = getClientChannel(index);
			if (eChannel != null) {
				sendMsg(eChannel, msg);
				return true;
			}
		}
		return false;
	}

	public void sendMsg(Channel ch, NetMsg msg) {
		try {
			ch.write(msg);
		} catch (Throwable e) {
			// log.log(Level.WARNING, "send msg fail");
		}
	}

	public boolean stats() {
		nr_Stats.Builder builder = nr_Stats.newBuilder();
		builder.setTime(System.nanoTime());
		NetMsg msg = NetMsg.newMessage();
		msg.setMessageLite(builder);
		msg.setMsgID(EMSGID.nr_stats);
		allSendMsg2Memcached(msg);
		return true;
	}

	public boolean get(String key) {
		nr_Read.Builder builder = nr_Read.newBuilder();
		builder.setKey(key);
		builder.setTime(System.nanoTime());
		NetMsg msg = NetMsg.newMessage();
		msg.setMessageLite(builder);
		msg.setMsgID(EMSGID.nr_read);
		randSendMsg2Memcached(key.hashCode(), msg);
		return true;
	}

	public boolean set(String key, String value) {
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
