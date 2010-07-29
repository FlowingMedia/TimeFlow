package timeflow.model;

import java.util.*;

public class VirtualField {
	public static final String LABEL="TIMEFLOW_LABEL";
	public static final String COLOR="TIMEFLOW_COLOR";
	public static final String SIZE="TIMEFLOW_SIZE";
	public static final String TRACK="TIMEFLOW_TRACK";
	public static final String START="TIMEFLOW_START";
	public static final String LATEST_START="TIMEFLOW_LATEST_START";
	public static final String END="TIMEFLOW_END";
	public static final String EARLIEST_END="TIMEFLOW_EARLIEST_END";
	
	private static HashMap<String, String> humanNames=new HashMap<String, String>();
	private static void tie(String a, String b) {humanNames.put(a,b);}
	
	static
	{
		tie(LABEL, "Label");
		tie(COLOR, "Color");
		tie(SIZE, "Size");
		tie(TRACK, "Track");
		tie(START, "Start");
		tie(LATEST_START, "Latest Start");
		tie(END, "End");
		tie(EARLIEST_END, "Earliest End");
	}
	
	public static String humanName(String s)
	{
		return humanNames.get(s);
	}
	
	public static Iterable<String> list()
	{
		return humanNames.keySet();
	}
}
