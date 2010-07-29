package timeflow.data.db;

import java.net.URL;
import java.util.*;

import timeflow.data.db.filter.*;
import timeflow.data.time.*;

public class ArrayDB implements ActDB {
	
	private Schema schema;
	private List<Act> data=new ArrayList<Act>();
	private Field[] fields;
	private String source="[unknown]";
	private String description="";
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	@Override
	public void setAlias(Field field, String name) {
		schema.addAlias(field,name);
	}

	
	public ArrayDB(String[] fieldNames, Class[] types, String source)
	{
		this.schema=new Schema();
		this.source=source;
		int n=fieldNames.length;
		fields=new Field[n];
		for (int i=0; i<n; i++)
		{
			fields[i]=schema.add(fieldNames[i], types[i]);
			fields[i].index=i;
		}
	}
	
	public Field[] getFieldArray()
	{
		return fields;
	}
	
	@Override
	public Field addField(String name, Class type) {
		
		int n=fields.length;

		// make new Field.
		Field field=new Field(name, type);
		field.index=n;
		
		// make new array of fields.
		Field[] moreFields=new Field[n+1];
		System.arraycopy(fields, 0, moreFields, 0, n);
		moreFields[n]=field;
		this.fields=moreFields;
		
		// go through all the data items and expand their arrays, too.
		for (Act d: data)
		{
			IndexedAct item=(IndexedAct)d;
			Object[] old=item.data;
			item.data=new Object[n+1];
			System.arraycopy(old,0,item.data,0,n);
		}
		
		//System.out.println("Field added: "+field);
		schema.add(field);
		
		return field;
	}
	
	public Field getField(String name)
	{
		return schema.getField(name);
	}

	@Override
	public ActList all() {
		return select(null);
	}

	@Override
	public Act createAct() {
		IndexedAct act=new IndexedAct(this, fields.length);
		data.add(act);
		return act;
	}

	@Override
	public void delete(Act act) {
		data.remove(act);
	}

	@Override
	public void deleteField(Field deadField) {

		System.out.println("Deleting: "+deadField);
		
		schema.delete(deadField);
		int n=fields.length;
		int m=deadField.index;
		
		// make new array of fields.
		Field[] fewerFields=new Field[n-1];
		removeItem(fields, fewerFields, m);
		fields=fewerFields;
		
		// go through all the data items and contract their arrays, too.
		for (Act d: data)
		{
			IndexedAct item=(IndexedAct)d;
			Object[] old=item.data;
			item.data=new Object[n-1];
			removeItem(old,item.data,m);
		}
		
		// change field indices
		for (int i=0; i<fields.length; i++)
		{
			System.out.println("fields["+i+"]="+fields[i]);
			if (fields[i].index>deadField.index)
				fields[i].index--;
		}
	}
	
	private static void removeItem(Object[] a, Object[] b, int m)
	{
		int n=a.length;
		if (m>0)
			System.arraycopy(a,0,b,0,m);
		if (m<n-1)
			System.arraycopy(a,m+1, b, m,n-m-1);
	}

	@Override
	public List<Field> getFields(Class type) {
		return schema.getFields(type);
	}

	@Override
	public ActList select(ActFilter filter) {
		ActList set=new ActList(this);
		for (Act a: data)
			if (filter==null || filter.accept(a))
				set.add(a);
		return set;
	}

	@Override
	public List<Field> getFields() {
		return schema.getFields();
	}

	@Override
	public Act get(int i) {
		return data.get(i);
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public Iterator<Act> iterator() {
		return data.iterator();
	}
	
	@Override
	public List<String> getFieldKeys() {
		return schema.getKeys();
	}
	
	

	@Override
	public void setNewFieldOrder(List<Field> newOrder) {
		schema.setNewFieldOrder(newOrder);
	}

	
	class IndexedAct implements Act {
		
		Object[] data;
		ActDB db;
		
		IndexedAct(ActDB db, int numFields)
		{
			this.db=db;
			data=new Object[numFields];
		}

		@Override
		public String getString(Field field) {
			return (String)data[field.index];
		}
		
		public void setText(Field field, String text)
		{
			data[field.index]=text;
		}

		@Override
		public String[] getTextList(Field field) {
			return (String[])data[field.index];
		}
		
		public void setTextList(Field field, String[] list){
			data[field.index]=list;
		}

		@Override
		public double getValue(Field field) {
			Double d=(Double)data[field.index];
			return d==null ? Double.NaN : d.doubleValue();
		}
		
		public void setValue(Field field, double value)
		{
			data[field.index]=value;
		}

		@Override
		public Object get(Field field) {
			return data[field.index];
		}

		@Override
		public ActDB getDB() {
			return db;
		}

		@Override
		public void set(Field field, Object value) {
			data[field.index]=value;
		}

		@Override
		public RoughTime getTime(Field field) {
			return (RoughTime)data[field.index];
		}

		@Override
		public void setTime(Field field, RoughTime time) {
			data[field.index]=time;
		}

		@Override
		public URL getURL(Field field) {
			return (URL)data[field.index];
		}

		@Override
		public void setURL(Field field, URL url) {
			data[field.index]=url;
		}

	}

	@Override
	public void renameField(Field field, String name) {
		schema.renameField(field, name);
	}

}
