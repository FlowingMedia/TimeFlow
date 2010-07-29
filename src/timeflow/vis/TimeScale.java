package timeflow.vis;

import timeflow.data.time.*;

import java.util.*;

public class TimeScale {
	
	private double low,high;
	private Interval interval;
	
	public TimeScale()
	{
		low=0;
		high=100;
		interval=new Interval(new Date(0).getTime(),new Date().getTime());
	}
	
	public Interval getInterval()
	{
		return interval;
	}
	
	public void setNumberRange(double low, double high)
	{
		this.low=low;
		this.high=high;		
	}
	
	public void setDateRange(Interval t)
	{
		setDateRange(t.start, t.end);
	}
	
	public void setDateRange(long first, long last)
	{	
		interval.setTo(first, last);
	}

	
	public boolean containsDate(long date)
	{
		return interval.contains(date);
	}
	
	public boolean containsNum(double x)
	{
		return x>=low && x<=high;
	}
	
	public long duration()
	{
		return interval.length();
	}
	
	public double toNum(long time)
	{
		return low+(high-low)*(time-interval.start)/(double)duration();
	}
	
	public long spaceToTime(double space)
	{
		return (long)(space*duration()/(high-low));
	}
	
	public int toInt(long time)
	{
		return (int)toNum(time);
	}


	public long toTime(double num)
	{
		double millis=interval.start+duration()*(num-low)/(high-low);
		return (long)millis;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}
	
}
