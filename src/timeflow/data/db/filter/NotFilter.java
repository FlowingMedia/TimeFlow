package timeflow.data.db.filter;

import java.util.*;

import timeflow.data.db.Act;

public class NotFilter extends ActFilter {
	private ActFilter f;
	
	public NotFilter(ActFilter f)
	{
		this.f=f;
	}

	@Override
	public boolean accept(Act act) {
		return f!=null && !f.accept(act);
	}
	
	public int countFilters()
	{
		return 1+f.countFilters();
	}
}
