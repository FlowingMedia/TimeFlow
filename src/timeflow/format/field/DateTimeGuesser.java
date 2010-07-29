package timeflow.format.field;

import java.text.*;
import java.util.*;

import timeflow.data.time.*;

public class DateTimeGuesser {
	
	private DateTimeParser lastGoodFormat;
	
	private static List<DateTimeParser> parsers=new ArrayList<DateTimeParser>();

	/*
	 
	HANDY REFERENCE FOR SIMPLEDATEFORMAT:
	(quoted from Java documentation)
		 
	Letter	Date or Time Component	Presentation	Examples
	G	Era designator	Text	AD
	y	Year	Year	1996; 96
	M	Month in year	Month	July; Jul; 07
	w	Week in year	Number	27
	W	Week in month	Number	2
	D	Day in year	Number	189
	d	Day in month	Number	10
	F	Day of week in month	Number	2
	E	Day in week	Text	Tuesday; Tue
	a	Am/pm marker	Text	PM
	H	Hour in day (0-23)	Number	0
	k	Hour in day (1-24)	Number	24
	K	Hour in am/pm (0-11)	Number	0
	h	Hour in am/pm (1-12)	Number	12
	m	Minute in hour	Number	30
	s	Second in minute	Number	55
	S	Millisecond	Number	978
	z	Time zone	General time zone	Pacific Standard Time; PST; GMT-08:00
	Z	Time zone	RFC 822 time zone	-0800
		 
		 */
		
	
	// the order of the list below matters--better not to put the year-only ones at the top,
	// because then the guesser succeeds before it has a chance to try parsing days.
	static
	{
		parsers.add(new DateTimeParser("yyyy-MM-ddzzzzzzzzzz", TimeUnit.DAY));
		parsers.add(new DateTimeParser("MMM dd yyyy HH:mm", TimeUnit.SECOND));
		parsers.add(new DateTimeParser("MMM/dd/yyyy HH:mm", TimeUnit.SECOND));
		parsers.add(new DateTimeParser("MM/dd/yy HH:mm", TimeUnit.SECOND));
		parsers.add(new DateTimeParser("MMM dd yyyy HH:mm:ss", TimeUnit.SECOND));
		parsers.add(new DateTimeParser("MM/dd/yyyy HH:mm:ss", TimeUnit.SECOND));
		parsers.add(new DateTimeParser("MMM dd yyyy HH:mm:ss zzzzzzzz", TimeUnit.SECOND));
		parsers.add(new DateTimeParser("EEE MMM dd HH:mm:ss zzzzzzzz yyyy", TimeUnit.SECOND));
		parsers.add(new DateTimeParser("EEE MMM dd HH:mm:ss zzzzzzzz yyyy", TimeUnit.SECOND));
		parsers.add(new DateTimeParser("MM-dd-yyyy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("yyyy-MM-dd", TimeUnit.DAY));
		parsers.add(new DateTimeParser("yyyyMMdd", TimeUnit.DAY));
		parsers.add(new DateTimeParser("MM-dd-yy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("dd-MMM-yy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("MM-dd-yyyy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("MM/dd/yy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("MM/dd/yyyy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("dd MMM yyyy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("dd MMM, yyyy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("MMM dd yyyy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("MMM dd, yyyy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("EEE MMM dd zzzzzzzz yyyy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("EEE MMM dd yyyy", TimeUnit.DAY));
		parsers.add(new DateTimeParser("MMM-yy", TimeUnit.MONTH));
		parsers.add(new DateTimeParser("MMM yy", TimeUnit.MONTH));
		parsers.add(new DateTimeParser("MMM/yy", TimeUnit.MONTH));
		parsers.add(new DateTimeParser("yyyy", TimeUnit.YEAR));
		parsers.add(new DateTimeParser("yyyy GG", TimeUnit.YEAR));
	}
	
	public DateTimeParser getLastGoodFormat()
	{
		return lastGoodFormat;
	}

	public RoughTime guess(String s)
	{
		// old code for trying the last good parser.
		// we took this out because if the last good one was for a single year,
		// but a new one is for years and days,
		// all you get is the year.
		
		//if (lastGoodFormat!=null)
		//try { return lastGoodFormat.parse(s); }
		//catch (ParseException e) {}	
		if (s==null || s.trim().length()==0)
			return null;
		for (DateTimeParser d: parsers)
		{
			try
			{
				RoughTime date= d.parse(s);
				lastGoodFormat=d;
				return date;
			}
			catch (ParseException e) {}
		}
		throw new IllegalArgumentException("Couldn't guess date: '"+s+"'");
	}
	
	public static void main(String[] args)
	{
		DateTimeGuesser g=new DateTimeGuesser();
		System.out.println(g.guess("2009-03-04"));
		System.out.println(g.guess("June 10, 2010"));
		System.out.println(g.guess("2010"));
		System.out.println(g.guess("3/17/10"));
	}
}
