/**
 * 
 */
package timeflow.format.field;

import java.net.URL;

public class FormatURL extends FieldFormat
{	
	@Override
	public String format(Object o) {
		return o.toString();
	}

	@Override
	public Object _parse(String s) throws Exception {
		if (s.length()==0)
			return null;
		return new URL(s);
	}

	@Override
	public Class getType() {
		return URL.class;
	}	
	
	@Override
	public double scoreFormatMatch(String s) {
		if (s==null || s.length()==0)
			return 0;
		if (s.startsWith("http") || s.startsWith("file://"))
			return 5;
		return -1;
	}

	@Override
	public String getHumanName() {
		return "URL";
	}

}