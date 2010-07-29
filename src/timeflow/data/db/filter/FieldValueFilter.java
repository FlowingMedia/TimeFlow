package timeflow.data.db.filter;

import timeflow.data.db.*;

public class FieldValueFilter extends ActFilter implements ValueFilter {

	private Field field;
	private Object value;
	
	public FieldValueFilter(Field field, Object value)
	{
		this.field=field;
		this.value=value;
	}
	
	public boolean ok(Object o)
	{
		if (o==null)
			return value==null;
		if (o.equals(value))
			return true;
		if (o instanceof Object[])
		{
			Object[] s=(Object[] )o;
			for (int i=0; i<s.length; i++)
				if (s[i].equals(value))
					return true;
		}
		return false;
	}
	
	@Override
	public boolean accept(Act act) {
		return ok(act.get(field));	
	}

}
