package timeflow.data.analysis;

import timeflow.data.analysis.DBAnalysis.*;
import timeflow.data.db.*;
import timeflow.data.db.filter.*;
import java.text.*;

public class RangeNumberAnalysis implements FieldAnalysis {

	String[] description;
	static DecimalFormat intFormat=new DecimalFormat("###,###,###,###");
	static DecimalFormat df=new DecimalFormat("###,###,###,###.##");
	
	@Override
	public String getName() {
		return "Value Range";
	}

	@Override
	public String[] getResultDescription() {
		return description;
	}

	@Override
	public InterestLevel perform(ActList acts, Field field) {
		double low=0;
		double high=0;
		int numZero=0;
		double sum=0;
		int numDefined=0;

		boolean defined=false;
		for (Act a: acts)
		{
			if (a.get(field)==null)
				continue;

			double x=a.getValue(field);
			numDefined++;
			sum+=x;
			
			if (x==0)
				numZero++;
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
				"Average: "+df.format((sum/numDefined)),
	  			"Lowest: "+df.format(low),
	  			"Highest: "+df.format(high),
	  			"Number of zero values: "+df.format(numZero)
	  		};
		else
			description=new String[] {"No values defined."};
		
		return InterestLevel.INTERESTING;
	}
	
	static String format(double x)
	{
		
		if (Math.abs(x)>.1)
		{
			if (Math.round(x)-x<.01)
				return intFormat.format(x);
			return df.format(x);
		}
		return ""+x;
	}

	@Override
	public boolean canHandleType(Class type) {
		return type==Double.class;
	}
}
