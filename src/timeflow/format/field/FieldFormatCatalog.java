package timeflow.format.field;

import timeflow.data.time.*;
import timeflow.util.*;

import java.net.URL;
import java.util.*;

public class FieldFormatCatalog {

	private static Map<String, FieldFormat> formatTable=new HashMap<String, FieldFormat>();
	private static Map<Class, FieldFormat> classTable=new HashMap<Class, FieldFormat>();

	static
	{
		for (FieldFormat f: listFormats())
		{
			formatTable.put(f.getHumanName(), f);
			classTable.put(f.getType(), f);
		}
	}
	
	static FieldFormat[] listFormats()
	{
		return new FieldFormat[] {new FormatDateTime(), new FormatString(),
				new FormatStringArray(), new FormatDouble(), new FormatURL()};
	}
	
	public static Iterable<String> classNames()
	{
		return formatTable.keySet();
	}
	
	public static String humanName(Class c){
		return getFormat(c).getHumanName();
	}
	

	public static FieldFormat getFormat(Class c) {
		FieldFormat f= classTable.get(c);
		if (f==null)
			System.out.println("Warning: no FieldFormat for "+c);
		return f;
	}

	
	public static Class javaClass(String humanName)
	{
		Class  c=formatTable.get(humanName).getType();
		if (c==null)
			System.out.println("Warning: no class for "+humanName);
		return c;
	}
}
