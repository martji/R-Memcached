package com.myself.server;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.MessageLite;

import common.EMSGID;
import common.MessageManager;

public class NetMsg
{
	EMSGID msgID;
	MessageLite messageLite;
	
	private NetMsg(){};
	public static NetMsg newMessage()
	{
		return new NetMsg();
	}
	
	NetMsg(byte[] decoded, int id) throws Exception 
	{
		messageLite = MessageManager.getMessage(id, decoded);
	}
	
	public byte[] getBytes() 
	{
		return messageLite.toByteArray();
	}

	public EMSGID getMsgID() 
	{
		return msgID;
	}

	public void setMsgID(EMSGID id) {
		this.msgID = id;
		
	}

	@SuppressWarnings("unchecked")
	public <T extends MessageLite> T getMessageLite() 
	{
		return (T)messageLite;
	}
	
	//线程安全的
	@SuppressWarnings("rawtypes")
	public void setMessageLite(GeneratedMessage.Builder builder) 
	{
		this.messageLite = builder.build();
	}

}
