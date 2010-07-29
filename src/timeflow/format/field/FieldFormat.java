package timeflow.format.field;

import java.net.URL;

import timeflow.data.time.*;

public abstract class FieldFormat {	
	protected String lastInput;
	protected Object lastValue;
	protected boolean understood=true;
	
	double value;
	
	void add(double x)
	{
		value+=x;
	}
	
	void note(String s)
	{
		add(scoreFormatMatch(s));
	}

	
	protected abstract Object _parse(String s) throws Exception;
	public abstract String format(Object o);
	public abstract Class getType();
	public abstract double scoreFormatMatch(String s);
	public abstract String getHumanName();


	public void setValue(Object o)
	{
		lastValue=o;
		lastInput=o==null ? "" : format(o);
	}
	
	public Object parse(String s) throws Exception
	{
		lastInput=s;
		lastValue=null;
		understood=false;
		lastValue=_parse(s);
		understood=true;
		return lastValue;
	}
	
	public Object getLastValue()
	{
		return lastValue;
	}
	
	public String feedback() 
	{
		if (!understood)
			return "Couldn't understand";
		return lastValue==null ? "(missing)" : "Read: "+format(lastValue);
	}
	
	public boolean isUnderstood()
	{
		return understood;
	}
}
