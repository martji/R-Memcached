package com.myself.memcached;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import com.myself.server.ClientConfig;

public class MemcachedMgr
{	
	public HashMap<Integer, Client> m_mapLocalClients;
	static MemcachedMgr memcachedMgr;
	public static final int nCopyNode = 3;
	
	public static MemcachedMgr getInstance()
	{
		if (memcachedMgr == null) 
		{
			memcachedMgr = new MemcachedMgr();	
		}
		return memcachedMgr;
	}	
	
	public Integer getSize() 
	{
		return m_mapLocalClients.size();
	}
	
	@SuppressWarnings("rawtypes")
	public void init(HashMap<Integer, ClientConfig> hm)
	{
		m_mapLocalClients = new HashMap<Integer, Client>();
		
		Iterator iter = hm.entrySet().iterator();
		while (iter.hasNext()) 
		{
			Entry entry = (Entry) iter.next();
			ClientConfig cc = (ClientConfig)entry.getValue();

			Client lc = new Client();
			lc.host = cc.host;
			lc.port = cc.client_port;
			lc.id = cc.id;
			m_mapLocalClients.put(lc.id, lc);
			
			if(lc.init(lc.host, lc.port))
			{
				System.out.println("client connected successful");
			}
		}
	}

}
