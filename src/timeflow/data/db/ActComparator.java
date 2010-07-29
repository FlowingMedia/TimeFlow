package timeflow.data.db;

import java.util.*;

import timeflow.data.time.RoughTime;

public abstract class ActComparator implements Comparator<Act> {
	
	protected Field field;
	protected boolean ascending=true;
	protected String description;
	
	
	private ActComparator(Field field, String description)
	{
		this.field=field;
		this.description=description;
	}
	
	public String getDescription()
	{
		return description + (ascending ? "" : " (descending)");
	}
	
	public static ActComparator by(Field field)
	{
		Class type=field.getType();
		if (type==Double.class)
			return new NumberComparator(field);
		if (type==String[].class)
			return new ArrayComparator(field);
		if (type==RoughTime.class)
			return new TimeComparator(field);
		return new StringComparator(field);
	}
	
	static class TimeComparator extends ActComparator
	{
		
		TimeComparator(Field field)
		{
			super(field, "by time");
		}

		@Override
		public int compare(Act o1, Act o2) {
			RoughTime a1=o1.getTime(field);
			RoughTime a2=o2.getTime(field);
			if (a1==a2)
				return 0;
			if (a1==null)
				return ascending ? 1 : -1;
			if (a2==null)
				return ascending ? -1 : 1;
			int n=a1.compareTo(a2);
			return ascending ? n : -n;
		}
	}

	
	static class ArrayComparator extends ActComparator
	{
		
		ArrayComparator(Field field)
		{
			super(field, "by length of "+field.getName());
		}

		@Override
		public int compare(Act o1, Act o2) {
			int n=length(o1.getTextList(field))-length(o2.getTextList(field));
			return ascending ? n : -n;
		}
		
		static int length(String[] s)
		{
			return s==null ? 0 : s.length;
		}
	}
	
	static class StringComparator extends ActComparator
	{
		
		StringComparator(Field field)
		{
			super(field, "by "+field.getName());
		}

		@Override
		public int compare(Act o1, Act o2) {
			int n=val(o1.getString(field)).toString().compareTo(val(o2.getString(field)).toString());
			return ascending ? n : -n;
		}
		
		String val(String s)
		{
			return s==null ? "" : s;
		}
	}
	
	static class NumberComparator extends ActComparator
	{
		
		NumberComparator(Field field)
		{
			super(field, "by "+field.getName());
		}

		@Override
		public int compare(Act o1, Act o2) {
			double x=o1.getValue(field)-o2.getValue(field);
			int n=x>0 ? 1 : x<0 ? -1 : 0;
			return ascending ? n : -n;
		}
		
		
	}
}
