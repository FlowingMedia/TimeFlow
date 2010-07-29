package timeflow.data.db.filter;

import timeflow.data.db.*;
import timeflow.data.time.*;

public class TimeIntervalFilter extends ActFilter {
	
	Interval interval;
	Field timeField;
	boolean acceptNull;
	
	public TimeIntervalFilter(long start, long end, boolean acceptNull, Field timeField)
	{
		this.interval=new Interval(start, end);
		this.acceptNull=acceptNull;
		this.timeField=timeField;
	}
	
	public TimeIntervalFilter(Interval interval, Field timeField)
	{
		this.interval=interval;
		this.timeField=timeField;
	}

	@Override
	public boolean accept(Act act) {
		if (timeField==null)
			return false;
		RoughTime t=act.getTime(timeField);
		if (t==null)
			return acceptNull;
		return interval.contains(t.getTime());
	}

}
