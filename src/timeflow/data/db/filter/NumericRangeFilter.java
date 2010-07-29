package timeflow.data.db.filter;

import timeflow.data.db.*;
import timeflow.data.time.*;

public class NumericRangeFilter extends ActFilter {
	
	double low, high;
	Field field;
	boolean acceptNull;
	
	public NumericRangeFilter(Field field, double low, double high, boolean acceptNull)
	{
		this.low=low;
		this.high=high;
		this.field=field;
		this.acceptNull=acceptNull;
	}

	@Override
	public boolean accept(Act act) {
		if (field==null)
			return false;
		double x=act.getValue(field);
		return Double.isNaN(x) && acceptNull || x>=low && x<=high;
	}

}
