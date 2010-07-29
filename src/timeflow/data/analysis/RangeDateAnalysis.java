package timeflow.data.analysis;

import java.sql.Date;

import timeflow.data.analysis.DBAnalysis.*;
import timeflow.data.db.*;
import timeflow.data.db.filter.*;
import timeflow.data.time.RoughTime;

public class RangeDateAnalysis implements FieldAnalysis {

	String[] description;
	
	@Override
	public String getName() {
		return "Date Range";
	}

	@Override
	public String[] getResultDescription() {
		return description;
	}

	@Override
	public InterestLevel perform(ActList acts, Field field) {
		long low=0;
		long high=0;

		boolean defined=false;
		for (Act a: acts)
		{
			if (a.get(field)==null)
				continue;
			long x=a.getTime(field).getTime();
			if (defined)
			{
				low=Math.min(low,x);
				high=Math.max(high, x);
			} else
			{
				defined=true;
				low=x;
				high=low;
			}
		}
		if (defined)
			description= new String[]
	  		{
	  			"Lowest value: "+new Date(low),
	  			"Highest value: "+new Date(high),
	  		};
		else
			description=new String[] {"No values defined."};
		
		return InterestLevel.INTERESTING;
	}

	@Override
	public boolean canHandleType(Class type) {
		return type==RoughTime.class;
	}
}
