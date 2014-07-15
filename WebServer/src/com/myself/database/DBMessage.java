package com.myself.database;

public class DBMessage 
{
	public int mode;
	public Integer ClientID;
	
	public String key;
	public String value;
	
	public final static int mode_query = 1;
	public final static int mode_set=2;
}
