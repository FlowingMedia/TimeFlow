package timeflow.format.field;

import timeflow.data.time.*;

import java.util.*;
import java.text.*;


// Guesses date format and returns result
public class DateTimeParser {
	
	private DateFormat format;
	private TimeUnit units;
	private String pattern;

	public DateTimeParser(String pattern, TimeUnit units)
	{
		this.pattern=pattern;
		format=new SimpleDateFormat(pattern);
		this.units=units;
	}
	
	public RoughTime parse(String s) throws ParseException
	{
		RoughTime f= new RoughTime(format.parse(s).getTime(), units);
		return f;
	}
	
	public TimeUnit getUnits()
	{
		return units;
	}
	
	public String toString()
	{
		return "DateParser: pattern="+pattern+", units="+units;
	}
	
}
