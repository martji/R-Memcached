package cn.org.act.sdp.tpcw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class CassandraRSMClient {
	
	public static Properties properties=null;
	public static ServerSocket ss_coo = null;
	public static Integer mutex = new Integer(2) ;
	//public static Thread modify_Coo_thread = null;
	
	private int port;
    private String serverAddress= null;
	
	static
	{
			properties= new Properties();			
			
			//���������ļ�
			String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
			int index = path.lastIndexOf('/');
			path = path.subSequence(0, index-1).toString();
			index = path.lastIndexOf('/');
			path = path.subSequence(0, index).toString();
			path = path+"/conf/CassandraRSM.property";
			
			System.out.println(path);
			
			File pfile=new File(path);	

			
			//�����ļ���Properties
			try {
				properties.load(new FileInputStream(pfile));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			String pp = properties.getProperty("server.port");
			try {
				ss_coo = new ServerSocket(Integer.parseInt(pp));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			/*
			 * �����޸�coordinator��ip���߳�
			 */
			new Thread(){
				public void run()
				{
					try
					{
						while(true)
						{
							Socket s = CassandraRSMClient.ss_coo.accept();
							BufferedReader bReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
							PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
							
							String recv = null;
							String ip = null;
							while((recv=bReader.readLine())!=null)
							{
								if(recv.startsWith("NEW"))
								{
									ip = recv.split(" ")[1];
									
									synchronized (mutex) {
										CassandraRSMClient.properties.setProperty("coordinator", ip+":7000");
									}
									
									break;
								}
							}
							
							System.out.println("��coordinator��"+properties.getProperty("coordinator"));
							
							pw.println("OK");
							pw.flush();
							
							pw.close();
							bReader.close();
							s.close();
						}
					}catch(Exception e )
					{
						e.printStackTrace();
					}
				}
			}.start();
			
	}
	
	public CassandraRSMClient()
	{
		String cstr;
		
		// ���޸�coordinator���̻߳���ʹ�������ļ�
		//ȷ��coordinator��ip��port
		synchronized (mutex) {
			cstr = properties.getProperty("coordinator");
		}
		
		System.out.println(cstr);
		
		String[] ip = cstr.split(":");
		serverAddress = ip[0];
		port = Integer.parseInt(ip[1]);
	}
	
	
	public static void main(String[] args) {   
		
		//System.out.println(TPCW_Database.GetPassword("OGINRI"));
		new CassandraRSMClient().addOrder(195787, 345.61);
    }
	

	
	public boolean addOrder(int OID, double val)
	{
		 Socket socket = null;
		 String wstr="WRITE("+String.valueOf(OID)+","+String.valueOf(val)+")";
		 
		 try {                    
             socket = new Socket(serverAddress, port);
             // ���ʹ洢����
             OutputStream socketOut = socket.getOutputStream();
             PrintWriter printWriter=new PrintWriter(socketOut,true);
             printWriter.println(wstr); 
             printWriter.flush();
             
             System.out.println(wstr);
             
             // ���շ������ķ���
             BufferedReader br = new BufferedReader(
                     new InputStreamReader(socket.getInputStream()));
             String msg = null;
             while ((msg = br.readLine()) != null)
             {
            	 System.out.println(msg);
            	 break;
             }
             
             return true;
         } catch (IOException e) {    
        	 e.printStackTrace();
             System.out.println("Server shutdown!");
         } finally
         {
        	 try{
        		 if( socket != null )
        			 socket.close();
        	 }catch (Exception e){}
         }
        return false;
	}
}
