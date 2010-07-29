package timeflow.data.db;

import timeflow.data.time.*;

import java.net.URL;
import java.util.*;

public interface Act {
	
	public ActDB getDB();
	
	public Object get(Field field);
	public double getValue(Field field);
	public String getString(Field field);
	public String[] getTextList(Field field);
	public RoughTime getTime(Field field);
	public URL getURL(Field field);
	
	public void set(Field field, Object value);
	public void setText(Field field, String text);
	public void setTextList(Field field, String[] list);
	public void setValue(Field field, double value);
	public void setTime(Field field, RoughTime time);
	public void setURL(Field field, URL url);
}
