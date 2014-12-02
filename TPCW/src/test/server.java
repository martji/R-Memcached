package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

	public static void confirm()
	{
		ServerSocket ss = null ;
		
		try
		{
			ss = new ServerSocket(8821);
			while( true )
			{
				System.out.print("1");
				Socket s = ss.accept();
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String wstr = br.readLine();
				System.out.println(wstr);
				PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
				pw.println("has recived!");
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		confirm();
	}
}
