package com.myself.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.myself.database.DBMessage;
import com.myself.database.DatabaseCon;

public class DBSession implements Runnable 
{
	public static DBSession dbSession;
	ArrayBlockingQueue<DBMessage> dbqueryQueue = new ArrayBlockingQueue<>(100);
	ConcurrentLinkedDeque<DBMessage> dbReturnQueue = new ConcurrentLinkedDeque<DBMessage>();
	public static DBSession getInstance()
	{
		if (dbSession == null) 
		{
			dbSession = new DBSession();
		}
		return dbSession;
	}
	
	public void start()
	{
		DatabaseCon.getInstance().start();
		new Thread(dbSession).start();
	}	
	
	public void run() 
	{
		while(true) // 数据库执行线程
		{
			try {
				handle(dbqueryQueue.take());
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void handle(DBMessage msg) 
	{
		if (msg == null) return;
		
		switch (msg.mode) 
		{
		case DBMessage.mode_query:
			msg.value = DatabaseCon.getInstance().queryKey(msg.key);
			dbReturnQueue.add(msg);
			break;
		case DBMessage.mode_set:
			DatabaseCon.getInstance().setKey(msg.key, msg.value);
			dbReturnQueue.add(msg);
			break;
		default:
			break;
		}
	}
	
	public void addDBMessage(DBMessage msg)
	{
		try {
			dbqueryQueue.put(msg);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
