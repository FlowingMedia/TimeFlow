package timeflow.data.time;

import java.util.*;

public class Interval {
	public long start;
	public long end;
	
	public Interval(long start, long end)
	{
		this.start=start;
		this.end=end;
	}
	
	public Interval copy()
	{
		return new Interval(start, end);
	}
	
	public boolean contains(long x)
	{
		return x>=start && x<=end;
	}
	
	public boolean intersects(Interval x)
	{
		return intersects(x.start, x.end);
	}
	
	public boolean intersects(long start1, long end1)
	{
		return start1<=end && end1>=start;
	}
	
	
	public Interval subinterval(double startFraction, double endFraction)
	{
		return new Interval((long)(start+startFraction*length()),
				            (long)(start+endFraction*length()));
	}
	
	public void setTo(long start, long end)
	{
		this.start=start;
		this.end=end;
	}
	
	public void setTo(Interval t)
	{
		start=t.start;
		end=t.end;
	}
	
	public void include(long time)
	{
		start=Math.min(start, time);
		end=Math.max(end, time);
	}
	
	public void include(Interval t)
	{
		include(t.start);
		include(t.end);
	}
	
	public void expand(long amount)
	{
		start-=amount;
		end+=amount;
	}
	
	
	public void add(long amount)
	{
		start+=amount;
		end+=amount;
	}
	
	public long length()
	{
		return end-start;
	}
	
	public void translateTo(long newStart)
	{
		add(newStart-start);
	}
	
	public Interval intersection(Interval i)
	{
		start=Math.max(i.start, start);
		end=Math.min(i.end, end);
		return this;
	}
	
	public void clampInside(Interval container)
	{
		if (length()>container.length())
			throw new IllegalArgumentException("Containing interval too small: "+container+" < "+this);
		if (start>=container.start && end<=container.end)
			return;
		add(Math.max(0, container.start-start));
		add(Math.min(0, container.end-end));
	}
	
	public String toString()
	{
		return "[Interval: From "+new Date(start)+" to "+new Date(end)+"]";
	}

}
