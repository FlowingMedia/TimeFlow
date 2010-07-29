package timeflow.data.analysis;

import timeflow.util.*;
import timeflow.data.time.*;
import timeflow.data.analysis.DBAnalysis.InterestLevel;
import timeflow.data.db.*;
import java.util.*;

public class FrequencyAnalysis implements FieldAnalysis {
	String[] description;
	
	@Override
	public String getName() {
		return "Frequency Of Values";
	}

	@Override
	public String[] getResultDescription() {
		return description;
	}

	@Override
	public InterestLevel perform(ActList acts, Field field) {
		Bag<Object> bag=new Bag<Object>();
		if (field.getType()==String[].class)
		{
			for (Act a: acts)
			{
				String[] tags=a.getTextList(field);
				if (tags!=null)
					for (String tag:tags)
						bag.add(tag);
			}
		}
		else
			for (Act a: acts)
				bag.add(a.get(field));
		
		int numItems=acts.size();
		int numDistinctVals=bag.size();
		int numNullVals=bag.num(null)+bag.num("");
		
		if (numItems==numDistinctVals)
			description=new String[] {"All values are defined and unique."};
		else if (numItems==numDistinctVals+numNullVals-1)
			description=new String[] {"All defined values are unique."};
		else if (numDistinctVals==1)
			description=new String[] {"This field is always equal to "+string(bag.list().get(0))};
		else if (numDistinctVals<4)
		{
			List<Object> all=bag.list();
			description=new String[] {"This field takes only "+all.size()+" values.",
				"which are: "+all};
		}
		else
		{
			List<Object> all=bag.list();
			description=new String[] {"There are "+numDistinctVals+" distinct values.",
					"Most common: \""+string(all.get(0))+"\", occurring "+bag.num(all.get(0))+" times."};
		}
		return InterestLevel.INTERESTING;
	}
	
	private static String[] empty=new String[0];
	static String string(Object o)
	{
		if (o==null || "".equals(o) || empty.equals(o))
			return "[missing]";
		return o.toString();
	}

	@Override
	public boolean canHandleType(Class type) {
		return type==Double.class || type==String.class || type==String[].class;
	}
}
