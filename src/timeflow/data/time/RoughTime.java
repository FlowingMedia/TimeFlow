package timeflow.data.time;

import java.util.Calendar;
import java.util.Date;

public class RoughTime implements Comparable {

	public static final long UNKNOWN=Long.MIN_VALUE;
	private TimeUnit units;
	private long time;
		
	public RoughTime(TimeUnit units)
	{
		time=UNKNOWN;
		this.units=units;
	}
	
	public RoughTime(long time, TimeUnit units)
	{
		this.time=time;
		this.units=units;
	}
	
	public boolean isDefined()
	{
		return time!=UNKNOWN;
	}
	
	public long getTime()
	{
		return time;
	}
	
	public void setTime(long time)
	{
		this.time=time;
	}
	
	public Date toDate()
	{
		return new Date(time);
	}
	
	public boolean after(RoughTime t)
	{
		return t.time<time;
	}
	
	public boolean before(RoughTime t)
	{
		return t.time>time;
	}
	
	public RoughTime plus(int numUnits)
	{
		return plus(units, numUnits);
	}
	
	public RoughTime plus(TimeUnit unit, int times)
	{
		RoughTime r=copy();
		unit.addTo(r,times);
		return r;
	}
	
	public String toString()
	{
		if (isKnown())
			return new Date(time).toString();
		return "unknown";
	}
	
	public boolean isKnown()
	{
		return time!=UNKNOWN;
	}
	
	public boolean equals(Object o)
	{
		if (!(o instanceof RoughTime))
			return false;
		RoughTime t=(RoughTime)o;
		return t.units==units  && t.time==time;
	}
	
	public RoughTime copy()
	{
		RoughTime t=new RoughTime(time, units);
		return t;
	}
	
	public void setUnits(TimeUnit units)
	{
		this.units=units;
	}

	public TimeUnit getUnits() {
		return units;
	}
	
	public String format()
	{
		return units.formatFull(time);
	}
	
	public static int compare(RoughTime t1, RoughTime t2)
	{
		if (t1==t2)
			return 0;
		if (t1==null)
			return -1;
		if (t2==null)
			return 1;
		long dt= t1.time-t2.time;
		if (dt==0)
			return 0;
		if (dt>0)
			return 1;
		return -1;
	}

	@Override
	public int compareTo(Object o) {
		return compare(this, (RoughTime)o);
	}


}
