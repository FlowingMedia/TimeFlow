package timeflow.data.db;

import timeflow.data.db.filter.*;
import timeflow.data.time.*;
import timeflow.util.*;

import java.util.*;

public class DBUtils {
	
	public static void dump(Act act)
	{
		List<Field> fields=act.getDB().getFields();
		for (Field f: fields)
		{
			System.out.println(f.getName()+" = "+act.get(f));
		}
	}
	
	public static Object get(Act act, String field)
	{
		return act.get(act.getDB().getField(field));
	}
	
	public static List<String> getFieldAliases(ActDB db)
	{
		ArrayList<String> list=new ArrayList<String>();
		for (String s: db.getFieldKeys())
			if (!db.getField(s).getName().equals(s))
				list.add(s);
		return list;
	}
	
	public static Interval range(ActList a, Field[] fields)
	{
		if (fields==null || fields.length==0)
			return new Interval(0,0);
		Interval t=null;
		for (Act act:a)
		{
			for (Field f: fields)
			{
				RoughTime d=act.getTime(f);
				if (d!=null && d.isDefined())
				{
					if (t==null)
						t=new Interval(d.getTime(), d.getTime());
					else
						t.include(d.getTime());
				}
			}
		}
		return t!=null ? t : new Interval(RoughTime.UNKNOWN, RoughTime.UNKNOWN);
	}
	
	public static Interval range(ActList a, String fieldName)
	{
		
		Field field=a.getDB().getField(fieldName);
		if (field==null || a.size()==0)
		{
			return new Interval(0,0);
		}
		Interval t=null;
		for (Act act:a)
		{
			RoughTime d=act.getTime(field);
			if (d!=null && d.isDefined())
			{
				if (t==null)
					t=new Interval(d.getTime(), d.getTime());
				else
					t.include(d.getTime());
			}
		}
		return t!=null ? t : new Interval(RoughTime.UNKNOWN, RoughTime.UNKNOWN);
	}
	
	public static List<Field> categoryFields(ActDB db)
	{
		List<Field> list=new ArrayList<Field>();
		list.addAll(db.getFields(String.class));
		list.addAll(db.getFields(String[].class));
		return list;
	}
	
	public static int count(Iterable<Act> acts, Interval i, Field field)//String fieldName)
	{
		return count(acts, new TimeIntervalFilter(i, field));
	}
	
	public static int count(Iterable<Act> acts, ActFilter filter)
	{
		int num=0;
		for (Act a: acts)
			if (filter.accept(a))
				num++;
		return num;
	}

	public static double[] minmax(Iterable<Act> acts, Field field)
	{
		double min=Double.NaN;
		double max=min;
		for (Act a: acts)
		{
			double x=a.getValue(field);
			if (Double.isNaN(min))
			{
				min=x;
				max=x;
			}
			else if (!Double.isNaN(x))
			{
				min=Math.min(x, min);
				max=Math.max(x, max);
			}
		}
		return new double[] {min, max};
	}
	
	public static double[] getValues(Iterable<Act> acts, Field field)
	{
		ArrayList<Double> list=new ArrayList<Double>();
		if (field.getType()==Double.class)
		{
			for (Act a: acts)
				list.add(a.getValue(field));
		}
		else if (field.getType()==RoughTime.class)
		{
			for (Act a: acts)
			{
				RoughTime r=a.getTime(field);
				if (r!=null)
					list.add(new Double(r.getTime()));
			}
		}
		int n=list.size();
		double[] x=new double[n];
		for (int i=0; i<n; i++)
			x[i]=list.get(i);
		return x;
	}
	
	public static Bag<String> countValues(Iterable<Act> acts, Field field)
	{
		Bag<String> bag=new Bag<String>();
		if (field.getType()==String.class)
		{
			for (Act a: acts)
				bag.add(a.getString(field));
		}
		else if (field.getType()==String[].class)
		{
			for (Act a: acts)
			{
				String[] s=a.getTextList(field);
				if (s!=null)
					for (int i=0; i<s.length; i++)
						bag.add(s[i]);
			}
		}
		else
			throw new IllegalArgumentException("Asked to count values for non-text field: "+field);
		return bag;
	}
	
	public static void setRecSizesFromCurrent(ActDB db)
	{
		// for String fields.
		for (Field f: db.getFields(String.class))
		{
			int max=0;
			for (Act a: db)
			{
				String s=a.getString(f);
				if (s!=null)
					max=Math.max(s.length(), max);
			}
			f.setRecommendedSize(max);
		}
	}

	public static Field ensureField(ActDB db, String name, Class type)
	{
		Field f=db.getField(name);
		if (f==null)
		{
			return db.addField(name, type);
		}
		else
		{
			if (f.getType()!=type)
				throw new IllegalArgumentException("Mismatched types: got "+type+", expected "+f.getType());
		}
		return f;
	}
}
