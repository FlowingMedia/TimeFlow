package timeflow.util;

import java.util.*;

public class Bag<T> implements Iterable<T> {
	HashMap<T, Count> table;
	int max;
	
	public Bag()
	{
		table=new HashMap<T, Count>();
	}
	
	public Bag(Iterable<T> i)
	{
		for (T x:i)
			add(x);
	}
	
	public Bag(T[] array)
	{
		for (int i=0; i<array.length; i++)
			add(array[i]);
	}
	
	public int getMax()
	{
		return max;
	}
	
	public List<T> listTop(int n)
	{
		int count=0;
		Iterator<T> i=list().iterator();
		List<T> top=new ArrayList<T>();
		while (count<n && i.hasNext())
		{
			top.add(i.next());
			count++;
		}
		return top;
	}
	
	public List<T> unordered()
	{
		List<T> result=new ArrayList<T>();
		result.addAll(table.keySet());
		return result;
	}
	
	public List<T> list()
	{
		List<T> result=new ArrayList<T>();
		result.addAll(table.keySet());
		
		Collections.sort(result, new Comparator<T>()
				{
					public int compare(T x, T y)
					{
						return num(y)-num(x);
					}
				});
		return result;
	}
	
	public int num(T x)
	{
		Count c=table.get(x);
		if (c!=null)
			return c.num;
		else
			return 0;
	}
	
	public int add(T x)
	{		
		Count c=table.get(x);
		int n=0;
		if (c!=null)
			n=++c.num;
		else
		{
			table.put(x, new Count(1));
			n=1;
		}
		max=Math.max(n,max);
		return n;
	}
	
	class Count
	{
		int num;
		public Count(int num)
		{
			this.num=num;
		}
	}
	
	public int size()
	{
		return table.size();
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
		Bag<String> b=new Bag<String>();
		b.add("a");
		b.add("b");
		b.add("a");
		System.out.println(b.num("a"));
		System.out.println(b.num("b"));
		System.out.println(b.num("c"));
		List<String> s=b.list();
		for (int i=0; i<s.size(); i++)
			System.out.println(s.get(i)+": "+b.num(s.get(i)));
	}

	@Override
	public Iterator<T> iterator() {
		return table.keySet().iterator();
	}
}
