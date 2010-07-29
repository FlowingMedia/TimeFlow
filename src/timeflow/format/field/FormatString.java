/**
 * 
 */
package timeflow.format.field;

public class FormatString extends FieldFormat
{	
	@Override
	public String format(Object o) {
		return o.toString();
	}

	@Override
	public Object _parse(String s) {
		return s;
	}		
	
	
	public String feedback() 
	{
		if (lastValue==null)
			return "Couldn't understand";
		if (((String)lastValue).length()==0)
			return "Blank";
		return "";
	}

	@Override
	public Class getType() {
		return String.class;
	}
	
	@Override
	public double scoreFormatMatch(String s) {
		return s!=null && s.length()>0 ? .1 : 0;
	}

	@Override
	public String getHumanName() {
		return "Text";
	}


}