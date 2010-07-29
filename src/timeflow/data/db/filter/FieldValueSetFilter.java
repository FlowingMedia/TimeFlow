package timeflow.data.db.filter;

import timeflow.data.db.*;

import java.util.*;

public class FieldValueSetFilter extends ActFilter implements ValueFilter {

	private Field field;
	private Set valueSet;
	
	public FieldValueSetFilter(Field field)
	{
		this.field=field;
		valueSet=new HashSet();
	}
	
	public void addValue(Object value)
	{
		valueSet.add(value);
	}
	
	@Override
	public boolean ok(Object o) {
		if (o instanceof Object[])
		{
			Object[] s=(Object[] )o;
			for (int i=0; i<s.length; i++)
				if (valueSet.contains(s[i]))
					return true;
		}
		return valueSet.contains(o);
	}
	
	@Override
	public boolean accept(Act act) {
		return ok(act.get(field));
	}

}
