package timeflow.format.file;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import timeflow.data.db.Act;
import timeflow.data.db.ActDB;
import timeflow.data.db.DBUtils;
import timeflow.data.db.Field;
import timeflow.data.time.RoughTime;
import timeflow.model.Display;

import timeflow.util.*;

public class DelimitedFormat {
	
	char delimiter;
	DelimitedText delimitedText;

	public DelimitedFormat(char delimiter)
	{
		this.delimiter=delimiter;
		delimitedText=new DelimitedText(delimiter);
	}

	public static String[][] readArrayGuessDelim(String fileName, PrintStream messages) throws Exception
	{
		//messages.println("DelimitedFormat: reading "+fileName);
		String text=IO.read(fileName);
		return readArrayFromString(text, messages);		
	}
	
	public static String[][] readArrayFromString(String text, PrintStream messages) throws Exception
	{
		//messages.println("DelimitedFormat: reading string, length="+text.length());
		int n=Math.min(text.length(), 1000);
		String beginning=text.substring(0,n);
		char c=count(beginning, '\t')>count(beginning, ',') ? '\t' : ',';
		return new DelimitedFormat(c).readTokensFromString(text, messages);
	}
	
	private static String[] removeBlankLines(String[] lines)
	{
		List<String> good=new ArrayList<String>();
		for (int i=0; i<lines.length; i++)
		{
			if (!(lines[i]==null || lines[i].trim().length()==0))
				good.add(lines[i]);
		}
		return (String[])good.toArray(new String[0]);
	}
	
	private static int count(String s, char c)
	{
		int n=0;
		for (int i=0; i<s.length(); i++)
		{
			if (s.charAt(i)==c)
				n++;
		}
		return n;
	}

	public String[][] readTokensFromString(String text, PrintStream messages) throws Exception
	{
		
		ArrayList<String[]> resultList=new ArrayList<String[]>();
		Iterator<String[]> lines=delimitedText.read(text).iterator();
		int numCols=-1;
		while(lines.hasNext())
		{
			String[] r=lines.next();
			int ri=r.length;
			if (numCols==-1)
				numCols=r.length;
			else
			{
				if (ri>numCols)
				{
					messages.println("Line too long: "+ri+" > "+numCols);
					messages.println("line="+Display.arrayToString(r));
				}
				else if (ri<numCols)
				{
					String[] old=r;
					r=new String[numCols];
					System.arraycopy(old,0,r,0,ri);
					for (int j=ri; j<numCols; j++) 
						r[j]="";
				}
			}
			resultList.add(r);
		}
		//messages.println("# lines read: "+resultList.size());
		return (String[][]) resultList.toArray(new String[0][]);
	}
	
	public void write(ActDB db, File file) throws IOException
	{
		write(db, db, file);
	}
		
	public void write(ActDB db, Iterable<Act> acts, File file) throws IOException
	{
		FileOutputStream fos=new FileOutputStream(file);
		BufferedOutputStream b=new BufferedOutputStream(fos);
		PrintStream out=new PrintStream(b);
		
		// Write data!
		writeDelimited(db, acts, out);
		
		out.flush();
		out.close();
		b.close();
		fos.close();
	}

	
	void writeDelimited(ActDB db, PrintStream out)
	{		
		writeDelimited(db, db, out);
	}
	
	public void writeDelimited(ActDB db, Iterable<Act> acts, PrintWriter out)
	{		
		// Write headers		
		List<String> names=new ArrayList<String>();
		List<Field> fields=db.getFields();
		for (Field f: fields)
			names.add(f.getName());
		print(names, out);
		
		// Write data
		for (Act a: acts)
		{
			List<String> data=new ArrayList<String>();
			for (Field f: fields)
				data.add(format(a.get(f)));
			print(data, out);
		}
	}

	
	public void writeDelimited(ActDB db, Iterable<Act> acts, PrintStream out)
	{		
		// Write headers		
		List<String> names=new ArrayList<String>();
		List<Field> fields=db.getFields();
		for (Field f: fields)
			names.add(f.getName());
		print(names, out);
		
		// Write data
		for (Act a: acts)
		{
			List<String> data=new ArrayList<String>();
			for (Field f: fields)
				data.add(format(a.get(f)));
			print(data, out);
		}
	}

	
	static String format(Object o)
	{
		if (o==null)
			return "";
		if (o instanceof String)
			return (String)o;
		if (o instanceof RoughTime)
			return ((RoughTime)o).format();
		if (o instanceof Number)
			return o.toString();
		if (o instanceof String[])
		{
			return writeArray((String[])o);
		}
		return o.toString();
	}
	
	public static String writeArray(Object[] s)
	{
        if (s==null || s.length==0)
        {
        	return "";
        }
        StringBuffer b=new StringBuffer();
        for (int i=0; i<s.length; i++)
        {
        	if (i>0)
        		b.append(",");
        	b.append(s[i]);
        	
        }
        return b.toString();
	}
	
	void print(List<String> list, PrintStream out)
	{
		out.println(delimitedText.write((String[])list.toArray(new String[0])));
	}
	
	void print(List<String> list, PrintWriter out)
	{
		out.println(delimitedText.write((String[])list.toArray(new String[0])));
	}

}

