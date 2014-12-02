package cn.org.act.sdp.tpcw;

public class ValueCounter {
	
	private static long sumofValue=0;
	static int sn = 0;
	
	public static synchronized void addValue(int OID,double val)
	{
		sumofValue+=val;
		sn++;
		System.out.println(sn+ " Now Purchase Order("+OID+") Value is "+val+"$");
	}

}
