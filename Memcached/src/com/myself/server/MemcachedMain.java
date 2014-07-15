package com.myself.server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.myself.client.Client;
import com.myself.client.ClientMgr;
import common.RegisterHandler;

public class MemcachedMain {
	HashMap<Integer, ClientConfig> m_mapMemcachedClient;

	public boolean initConfig() {
		m_mapMemcachedClient = new HashMap<Integer, ClientConfig>();

		File f = new File(System.getProperty("user.dir"));
		String path = f.getPath() + File.separator + "bin" + File.separator;
		readClientsXML(path + "client.xml");
		return true;
	}

	public int getMemcachedNumber() {
		System.out.print("输入服务编号：");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		return Integer.decode(scanner.next());
	}

	public void start() {
		initConfig();
		int num = getMemcachedNumber();

		RegisterHandler.initHandler();
		memSession.getInstance().start(m_mapMemcachedClient.get(num).memcached);

		ClientMgr clientMgr = ClientMgr.getInstance();
		Server server = Server.getInstance();
		server.init(m_mapMemcachedClient.get(num).client_port);
		clientMgr.init(num, m_mapMemcachedClient);

		Client webClient = new Client();
		webClient.init("192.168.3.201", 8888);
	}

	public static void main(String[] args) {
		MemcachedMain entrance = new MemcachedMain();
		entrance.start();
	}

	// 读取memcached client配置
	public boolean readClientsXML(String str) {
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
							} else if (record.getNodeName().equals(
									"client_port")) {
								m++;
								localClient.client_port = Integer.decode(record
										.getTextContent());
							} else if (record.getNodeName().equals(
									"request_port")) {
								m++;
								localClient.request_port = Integer
										.decode(record.getTextContent());
							} else if (record.getNodeName().equals("memcached")) {
								m++;
								localClient.memcached = record.getTextContent();
							}
						}
					}
					if (m == 5) {
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
