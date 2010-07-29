package timeflow.vis.timeline;

import java.util.*;

import timeflow.data.time.*;

public class AxisTicMarks {
	public TimeUnit unit;
	public List<Long> tics;
	
	private static final TimeUnit[] units={
		TimeUnit.YEAR, TimeUnit.MONTH, TimeUnit.DAY, TimeUnit.HOUR, TimeUnit.MINUTE, TimeUnit.SECOND
	};
	
	private static final TimeUnit[] histUnits={
		TimeUnit.YEAR.times(100), TimeUnit.YEAR.times(50), TimeUnit.YEAR.times(25), 
			TimeUnit.YEAR.times(10), TimeUnit.YEAR.times(5), TimeUnit.YEAR.times(2), TimeUnit.YEAR,
		TimeUnit.MONTH.times(6), TimeUnit.MONTH.times(3), TimeUnit.MONTH.times(2), TimeUnit.MONTH, 
		TimeUnit.WEEK, TimeUnit.DAY.times(2), TimeUnit.DAY,

		TimeUnit.HOUR, 
		TimeUnit.MINUTE, 
		TimeUnit.SECOND
	};
	
	public AxisTicMarks(TimeUnit unit, long start, long end)
	{
		this.unit=unit;
		tics=new ArrayList<Long>();
		RoughTime r=unit.roundDown(start);
		tics.add(r.getTime());
		do
		{
			unit.addTo(r);
			tics.add(r.getTime());
		} while (r.getTime()<end);	
	}
	
	
	
	public static List<AxisTicMarks> allRelevant(Interval interval)
	{
		return allRelevant(interval.start, interval.end);
	}
	
	public static List<AxisTicMarks> allRelevant(long start, long end)
	{
		return allRelevant(start, end, 40);
	}
	
	public static AxisTicMarks histoTics(long start, long end)
	{
		for (int i=histUnits.length-1; i>=0; i--)
		{
			TimeUnit u=histUnits[i];
			long estimate=u.approxNumInRange(start, end);		
			if (estimate<200 || i==0)
			{
				AxisTicMarks t=new AxisTicMarks(u, start, end);
				return t;
			}
		}
		return null;
	}
	
	public static List<AxisTicMarks> allRelevant(long start, long end, long maxTics)
	{
		List<AxisTicMarks> list=new ArrayList<AxisTicMarks>();
		
		
		for (int i=0; i<units.length; i++)
		{
			TimeUnit u=units[i];
			long estimate=u.approxNumInRange(start, end);
			
			if (estimate<maxTics)
			{
				AxisTicMarks t=new AxisTicMarks(u, start, end);
				if (list.size()>0)
				{
					AxisTicMarks last=list.get(0);
					if (last.tics.size()==t.tics.size())
						list.remove(0);
				}
				list.add(t);
				
			}
		}
		while (list.size()>2)
			list.remove(0);
		
		if (list.size()==0) // uh oh! must be many years. we will add in bigger increments.
		{
			long length=end-start;
			long size=365*24*60*60*1000L;
			int m=1;
			maxTics=15;
			while (m<2000000000 && length/(m*size)>maxTics)
			{
				if (length/(2*m*size)<=maxTics)
				{
					m*=2;
					break;
				}
				if (length/(5*m*size)<=maxTics)
				{
					m*=5;
					break;
				}
				m*=10;
			}	
			AxisTicMarks t=new AxisTicMarks(TimeUnit.multipleYears(m), start, end);
			list.add(t);
		}	
		return list;
	}
}
