package timeflow.data.db.filter;

import timeflow.data.db.Act;

public abstract class ActFilter {
	public abstract boolean accept(Act act);
	
	// in earlier versions we've wanted the UI to count the number of filters applied.
	// because of the hierarchical way filters are defined, we need this method.
	public int countFilters()
	{
		return 1;
	}
}
