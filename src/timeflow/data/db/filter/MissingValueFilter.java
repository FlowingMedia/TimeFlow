package timeflow.data.db.filter;

import timeflow.data.db.Act;
import timeflow.data.db.Field;

public class MissingValueFilter extends ActFilter {
	private Field field;
	private boolean text, array, number;
	
	public MissingValueFilter(Field field)
	{
		this.field=field;
		text=field.getType()==String.class;
		array=field.getType()==String[].class;
		number=field.getType()==Double.class;
	}

	@Override
	public boolean accept(Act act) {
		Object o=act.get(field);
		return o==null || 
		      number && Double.isNaN(((Double)o).doubleValue()) ||
		      text && "".equals(o) || 
		      array && ((String[])o).length==0;
	}

}
