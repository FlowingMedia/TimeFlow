package timeflow.data.db.filter;

import timeflow.data.db.Act;

public class ConstFilter extends ActFilter {
	
	boolean result;
	
	public ConstFilter(boolean result)
	{
		this.result=result;
	}

	@Override
	public boolean accept(Act act) {
		return result;
	}
	
	public int countFilters()
	{
		return result ? 0 : 1;
	}

}
