package cn.org.act.sdp.tpcw;

public class IDGenerator {
	
	
	
	private static int O_ID;
	static
	{
		O_ID=TPCW_Database.getNumberofOrders();
	    
	}
	
	
	
	public  synchronized static int getOID()
	{
		O_ID++;
		
		return O_ID;
	}
	
	public static void main(String[] args)
	{
		System.out.println("OID="+IDGenerator.getOID());
	}

}
