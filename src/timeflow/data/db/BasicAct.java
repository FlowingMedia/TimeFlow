package timeflow.data.db;

import timeflow.data.db.*;
import timeflow.data.time.*;

import java.net.URL;
import java.util.*;

public class BasicAct implements Act {
	
	private HashMap data=new HashMap();
	private ActDB db;
	
	public BasicAct(ActDB db)
	{
		this.db=db;
	}
	

	@Override
	public String getString(Field field) {
		return (String)data.get(field.getName());
	}
	
	public void setText(Field field, String text)
	{
		data.put(field.getName(), text);
	}

	@Override
	public String[] getTextList(Field field) {
		return (String[])data.get(field.getName());
	}
	
	public void setTextList(Field field, String[] list){
		data.put(field.getName(), list);
	}

	@Override
	public double getValue(Field field) {
		return (Double)data.get(field.getName());
	}
	
	public void setValue(Field field, double value)
	{
		data.put(field.getName(), value);
	}

	@Override
	public Object get(Field field) {
		return data.get(field.getName());
	}

	@Override
	public ActDB getDB() {
		return db;
	}

	@Override
	public void set(Field field, Object value) {
		data.put(field.getName(), value);
	}


	@Override
	public RoughTime getTime(Field field) {
		return (RoughTime)data.get(field.getName());
	}


	@Override
	public void setTime(Field field, RoughTime time) {
		data.put(field.getName(), time);
		
	}


	@Override
	public URL getURL(Field field) {
		return (URL)data.get(field.getName());
	}


	@Override
	public void setURL(Field field, URL url) {
		data.put(field.getName(), url);
	}

}
