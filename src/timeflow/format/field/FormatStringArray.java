/**
 * 
 */
package timeflow.format.field;

import timeflow.model.Display;

public class FormatStringArray extends FieldFormat
{
	@Override
	public String format(Object o) {
		return Display.arrayToString((String[])o);
	}

	@Override
	public Object _parse(String s) {
		return parseList(s);
	}	
	
	public static String[] parseList(String s)
	{
		String[] t= s.length()==0 ? new String[0] : s.split(",");
		for (int i=0; i<t.length; i++)
			t[i]=t[i].trim();
		return t;
	}
	
	public String feedback() 
	{
		if (lastValue==null)
			return "Couldn't understand";
		String[] s=(String[])lastValue;
		if (s.length==0)
			return "Empty list";
		if (s.length==1)
			return "One item";
		return s.length+" items";
	}

	@Override
	public Class getType() {
		return String[].class;
	}	

	@Override
	public double scoreFormatMatch(String s) {
		double commas=-1;
		for (int i=s.length()-1; i>=0; i--)
			if (s.charAt(i)==',')
				commas++;
		return commas/s.length();
	}

	@Override
	public String getHumanName() {
		return "List";
	}

}