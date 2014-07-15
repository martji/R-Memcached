package com.myself.server;

public class LockKey {
	public Integer memNumber = 0;
	public Integer ncount = 0;
	public Integer state = unLock;
	
	LockKey(Integer num, Integer count, Integer s)
	{
		memNumber = num;
		ncount = count;
		state = s;
	}
	
	public final static Integer unLock = 0;
	public final static Integer badLock =1;
	public final static Integer waitLock =2;
}
