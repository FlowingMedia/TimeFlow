package timeflow.util;

import java.util.*;

public class TimeIt {
	public static long last;
	static HashMap<Object, Long> marks=new HashMap<Object, Long>();
	
	public static void mark()
	{
		last=System.currentTimeMillis();
	}
	
	public static void sinceLast()
	{
		long now=System.currentTimeMillis();
		System.out.println("TimeIt: "+(now-last));
		last=now;
	}
	
	public static void since(Object o)
	{
		long now=System.currentTimeMillis();
		System.out.println("TimeIt: "+o+": "+(now-last));
		last=now;
	}
	
	public static void mark(Object o)
	{
		long now=System.currentTimeMillis();
		marks.put(o, System.currentTimeMillis());
		last=now;
	}
}
