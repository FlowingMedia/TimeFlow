/**
 * 
 */
package timeflow.format.field;

public class FormatDouble extends FieldFormat
{	
	@Override
	public String format(Object o) {
		return o.toString();
	}

	@Override
	public Object _parse(String s) {
		if (s==null || s.trim().length()==0)
			return null;
		return parseDouble(s);
	}
	
	public static double parseDouble(String s)
	{
		int n=s.length();
		if (n>3)
		{
			if (s.charAt(0)=='(' && s.charAt(n-1)==')')
			{
				s='-'+s.substring(1,n-1);
				n--;
			}
		}
		// remove $,%, etc.
		StringBuffer b=new StringBuffer();
		for (int i=0; i<n; i++)
		{
			char c=s.charAt(i);
			if (Character.isDigit(c) || c=='-' || c=='.')
				b.append(c);
		}
		
		try
		{
		return Double.parseDouble(b.toString());
		}
		catch (RuntimeException e)
		{
			System.out.println("b="+b);
			throw e;
		}
	}


	@Override
	public Class getType() {
		return Double.class;
	}	
	@Override
	public double scoreFormatMatch(String s) {
		s=s.trim();
		int n=s.length();
		if (n==5) // possible zip code
		{
			if (s.charAt(0)=='0')
				return -3; // gotta be a zip code!
			return 0;
		}
		if (n==9) // possible zip+4, but really, who knows...
			return 0;
		
		if (n==4) // possible date.
		{
			try
			{
				int x=Integer.parseInt(s);
				if (x>1900 && x<2030)
					return -1; // that's very likely a date.
				if (x>1700 && x<2100)
					return 0; // you don't know.
				
			}
			catch (Exception e) {} // evidently not a date :-)
		}
		
		if (n==0)
			return -.1;
		int ok=0;
		int bad=0;
		for (int i=0; i<n; i++)
		{
			char c=s.charAt(i);
			if (Character.isDigit(c) || c=='.' || c==',' || c=='-' || c=='$' || c=='%')
				ok++;
			else
				bad++;
		}
		return 4-5*bad;
	}

	@Override
	public String getHumanName() {
		return "Number";
	}

}