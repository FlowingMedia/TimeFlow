package timeflow.util;

import java.util.*;

public class DoubleBag<T> implements Iterable<T> {
	private HashMap<T, Count> table;
	private double max;
	
	public DoubleBag()
	{
		table=new HashMap<T, Count>();
	}

	public double getMax()
	{
		return max;
	}
	
	public List<T> listTop(int n, boolean useSum)
	{
		int count=0;
		Iterator<T> i=list(useSum).iterator();
		List<T> top=new ArrayList<T>();
		while (count<n && i.hasNext())
		{
			top.add(i.next());
			count++;
		}
		return top;
	}
	
	public List<T> list(final boolean useSum)
	{
		List<T> result=new ArrayList<T>();
		result.addAll(table.keySet());
		
		Collections.sort(result, new Comparator<T>()
				{
					public int compare(T x, T y)
					{
						double d= useSum ? num(y)-num(x) : average(y)-average(x);
						return d>0 ? 1 : (d<0 ? -1 : 0);
					}
				});
		return result;
	}
	
	public double num(T x)
	{
		Count c=table.get(x);
		if (c!=null)
			return c.num;
		else
			return 0;
	}
	
	public double average(T x)
	{
		Count c=table.get(x);
		return c.num/c.vals;
	}
	
	public void add(T x, double z)
	{
		if (Double.isNaN(z))
			return;
		Count c=table.get(x);
		double sum=z;
		if (c!=null)
		{
			 c.add(z);
			 sum=c.num;
		}
		else
		{
			table.put(x, new Count(z));
		}
		max=Math.max(sum, max);
	}
	
	class Count
	{
		double num;
		int vals;
		
		public Count(double num)
		{
			this.num=num;
			vals=1;
		}
		
		public double add(double x)
		{
			vals++;
			return num+=x;
		}
	}
	
	public int size()
	{
		return table.size();
	}
	
	
	public List<T> unordered()
	{
		List<T> result=new ArrayList<T>();
		result.addAll(table.keySet());
		return result;
	}

	
	public int removeLessThan(int cut)
	{
		
		Set<T> small=new HashSet<T>();
		for (T x: table.keySet())
		{
			if (num(x)<cut)
				small.add(x);
		}
		for (T x:small)
			table.remove(x);
		return small.size();
	}
	
	public static void main(String[] args)
	{
		DoubleBag<String> b=new DoubleBag<String>();
		b.add("a",1);
		b.add("b",2);
		b.add("a",3);
		System.out.println(b.num("a"));
		System.out.println(b.num("b"));
		System.out.println(b.num("c"));
		List<String> s=b.list(true);
		for (int i=0; i<s.size(); i++)
			System.out.println(s.get(i)+": "+b.num(s.get(i)));
	}

	@Override
	public Iterator<T> iterator() {
		return table.keySet().iterator();
	}
}
