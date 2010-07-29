package timeflow.data.time;

import java.util.*;

public class TimeUtils {
	
	public static Calendar cal(long time)
	{
		Calendar c=new GregorianCalendar();
		c.setTimeInMillis(time);
		return c;
	}

	public static Calendar cal(Date date)
	{
		Calendar c=new GregorianCalendar();
		c.setTime(date);
		return c;
	}
	
}
