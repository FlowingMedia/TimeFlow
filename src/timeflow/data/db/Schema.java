package timeflow.data.db;

import java.util.*;


// methods are public for testing purposes.
public class Schema implements Iterable<Field> {
	
	private Map<String, Field> schema=new HashMap<String, Field>();
	private List<Field> fieldList=new ArrayList<Field>(); // so we preserve field order.
	
	public Iterator<Field> iterator()
	{
		return fieldList.iterator();
	}
	
    public Field getField(String key)
	{
		return schema.get(key);
	}
    
    public List<String> getKeys()
    {
    	return new ArrayList(schema.keySet());
    }
	
	public List<Field> getFields(Class type)
	{
		List<Field> a=new ArrayList<Field>();
		for (Field s: fieldList)
			if (type==null || s.getType()==type)
				a.add(s);
		return a;
	}
	
	public List<Field> getFields()
	{
		return getFields(null);
	}
	
	// not sure this actually works! removing things while iterating? to-do: test!
	public void delete(Field field)
	{
		if (schema.get(field.getName())==null)
			throw new IllegalArgumentException("No field exists: "+field);
		
		Set<String> keys=new HashSet<String>(schema.keySet());
		for (String s: keys)
		{
			Field f=schema.get(s);
			if (f==field)
			{
				schema.remove(s);
			}
		}
		
		fieldList.remove(field);
	}
	

	public void addAlias(Field field, String name)
	{
		if (field==null)
		{
			schema.remove(name);
			return;
		}
		if (!schema.values().contains(field))
			throw new IllegalArgumentException("Field does not exist in schema: "+field);
		schema.put(name, field);
	}
	
	public Field add(String name, Class type)
	{
		return add(new Field(name, type));
	}
	
	public Field add(Field field)
	{
		if (schema.get(field.getName())!=null)
			throw new IllegalArgumentException("Schema already has field named '"+field.getName()+
					"', type="+field.getType());
		schema.put(field.getName(), field);
		fieldList.add(field);
		return field;
	}
	
	public void setNewFieldOrder(List<Field> newOrder)
	{
		// first, we go through and check that this really is a new ordering!
		if (newOrder.size()!=fieldList.size())
			throw new IllegalArgumentException("Field lists have different sizes");
		for (Field f: newOrder)
			if (!fieldList.contains(f))
				throw new IllegalArgumentException("New field list has unexpected field: "+f);
		fieldList=newOrder;
	}
	
	public void print()
	{
		System.out.println(schema);
	}
	
	public void renameField(Field field, String name)
	{
		Field old=schema.get(name);
		if (old!=null && old!=field)
			throw new IllegalArgumentException("Can't rename a field to a name that already exists: "+name);
		schema.remove(field);
		field.setName(name);
		schema.put(name, field);
	}
}
