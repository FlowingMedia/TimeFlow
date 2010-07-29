package timeflow.data.db.filter;

import java.util.*;

import timeflow.data.db.Act;

public class OrFilter extends ActFilter {
	private List<ActFilter> filters=new ArrayList<ActFilter>();
	
	public OrFilter(ActFilter a, ActFilter b)
	{
		or(a);
		or(b);
	}
	
	public void or(ActFilter a)
	{
		filters.add(a);
	}

	@Override
	public boolean accept(Act act) {
		for (ActFilter f: filters)
			if (f.accept(act))
				return true;
		return false;
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
