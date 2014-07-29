package dao;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.myself.memcached.MemcachedMgr;
import com.myself.server.ClientConfig;
import com.myself.server.Server;
import com.myself.server.webSession;

import common.RegisterHandler;

public class RMemcachedServer {
	static HashMap<Integer, ClientConfig> m_mapMemcachedClient;
	static String ORIGINPATH = Thread.currentThread().getContextClassLoader().getResource("").getPath();	
	public static Boolean status = false;
	public static int threadCount = 0;
	private static int startIndex = 0;
	public static boolean initFlag = false;
	
	public static long time = 0;
	private static Map<Integer, String> localStats = new HashMap<>();
	public static JSONArray nodeStats = new JSONArray();

	public static void main(String[] args){
		new Thread(new Runnable() {
			public void run() {
				RMemcachedServer.run(new String[]{"10","1000","1000","8","0.8"});
			}
		}).start();
		while (!RMemcachedServer.initFlag) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		while (!RMemcachedServer.status) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			RMemcachedServer.getResult();
		}
		RMemcachedServer.getResult();
		System.out.println(webSession.results.length());
		//System.out.println(nodeStats);
		//System.out.println(time);
		
		for (int i = 0; i < 50; i++){
			new Thread(new Runnable() {
				public void run() {
					RMemcachedServer.run(new String[]{"10","1000","1000","8","0.8"});
				}
			}).start();
			while (!RMemcachedServer.initFlag) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			while (!RMemcachedServer.status) {
				RMemcachedServer.getResult();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}
			RMemcachedServer.getResult();
			System.out.println(webSession.results.length());
		}
		System.exit(0);
	}
	
	private static void initial(){
		RMemcachedServer.startIndex = 0;
		RMemcachedServer.initFlag = false;
		RMemcachedServer.status = false;
		RMemcachedServer.time = 0;
		RMemcachedServer.nodeStats = new JSONArray();
		RMemcachedServer.localStats = new HashMap<>();
		RMemcachedServer.threadCount = 0;
		webSession.results = new JSONArray();
	}
	
	public static void run(String[] args) {
		initial();
		//System.out.println(ORIGINPATH);
		PropertyConfigurator.configure(ORIGINPATH + "log4j.properties");
		initConfig();

		RegisterHandler.initHandler();
		webSession.getInstance().start();
		//new webSession().start();
		

		MemcachedMgr clientMgr = MemcachedMgr.getInstance();
		clientMgr.init(m_mapMemcachedClient);

		Server requestServer = Server.getInstance();
		requestServer.init(8888);
		
		localStats = new HashMap<>();
		try {
			Thread.sleep(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getStats();
		
		int threads = Integer.parseInt(args[0]);
		int runs = Integer.parseInt(args[1]);
		int Nums = Integer.parseInt(args[2]);
		int size = Integer.parseInt(args[3]);
		double rate = Double.parseDouble(args[4]);
		RMemcachedServer.threadCount = threads;

		byte[] obj = new byte[size];
		for (int i = 0; i < size; i++) {
			obj[i] = '1';
		}
		String value = new String(obj);
		String[] keys = new String[Nums];
		for (int i = 0; i < Nums; i++) {
			keys[i] = "" + i;
		}
		RMemcachedServer.initFlag = true;
		for (int i = 0; i < threads; i++) {
			bench b = new bench(runs, Nums, i, value, keys, rate);
			b.start();
		}
		while (threadCount != 0 || startIndex < threads*runs*rate){
			//System.out.println("threadCounts= " + threadCount + "   startIndex= " + startIndex);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		getStats();
		time = time/threads;
		status = true;
		requestServer.stop();
		RMemcachedServer.initFlag = false;
		webSession.session = null;
		System.out.println("session finish");
	}
	
	public static JSONArray test(){
		Map<String, String> stats = new HashMap<>();
		stats.put("key", "371");
		stats.put("node", "3");
		stats.put("type", "GET");
		stats.put("value", "111111111111");
		
		JSONArray jsons = new JSONArray();
		jsons.put(stats);
		jsons.put(stats);
		return jsons;
	}
	
	public static JSONArray getResult() {
		JSONArray results = new JSONArray();
		JSONArray arrays = webSession.results;
		int endIndex = arrays.length();
		for (int i = startIndex; i < endIndex && i < startIndex + 1000; i++){
			try {
				JSONObject aResult = (JSONObject) arrays.get(i);
				int nodeNum = Integer.parseInt((String) aResult.get("node"));
				aResult.put("node", getOriginNode(nodeNum));
				results.put(aResult);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		startIndex = endIndex < startIndex+1000 ? endIndex : startIndex+1000;
		System.out.println(results.length());
		return results;
	}
	
	public static void getStats() {
		webSession.getInstance().stats();
		while (webSession.stats.size() != 4){
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (localStats == null || localStats.size() == 0) {
			localStats = webSession.stats;
		} else {
			try {
				for (int i = 0; i < 4; i++){
					int key = (int) Math.pow(2, i);
					JSONObject jStats_new = new JSONObject(webSession.stats.get(key));
					JSONObject jStats_old = new JSONObject(localStats.get(key));
					int cmd_get_old = Integer.parseInt((String) jStats_old.get("cmd_get"));
					int cmd_set_old = Integer.parseInt((String) jStats_old.get("cmd_set"));
					int cmd_get_new = Integer.parseInt((String) jStats_new.get("cmd_get"));
					int cmd_set_new = Integer.parseInt((String) jStats_new.get("cmd_set"));
					int cmd_get = cmd_get_new - cmd_get_old;
					int cmd_set = cmd_set_new - cmd_set_old;
					Map<String, Integer> node = new HashMap<>();
					node.put("node", i);
					node.put("get", cmd_get);
					node.put("set", cmd_set);
					nodeStats.put(node);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		webSession.stats = new HashMap<>();
	}
	
	public static boolean initConfig() {
		if (m_mapMemcachedClient != null && m_mapMemcachedClient.size() >0 ){
			return true;
		}
		m_mapMemcachedClient = new HashMap<Integer, ClientConfig>();
		String path = ORIGINPATH + "client.xml";
		readClientsXML(path);
		return true;
	}

	private static class bench extends Thread {
		private int runs;
		@SuppressWarnings("unused")
		private int threadNum;
		private String object;
		private String[] keys;
		@SuppressWarnings("unused")
		private int size;
		private int nums;
		private double rate;

		public bench(int runs,int nums, int threadNum, String object, String[] keys, double rate) {
			this.runs = runs;
			this.threadNum = threadNum;
			this.object = object;
			this.keys = keys;
			this.size = object.length();
			this.nums = nums;
			this.rate = rate;
		}

		public void run() {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long time = 0;
			time = System.nanoTime();
			randReadWrite(rate);
			RMemcachedServer.threadCount --;
			time = System.nanoTime() - time;
			RMemcachedServer.time += time;
			//System.out.println(time / 1000000000.0f);
		}

		public void randReadWrite(double scale) {
			Random randNum = new Random();
			int getCount = (int) (runs*scale);
			int setCount = runs - getCount;
			for (int i = 0, j = 0, k = 0; i < runs; i++) {
				if ((Math.random() < scale || j >= setCount) && k <= getCount) {
					webSession.getInstance().get(keys[randNum.nextInt(nums)]);
					k ++;
				} else {
					webSession.getInstance().set(keys[randNum.nextInt(nums)], object);
					j ++;
				}
				try {
					Thread.sleep((long) 0.00001);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean readClientsXML(String str) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			factory.setIgnoringElementContentWhitespace(true);

			DocumentBuilder db = factory.newDocumentBuilder();
			Document xmldoc = db.parse(new File(str));
			Element elmtInfo = xmldoc.getDocumentElement();
			NodeList nodes = elmtInfo.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node result = nodes.item(i);
				if (result.getNodeType() == Node.ELEMENT_NODE
						&& result.getNodeName().equals("client")) {
					NodeList ns = result.getChildNodes();
					ClientConfig localClient = new ClientConfig();
					int m = 0;
					for (int j = 0; j < ns.getLength(); j++) {
						Node record = ns.item(j);
						if (record.getNodeType() == Node.ELEMENT_NODE) {
							if (record.getNodeName().equals("id")) {
								m++;
								localClient.id = Integer.decode(record
										.getTextContent());
							} else if (record.getNodeName().equals("host")) {
								m++;
								localClient.host = record.getTextContent();
							} else if (record.getNodeName().equals("client_port")) {
								m++;
								localClient.client_port = Integer.decode(record
										.getTextContent());
							} else if (record.getNodeName().equals("memcached")) {
								m++;
								localClient.memcached = record.getTextContent();
							}
						}
					}
					if (m == 4) {
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
	
	public static String getOriginNode(int num) {
		String out = "";
		while (num != 0){
			int n = getMax(num);
			num = num % (int)Math.pow(2, n);
			out += out==""?"":"-";
			out += n;
		}
		return out;
	}
	private static int getMax(int num){
		int n = -1;
		while (num != 0){
			num = num / 2;
			n ++;
		}
		return n;
	}
}
