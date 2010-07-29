package timeflow.data.time;

import java.util.*;
import java.text.*;

public class TimeUnit {

	public static final TimeUnit YEAR=new TimeUnit("Years", Calendar.YEAR, 365*24*60*60*1000L, "yyyy", "yyyy");
	public static final TimeUnit MONTH=new TimeUnit("Months", Calendar.MONTH, 30*24*60*60*1000L, "MMM", "MMM yyyy");
	public static final TimeUnit WEEK=new TimeUnit("Weeks", Calendar.WEEK_OF_YEAR, 7*24*60*60*1000L, "d", "MMM d yyyy");
	public static final TimeUnit DAY=new TimeUnit("Days", Calendar.DAY_OF_MONTH, 24*60*60*1000L, "d", "MMM d yyyy");
	public static final TimeUnit DAY_OF_WEEK=new TimeUnit("Days", Calendar.DAY_OF_WEEK, 24*60*60*1000L, "d", "MMM d yyyy");
	public static final TimeUnit HOUR=new TimeUnit("Hours", Calendar.HOUR_OF_DAY, 60*60*1000L, "kk:mm", "MMM d yyyy kk:mm");
	public static final TimeUnit MINUTE=new TimeUnit("Minutes", Calendar.MINUTE, 60*1000L, ":mm", "MMM d yyyy kk:mm");
	public static final TimeUnit SECOND=new TimeUnit("Seconds", Calendar.SECOND, 1000L, ":ss", "MMM d yyyy kk:mm:ss");
	public static final TimeUnit DECADE=multipleYears(10);
	public static final TimeUnit CENTURY=multipleYears(100);
	
	private static final double DAY_SIZE=24*60*60*1000L;
	
	private int quantity;	
	private long roughSize;
	private SimpleDateFormat format, fullFormat;
	private String name;
	private int calendarCode;
	
	private TimeUnit()
	{		
	}
	
	private TimeUnit(String name, int calendarCode, long roughSize, String formatPattern, String fullFormatPattern)
	{
		this.name=name;
		this.calendarCode=calendarCode;
		this.roughSize=roughSize;
		format=new SimpleDateFormat(formatPattern);
		fullFormat=new SimpleDateFormat(fullFormatPattern);
		quantity=1;
	}
	
	public String toString()
	{
		return "[TimeUnit: "+name+"]";
	}

	public static TimeUnit multipleYears(int numYears)
	{
		TimeUnit t=new TimeUnit();
		t.name=numYears+" Years";
		t.calendarCode=Calendar.YEAR;
		t.roughSize=YEAR.roughSize*numYears;
		t.format=YEAR.format;
		t.fullFormat=YEAR.fullFormat;
		t.quantity=numYears;
		return t;
	}
	
	public static TimeUnit multipleWeeks(int num)
	{
		TimeUnit t=new TimeUnit();
		t.name=num+" Weeks";
		t.calendarCode=Calendar.WEEK_OF_YEAR;
		t.roughSize=WEEK.roughSize*num;
		t.format=WEEK.format;
		t.fullFormat=WEEK.fullFormat;
		t.quantity=num;
		return t;
	}
	
	public TimeUnit times(int quantity)
	{
		TimeUnit t=new TimeUnit();
		t.name=quantity+" "+this.name;
		t.calendarCode=this.calendarCode;
		t.roughSize=this.roughSize*quantity;
		t.format=this.format;
		t.fullFormat=this.fullFormat;
		t.quantity=quantity;
		return t;
		
	}

	
	public int numUnitsIn(TimeUnit u)
	{
		return (int)Math.round(u.getRoughSize()/(double)getRoughSize());
	}
	
	public boolean isDayOrLess()
	{
		return roughSize <= 24*60*60*1000L;
	}
	
	public RoughTime roundDown(long timestamp)
	{
		return round(timestamp, false);
	}
	
	public RoughTime roundUp(long timestamp)
	{
		return round(timestamp, true);
	}
	
