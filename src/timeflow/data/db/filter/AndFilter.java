package timeflow.data.db.filter;

import java.util.*;

import timeflow.data.db.Act;

public class AndFilter extends ActFilter {
	private List<ActFilter> filters;
	
	public AndFilter()
	{		
	}
	
	public AndFilter(ActFilter a, ActFilter b)
	{
		filters=new ArrayList<ActFilter>();
		and(a);
		and(b);
	}
	
	public void and(ActFilter a)
	{
		if (a==null)
			return;
		if (filters==null)
			filters=new ArrayList<ActFilter>();
		filters.add(a);
	}

	@Override
	public boolean accept(Act act) {
		if (filters!=null)
			for (ActFilter f: filters)
				if (!f.accept(act))
					return false;
		return true;
	}
	
	public int countFilters()
	{
		int sum=0;
		if (filters!=null)
			for (ActFilter f: filters)
				if (f!=null)
					sum+=f.countFilters();
		return sum;
	}
	
	
}
