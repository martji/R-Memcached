package com.myself.server;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.myself.memcached.MemcachedMgr;

import common.RegisterHandler;
public class WebServerMain
{
	static HashMap<Integer, ClientConfig> m_mapMemcachedClient;	
	public static boolean initConfig()
	{
		m_mapMemcachedClient = new HashMap<Integer, ClientConfig>();
		File f = new File(System.getProperty("user.dir"));
		String path = f.getPath() + File.separator + "bin" + File.separator;
		readClientsXML(path+"client.xml");
		return true;
	}			
	
	private static class bench extends Thread 
	{
		private int runs;
		@SuppressWarnings("unused")
		private int threadNum;
		private String object;
		private String[] keys;
		@SuppressWarnings("unused")
		private int size;
		private int nums;
		private double rate;

		public bench(int runs,int nums, int threadNum, String object, String[] keys, double rate)
		{
			this.runs = runs;
			this.threadNum = threadNum;
			this.object = object;
			this.keys = keys;
			this.size = object.length();
			this.nums = nums;
			this.rate = rate;
		}

		public void run() 
		{
			try	{
				Thread.sleep(10);
			} catch (InterruptedException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// time deletes
			long time = 0;
			time = System.nanoTime();
			randReadWrite(rate);
			time = System.nanoTime() - time;
			System.out.println(time/1000000000.0f);
		}
		
		public void randReadWrite(double scale)
		{
			Random randNum = new Random();
			for (int i = 0; i < runs; i++) {
				if (Math.random()<scale){
					webSession.getInstance().get(keys[randNum.nextInt(nums)]);
				}else {
					webSession.getInstance().set(keys[randNum.nextInt(nums)], object);
				}
				
				try	{
					Thread.sleep((long) 0.00001);
				} catch (InterruptedException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) 
	{
		PropertyConfigurator.configure(System.getProperty("user.dir")+"/bin/log4j.properties");//加载.properties文件
		initConfig();
		
		RegisterHandler.initHandler();
		webSession.getInstance().start();
		
		// client管理
		MemcachedMgr clientMgr = MemcachedMgr.getInstance();
		clientMgr.init(m_mapMemcachedClient);	

		Server requestServer = Server.getInstance();
		requestServer.init(8888);
		
		int threads = Integer.parseInt(args[0]);//线程数
		int runs = Integer.parseInt(args[1]);   //执行次数
		int Nums = Integer.parseInt(args[2]);	// key数目
		int size = Integer.parseInt(args[3]);	// value大小
		double rate = Double.parseDouble(args[4]); //读写比例

		// get object to store
		byte[] obj = new byte[size];
		for (int i = 0; i < size; i++) 
		{
			obj[i] = '1';
		}
		String value = new String(obj);

		String[] keys = new String[Nums];
		for (int i = 0; i < Nums; i++)
		{
			keys[i] = "" + i;
		}

		for (int i = 0; i < threads; i++) 
		{
			bench b = new bench(runs, Nums, i, value, keys, rate);
			b.start();
		}
	}
	
	// 读取memcached client配置
	 public static boolean readClientsXML(String str) 
	 {
       DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
       try {
           factory.setIgnoringElementContentWhitespace(true);
           
           DocumentBuilder db=factory.newDocumentBuilder();
           Document xmldoc=db.parse(new File(str));
           Element elmtInfo = xmldoc.getDocumentElement();
           NodeList nodes = elmtInfo.getChildNodes();
           for (int i = 0; i < nodes.getLength(); i++)
           {
               Node result = nodes.item(i);
               if (result.getNodeType() == Node.ELEMENT_NODE && result.getNodeName().equals("client"))
               {
                   NodeList ns = result.getChildNodes();
					ClientConfig localClient = new ClientConfig(); 
	                int m=0;
                   for (int j = 0; j < ns.getLength(); j++)
                   {
                       Node record = ns.item(j);						
                       if (record.getNodeType() == Node.ELEMENT_NODE)
                       {
	                       	if (record.getNodeName().equals("id")) 
	                       	{
	                       		m++;
									localClient.id = Integer.decode(record.getTextContent());
							}
	                       	else if (record.getNodeName().equals("host")) 
	                       	{
	                       		m++;
									localClient.host = record.getTextContent();
							}
	                       	else if (record.getNodeName().equals("client_port"))
	                       	{
	                       		m++;
	                       		localClient.client_port = Integer.decode(record.getTextContent());
							}  
	                        
	                       	else if (record.getNodeName().equals("memcached")) 
	                       	{
								m++;
								localClient.memcached = record.getTextContent();
							}
                       }
					}
                   if(m==4) 
                   {
                	   m_mapMemcachedClient.put(localClient.id, localClient);
                   }                   
               }
           }
       } catch (ParserConfigurationException e) {
           e.printStackTrace();
       } catch (SAXException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return true;
   }
}
