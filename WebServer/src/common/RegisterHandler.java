package common;

import java.lang.reflect.InvocationTargetException;

import messageBody.clientMsg.nc_Read;
import messageBody.clientMsg.nc_Write;
import messageBody.memcachedmsg.nm_Connected;
import messageBody.requestMsg.nr_Connected_mem_back;
import messageBody.requestMsg.nr_Read_res;
import messageBody.requestMsg.nr_Stats_res;
import messageBody.requestMsg.nr_write_res;

import com.google.protobuf.GeneratedMessage;


public class RegisterHandler 
{
	public static void initHandler()
	{		
		initHandler(EMSGID.nc_read.ordinal(), nc_Read.class);
		initHandler(EMSGID.nc_write.ordinal(), nc_Write.class);
		initHandler(EMSGID.nm_connected.ordinal(), nm_Connected.class);
		initHandler(EMSGID.nr_connected_mem_back.ordinal(), nr_Connected_mem_back.class);
		initHandler(EMSGID.nr_read_res.ordinal(), nr_Read_res.class);
		initHandler(EMSGID.nr_stats_res.ordinal(), nr_Stats_res.class);
		initHandler(EMSGID.nr_write_res.ordinal(), nr_write_res.class);
	}	
	
	private static void initHandler(int id, Class<? extends GeneratedMessage> msgCla) 
	{
		try {
			MessageManager.addMessageCla(id, msgCla);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
