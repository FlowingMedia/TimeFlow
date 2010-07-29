package timeflow.format.file;

import java.util.*;

import timeflow.util.*;

import timeflow.model.Display;

public class DelimitedText {
	private char delimiter;
	
	public DelimitedText(char delimiter)
	{
		if (delimiter=='"')
			throw new IllegalArgumentException("Can't use quote as delimiter.");
		this.delimiter=delimiter;
	}
	
	private static boolean isBreak(char c)
	{
		return c=='\n' || c=='\r';
	}
	
	public List<String[]> read(String text)
	{
		ArrayList<String[]> results=new ArrayList<String[]>();
		int n=text.length();
		StringBuffer currentToken=new StringBuffer();
		ArrayList<String> currentList=new ArrayList<String>();
		
		boolean quoted=false;
		for (int i=0; i<n; i++)
		{
			char c=text.charAt(i);
			if (quoted)
			{
				if (c=='"')
				{
					if (i==n-1) // end of file, ignore quote.
					{
						quoted=false;
						continue;
					}
					char next=text.charAt(i+1);
					if (next=='"') // a quoted quote.
					{
						currentToken.append('"');
						i++;
						
						// Alas, there is a weird special case here
						// if the user has pasted from Excel.
						// If a field starts with a quote, and ends with two quotes,
						// it turns out to be ambiguous!
						// Excel doesn't do any escaping on: "blah blah""
						// But, it does escape: blah "\n
						// turning it into: "blah blah""\n
						// So if "blah blah"" occurs at the end of the line,
						// you actually do not know which it is!
						// In practice, our first bug report was for a literal of "blah blah""
						// so that is what we will choose.
						
						//System.out.println("next++:  '"+text.charAt(i+1)+"'="+(int)text.charAt(i+1));
						if (i<n-1 && isBreak(text.charAt(i+1)))
						{
							quoted=false;
						}
						
						continue;
					}
					if (isBreak(next)) // end of line
					{
						quoted=false;
						currentList.add(currentToken.toString());
						currentToken.setLength(0);
						results.add((String[])currentList.toArray(new String[0]));
						currentList=new ArrayList<String>();
						i++;
						if (i<n-1 && isBreak(text.charAt(i+1)))
							i++;
						continue;
					}
					if (next==delimiter)
					{
						quoted=false;
						continue;
					}
					System.out.println("a bad quote from excel: next char="+(int)next);
					quoted=false;
				}
				currentToken.append(c);
				continue;
			}
			
			// ok, not quoted.
			if (c==delimiter)
			{
				currentList.add(currentToken.toString());
				currentToken.setLength(0);
				quoted=false;
				continue;
			}
			
			// not delimiter, not in the middle of a quote.
			if (c=='"')
			{
				if (currentToken.length()==0) // we are at beginning of a token, so this is a quote.
				{
					quoted=true;
					continue;
				}
			}
						
			// is it a line feed? we're not in the middle of a quote, so this means a new line.
			if (c=='\n' || c=='\r' || c=='\f')
			{
				currentList.add(currentToken.toString());
				currentToken.setLength(0);
				results.add((String[])currentList.toArray(new String[0]));
				currentList=new ArrayList<String>();
				if (i<n-1 && (text.charAt(i+1)=='\n' || text.charAt(i+1)=='\r'))
					i++;
				continue;
			}
			
			// by golly, just a normal character!
			currentToken.append(c);
		}
		
		// did it just end in a blank line?
		
		if (currentList.size()>0 || currentToken.toString().trim().length()>0)
		{
			currentList.add(currentToken.toString());
			results.add((String[])currentList.toArray(new String[0]));	
		}
		return results;
	}
	
	public String write(String s)
	{
		return write(new String[] {s});
	}
	
	public String write(String[] data)
	{
		StringBuffer b=new StringBuffer();
		for (int i=0; i<data.length; i++)
		{
			// add a delimiter if necessary.
			if (i>0)
				b.append(delimiter);
			
			// if null, just don't write anything.
			if (data[i]==null)
				continue;
			
			// does it have weird characters in it?
			boolean weird=false;
			int n=data[i].length();
			for (int j=0; j<n; j++)
			{
				char c=data[i].charAt(j);
				if (c==delimiter || isBreak(c))
				{
					weird=true;
					break;
				}
			}
			
			if (weird)
			{
				b.append('"');
				for (int j=0; j<n; j++)
				{
					char c=data[i].charAt(j);
					if (c=='"')
						b.append('"');
					b.append(c);
				}
				b.append('"');
			}
			else
				b.append(data[i]);
		}
		return b.toString();
	}
	
	public static String[] split(String s, char delimiter)
	{
		DelimitedText t= new DelimitedText(delimiter);
		List<String[]> lines=t.read(s);
		return lines.get(0);
	}
	
	public static void main(String[] args) throws Exception
	{
		String bad=IO.read("test/bad-all.txt");
		String[][] s=DelimitedFormat.readArrayFromString(bad, System.out);
		System.out.println("len="+s.length);

		/*
		//DelimitedText c=new DelimitedText(';');
		//List<String[]> arrays=c.read(IO.read("test/bad.txt"));
		//List<String[]> arrays=c.read("a;b;\"x;y\";c");
		//List<String[]> arrays=c.read("a;\"a\n\rq\";b;\"x;y\";c");
		//List<String[]> arrays=c.read("a;b;\"with a \"\"blah\";c\nd;e;f\ng;h;i");
		//List<String[]> arrays=c.read("a,\"b\",\"c\r\nd\"\r\ne,f,g\nh,i,j");
		for (String[] s:arrays)
		{
			System.out.println("["+Display.arrayToString(s)+"]");
		}
		*/
	}
}