	private static final int[] calendarUnits={Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY, Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR};
	public RoughTime round(long timestamp, boolean up)
	{
		Calendar c=TimeUtils.cal(timestamp);
		
		if (calendarCode==Calendar.WEEK_OF_YEAR )
		{
			c.set(Calendar.DAY_OF_WEEK, c.getMinimum(Calendar.DAY_OF_WEEK));
		}
		else
		{
		
			// set to minimum all fields of finer granularity.
			int roundingCode=calendarCode;
			if (calendarCode==Calendar.WEEK_OF_YEAR || calendarCode==Calendar.DAY_OF_WEEK)
				roundingCode=Calendar.DAY_OF_MONTH;
			for (int i=0; i<calendarUnits.length; i++)
			{
				if (calendarUnits[i]==roundingCode)
					break;
				if (i==calendarUnits.length-1)
					throw new IllegalArgumentException("Unsupported Calendar Unit: "+calendarCode);
				c.set(calendarUnits[i], c.getMinimum(calendarUnits[i]));
			}
			if (quantity>1)
			{
				c.set(calendarCode, quantity*(c.get(calendarCode)/quantity));
			}
		}
		
		// if rounding up, then add a unit at current granularity.
		if (up)
			c.add(calendarCode, quantity);
		
		return new RoughTime(c.getTimeInMillis(), this);
	}
	
	public int get(long timestamp)
	{
		Calendar c= TimeUtils.cal(timestamp);
		int n=c.get(calendarCode);
		return quantity==1 ? n : n%quantity;
	}
	
	public void addTo(RoughTime r)
	{
		addTo(r,1);
	}
	
	public void addTo(RoughTime r, int times)
	{
		Calendar c=TimeUtils.cal(r.getTime());
		c.add(calendarCode, quantity*times);
		r.setTime(c.getTimeInMillis());
	}
	
	// Finding the difference between two dates, in a given unit of time,
	// is much subtler than you'd think! And annoyingly, the Calendar class does not do
	// this for you, even though it actually "knows" how to do so since it
	// can add fields.
	//
	// The most vexing problem is dealing with daylight savings time,
	// which means that one day a year has 23 hours and one day has 25 hours.
	// We also have to handle the fact that months and years aren't constant lengths.
	//
	// Rather than write all this ourselves, in this code we
	// use the Calendar class to do the heavy lifting.
	public long difference(long x, long y)
	{
		// If this is not one of the hard cases,
		// just divide the timespan by the length of time unit.
		// Note that we're not worrying about hours and daylight savings time.
		if (calendarCode!=Calendar.YEAR && calendarCode!=Calendar.MONTH && 
		   calendarCode!=Calendar.DAY_OF_MONTH && calendarCode!=Calendar.DAY_OF_WEEK &&
		   calendarCode!=Calendar.WEEK_OF_YEAR)
		{
			return (x-y)/roughSize;
		}
			
		Calendar c1=TimeUtils.cal(x), c2=TimeUtils.cal(y); 
		int diff=0;
		switch (calendarCode)
		{
			case Calendar.YEAR:
				return (c1.get(Calendar.YEAR)-c2.get(Calendar.YEAR))/quantity;
				
			case Calendar.MONTH:
				diff= 12*(c1.get(Calendar.YEAR)-c2.get(Calendar.YEAR))+
				              c1.get(Calendar.MONTH)-c2.get(Calendar.MONTH);
				return diff/quantity;
				
			case Calendar.DAY_OF_MONTH:
			case Calendar.DAY_OF_WEEK:
			case Calendar.DAY_OF_YEAR:
			case Calendar.WEEK_OF_MONTH:
			case Calendar.WEEK_OF_YEAR:
				// This is ugly, but believe me, it beats the alternative methods :-)
				// We use the Calendar class's knowledge of daylight savings time.
				// and also the fact that if we calculate this naively, then we aren't going
				// to be off by more than one in either direction.
				int naive=(int)Math.round((x-y)/(double)roughSize);
				c2.add(calendarCode, naive*quantity);
				if (c1.get(calendarCode)==c2.get(calendarCode))
					return naive/quantity;
				c2.add(calendarCode, quantity);
				if (c1.get(calendarCode)==c2.get(calendarCode))
					return naive/quantity+1;
				return naive/quantity-1;
		}
		throw new IllegalArgumentException("Unexpected calendar code: "+calendarCode);
	}

	public long approxNumInRange(long start, long end)
	{
		return 1+(end-start)/roughSize;
	}
	
	public long getRoughSize() {
		return roughSize;
	}

	public String format(Date date)
	{
		return format.format(date);
	}

	public String formatFull(Date date)
	{
		return fullFormat.format(date);
	}

	public String formatFull(long timestamp)
	{
		return fullFormat.format(new Date(timestamp));
	}

	public String getName() {
		return name;
	}
}
